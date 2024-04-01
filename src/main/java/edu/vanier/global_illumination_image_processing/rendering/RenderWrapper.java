package edu.vanier.global_illumination_image_processing.rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

/**
 * MultiThreaded rendering class
 * Uses composition of the RenderingEquation class
 * 
 * @author William Carbonneau
 */
public class RenderWrapper {
    /** width and height of render image/camera in pixels, cannot be static for dynamic adjustment */
    private int width, height;
    /** The Scene to render */
    private Scene scene;
    /** Samples per pixel */
    private double SPP;
    /** thread count of previous render */
    private int threadCount = 0;
    /** processors to be spared during the render */
    private int sparedProcessors = 1;
    
    /** lost of array references */
    ArrayList<DiffuseColor[][]> imagePieces = new ArrayList<>();

    /**
     * Constructor for all components
     * 
     * @param width int
     * @param height int
     * @param scene Scene
     * @param SPP double
     */
    public RenderWrapper(int width, int height, Scene scene, double SPP) {
        this.width = width;
        this.height = height;
        this.scene = scene;
        this.SPP = SPP;
    }

    /**
    * Get the width of the image.
    * 
    * @return The width of the image.
    */
    public int getWidth() {
        return width;
    }

    /**
    * Get the height of the image.
    * 
    * @return The height of the image.
    */
    public int getHeight() {
        return height;
    }

    /**
    * Get the scene associated with the render.
    * 
    * @return The scene associated with the render.
    */
    public Scene getScene() {
        return scene;
    }

    /**
    * Get the samples per pixel (SPP) value.
    * 
    * @return The samples per pixel (SPP) value.
    */
    public double getSPP() {
        return SPP;
    }

    /**
    * Set the width of the image.
    * 
    * @param width The width of the image.
    */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
    * Set the height of the image.
    * 
    * @param height The height of the image.
    */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
    * Set the scene associated with the render.
    * 
    * @param scene The scene associated with the render.
    */
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
    * Set the samples per pixel (SPP) value.
    * 
    * @param SPP The samples per pixel (SPP) value.
    */
    public void setSPP(double SPP) {
        this.SPP = SPP;
    }

    /** 
     * get processors requested to not be used by user
     * 
     * @return int spared processors  
     */
    public int getSparedProcessors() {
        return sparedProcessors;
    }
    
    /** 
     * Set processors requested to not be used by user
     * 
     * @param sparedProcessors int spared processors  
     */
    public void setSparedProcessors(int sparedProcessors) {
        this.sparedProcessors = sparedProcessors;
    }

    
    
    
    /**
     * Render the scene MultiThreaded
     * 
     * @param multithread boolean MultiThread yes/no (true/false)
     * @param stratified boolean stratified sampling yes/no (true/false)
     * @param threads threads requested to use, <= 0 for maximum
     */
    public void render(boolean multithread, boolean stratified, int threads) {
        System.out.printf("Rendering: %2.0f samples%n", SPP);
        // start a clock
        final long startTime = System.currentTimeMillis();
        
        // deal with many processor types - get maximum available threads 
        // TODO make spared processors modifiable (requires error handling)
        int maxThreads = Runtime.getRuntime().availableProcessors() - sparedProcessors; // use max threads-1 to leave a thread for other processes (including the app itself)
        // handle maxThreads or 1
        maxThreads = Integer.max(maxThreads, 1);
        
        final int threadCountLocal;
        
        // if the threads requested exceeds the maximum available (-1), or it is 0, provide maximum available
        if (threads > maxThreads || threads <= 0) threadCountLocal = (multithread && maxThreads > 1) ? maxThreads : 1;
        else threadCountLocal = threads;

        // update previous threadcount
        this.threadCount = threadCountLocal;
        
        // instantiate sample count as final for read stability during threading
        final double samples = this.SPP;
        
        // clear the cache of arrays
        imagePieces.clear();
        
        final int pieceSize = (int) height/threadCountLocal;
        final int lastPieceSize = height-(pieceSize*threadCountLocal) + pieceSize;
        
        for (int thread = 0 ; thread < threadCountLocal; thread++) {
            DiffuseColor[][] temp = new DiffuseColor[width][((thread != threadCountLocal-1) ? pieceSize : lastPieceSize)];
            imagePieces.add(temp);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < ((thread != threadCountLocal-1) ? pieceSize : lastPieceSize); j++) {
                    temp[i][j] = new DiffuseColor();
                }
            }
        }
        
        System.out.println("Start Multithreading with "+threadCount+" threads");
        
         // create thread-pool
        ExecutorService execServ = Executors.newFixedThreadPool(threadCountLocal);
        ArrayList<CompletableFuture<Void>> tasks = new ArrayList<>(threadCountLocal);
        
        for (int rowPiece = 0; rowPiece < threadCountLocal; rowPiece++) {
            // create array managers
            final int rowPieceFinal = rowPiece;
            final int myStart = rowPiece*pieceSize;
            final int myEnd = myStart+((rowPiece != threadCountLocal-1) ? pieceSize : lastPieceSize);
            // instantiate renderer
            final RenderingEquation renderer = new RenderingEquation(width, height, scene);
            
            // create thread
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
            System.out.println("Start executor "+rowPieceFinal);
            
            // loop sampler
            for (int row = myStart; row < myEnd; row++) {
                for (int column = 0; column < width; column++) {
                    renderer.simulatePerPixel(column, row, row - myStart, samples, renderer.halton1, renderer.halton2, scene, imagePieces.get(rowPieceFinal), stratified);
                }
            }
                System.out.println("Finish executor "+rowPieceFinal);
            }, execServ);
            // add thread to list of threads
            tasks.add(task);
        }
        // collect threads and complete them.
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        // those who live by the thread die by the thread
        execServ.shutdown();   
        
        System.out.println("Render finished");
        System.out.println("Final time (milliseconnds): " + (System.currentTimeMillis() - startTime));
    }
    
    /** 
     * Save the image to a .bmp file 
     * 
     * @return int 1 if error. 0 if success
     */
    public int save() {
        System.out.println("Saving...");
        // check if valid
        if (this.imagePieces.isEmpty() || this.threadCount == 0) {
            return 1;
        }
        
        System.out.println(threadCount);
        
        final int pieceSize = Integer.max((int) height/threadCount,1);
        final int lastPieceSize = height-(pieceSize*threadCount) + pieceSize;
        
        // create buffered image
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        String name = "ray";
        
        File saveFile = new File(name+".bmp");
        
        for (int i = 0; i < threadCount; i++) {
            
            int myStart = i*pieceSize;
            int myEnd = myStart+((i != threadCount-1) ? pieceSize : lastPieceSize);
            
            for (int row = 0; row < myEnd-myStart; row++) {
                for (int column = 0; column < width; column++) {
                    output.setRGB(column, row+myStart, new Color(Integer.min((int)imagePieces.get(i)[column][row].getR(),255), Integer.min((int)imagePieces.get(i)[column][row].getG(),255), Integer.min((int)imagePieces.get(i)[column][row].getB(),255)).getRGB());
                }
            }
        }
        
        try {
            ImageIO.write(output, "bmp", saveFile);
        }catch(IOException e) {
            e.printStackTrace(); // TODO deal with error
            return 1;
        }
        System.out.println("Save completed as: " + name + ".bmp");
        return 0;
    }
}
