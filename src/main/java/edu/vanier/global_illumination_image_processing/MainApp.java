package edu.vanier.global_illumination_image_processing;

import edu.vanier.global_illumination_image_processing.controllers.FXMLTitleSceneController;
import java.io.IOException;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a JavaFX project template to be used for creating GUI applications.
 * The JavaFX GUI framework (version: 20.0.2) is linked to this project in the
 * build.gradle file.
 * Modified by Zachary Tremblay for use in initializing this javaFX application
 * @link: https://openjfx.io/javadoc/20/
 * @see: /Build Scripts/build.gradle
 * @author Sleiman Rabah.
 */
public class MainApp extends Application {

    public static final String FXMLTitleScene = "FXMLTitleScene";
    public static final String FXMLConvolutionsScene = "FXMLConvolutionsScene";
    public static final String FXMLRenderScene = "FXMLRenderScene";
    private static Scene scene;
    private final static Logger logger = LoggerFactory.getLogger(MainApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Bootstrapping the application...");
            //-- 1) Load the scene graph from the specified FXML file and 
            // associate it with its FXML controller.
            
            Parent root = loadFXML(FXMLTitleScene, new FXMLTitleSceneController(primaryStage));
            scene = new Scene(root, 1000, 700);
            
            //Image Rendering
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLTitleScene.fxml"));
            //loader.setController(new FXMLTitleSceneController(primaryStage));
            
            //Convolution
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLConvolutionsScene.fxml"));
            //loader.setController(new FXMLConvolutionsSceneController(primaryStage));
            
            //Pane root = loader.load();
            
            //-- 2) Create and set the scene to the stage.
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            // We just need to bring the main window to front.
            primaryStage.setAlwaysOnTop(true);            
            primaryStage.show();
            primaryStage.setAlwaysOnTop(false);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    /**
     * Changes the primary stage's current scene.
     *
     * @param fxmlFile The name of the FXML file to be loaded.
     * @param fxmlController An instance of the FXML controller to be associated
     * with the loaded FXML scene graph.
     */
    public static void switchScene(String fxmlFile, Object fxmlController) {
        try {
            scene.setRoot(loadFXML(fxmlFile, fxmlController));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Loads a scene graph from an FXML file.
     *
     * @param fxmlFile The name of the FXML file to be loaded.
     * @param fxmlController An instance of the FXML controller to be associated
     * with the loaded FXML scene graph.
     * @return The root node of the loaded scene graph.
     * @throws IOException
     */
    public static Parent loadFXML(String fxmlFile, Object fxmlController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/" + fxmlFile + ".fxml"));
        fxmlLoader.setController(fxmlController);
        return fxmlLoader.load();
    }

    /**
     * Start the app - main entry
     * 
     * @param args 
     */
    public static void main(String[] args) {
        launch(args);
    }
}
