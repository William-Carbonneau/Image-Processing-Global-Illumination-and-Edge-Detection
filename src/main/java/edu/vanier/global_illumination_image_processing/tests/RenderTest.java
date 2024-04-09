package edu.vanier.global_illumination_image_processing.tests;

import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;
import edu.vanier.global_illumination_image_processing.rendering.RenderWrapper;
import edu.vanier.global_illumination_image_processing.rendering.Scene;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.objects.Plane;
import edu.vanier.global_illumination_image_processing.rendering.objects.Sphere;

/**
 * A test class for the rendering, test is qualitative
 * 
 * @author William Carbonneau
 */
public class RenderTest {
    public static void main(String[] args) {
        
        final Scene scene = new Scene();
        
        // add objects
        // Sphere: origin, radius, color, emission, type 
        scene.addObj("Metal sphere 1", new Sphere(new Vec3D(-0.75,-1.45,-4.4), 1.05, new DiffuseColor(4, 8, 4), 0,2));
        scene.addObj("Glass sphere 1", new Sphere(new Vec3D(2.0,-2.05,-3.7), 0.5, new DiffuseColor(10, 10, 1), 0,3));
        scene.addObj("Diffuse sphere 1", new Sphere(new Vec3D(-1.75,-1.95,-3.1), 0.6, new DiffuseColor(4, 4, 12), 0,1));
        // Plane: normal, distance from origin to normal, color, emission, type
        scene.addObj("bottom plane", new Plane(new Vec3D(0,1,0), 2.5, new DiffuseColor(6, 6, 6), 0,1)); // bottom plane
        scene.addObj("back plane", new Plane(new Vec3D(0,0,1), 5.5, new DiffuseColor(6, 6, 6), 0,1)); // back plane
        scene.addObj("left plane", new Plane(new Vec3D(1,0,0), 2.75, new DiffuseColor(10, 2, 2), 0,1)); // left plane
        scene.addObj("right plane", new Plane(new Vec3D(-1,0,0), 2.75, new DiffuseColor(2, 10, 2), 0,1)); // right plane
        scene.addObj("ceiling plane", new Plane(new Vec3D(0,-1,0), 3.0, new DiffuseColor(6, 6, 6), 0,1)); // ceiling plane
        scene.addObj("front plane", new Plane(new Vec3D(0,0,-1), 0.5, new DiffuseColor(6, 6, 6), 0,1)); // front plane
        // add lights - use color and emissiveness and diffuse material for diffuse radial light
        scene.addObj("light sphere 1", new Sphere(new Vec3D(0,1.9,-3), 0.5, new DiffuseColor(0, 0, 0), 10000,1));

        // modify objects in the scene, need to deal with possibility of null
        try {
            scene.getObjectByName("Glass sphere 1").setRefractiveIndex(1.5);
        }catch(NullPointerException e) {
            System.out.println("Object not found to modify");
        }

        RenderWrapper renderer = new RenderWrapper(800, 800, scene, 16.0);
        renderer.setSPP(16.0);
        renderer.render(true,true,0);
        renderer.save();
        
        /*
        // benchmark 10 threads 16 SPP
        /*
        long time1 = 0;
        long time2 = 0;
        long time3 = 0;
        
        long average  = 0;
        
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("bench.csv");
        } catch (IOException ex) {
            Logger.getLogger(RenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        
        printWriter.println(""+time1+","+time3+","+time2+","+average);
        
        for (int i = 1; i < 11; i++) {
            for (int times = 0; times < 3; times++) {
                switch (times) {
                    case 0 -> time1 = System.currentTimeMillis();
                    case 1 -> time2 = System.currentTimeMillis();
                    case 2 -> time3 = System.currentTimeMillis();
                }
                renderer.render(true,true,i);
                switch (times) {
                    case 0 -> time1 = System.currentTimeMillis()-time1;
                    case 1 -> time2 = System.currentTimeMillis()-time2;
                    case 2 -> time3 = System.currentTimeMillis()-time3;
                }
            }
            average = (time1 + time2 + time3)/3;                
            printWriter.println(""+time1+","+time3+","+time2+","+average);
        }
        printWriter.close();
*/
    }
}
