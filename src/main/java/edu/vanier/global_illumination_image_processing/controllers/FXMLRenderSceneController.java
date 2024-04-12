package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.MainApp;
import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;
import edu.vanier.global_illumination_image_processing.rendering.RenderWrapper;
import edu.vanier.global_illumination_image_processing.rendering.RenderScene;
import edu.vanier.global_illumination_image_processing.rendering.SceneObject;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.objects.Plane;
import edu.vanier.global_illumination_image_processing.rendering.objects.Sphere;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;

/**
 * The controller for the rendering scene
 * 
 * @author Zachary Tremblay and William Carbonneau
 */
public class FXMLRenderSceneController {
    // FXML objects
    @FXML VBox RootVBox;
    @FXML VBox vboxPropertyList;
    @FXML MenuItem menuItemBackToTitle;
    @FXML MenuItem menuItemAddSphere;
    @FXML MenuItem menuItemAddPlaneX;
    @FXML MenuItem menuItemAddPlaneY;
    @FXML MenuItem menuItemAddPlaneZ;
    @FXML MenuItem menuItemRemoveSelected;
    @FXML MenuItem menuItemAboutRendering;
    @FXML ListView listObjectList;
    @FXML Label lblObjectType;
    @FXML Label lblRightStatus;
    @FXML Label lblLeftStatus;
    @FXML TextField txtObjectName;
    @FXML TextField txtObjectXPos;
    @FXML TextField txtObjectYPos;
    @FXML TextField txtObjectZPos;
    @FXML TextField txtDTO;
    @FXML TextField txtRadius;
    @FXML TextField txtEmissiveness;
    @FXML TextField txtIOR;
    @FXML TextField txtThreads;
    @FXML TextField txtSPP;
    @FXML ChoiceBox choiceMaterial;
    @FXML ChoiceBox choiceEngine;
    @FXML CheckBox checkStratified;
    @FXML Button btnRender;
    @FXML Button btnObjDelete;
    @FXML ColorPicker clrObjPicker;
    @FXML HBox HboxDTO;
    @FXML HBox HboxRadius;
    @FXML ImageView imgResult;
    @FXML ScrollPane scrollImageHolder;
    @FXML StackPane stackImageHolder;
    Stage primaryStage;
    private boolean autoRender;
    
    /** create the render scene */
    private final RenderScene mainScene = new RenderScene();
    /** The renderer instance TODO modify width/height */
    private final RenderWrapper renderer = new RenderWrapper(800, 800, mainScene, 8);
    private int renderEngine = 0;
    private boolean stratified = true;

    // construct this controller with the primary stage
    public FXMLRenderSceneController(Stage primaryStage) {
        this.autoRender = false;
        this.primaryStage = primaryStage;
    }
    
    /** the regex expression to match any double number even negatives */
    public final String textFormatterDoubleRegex = "\\-?\\d+\\.?\\d*";
    public final String textFormatterIntegerRegex = "[1-9][0-9]*";
    
    public String doubleFormatterRemoveTrailingPeriod(String input) {
        if (input.endsWith(".")) return input.replace(".", "");
        return input;
    }
    
    /**
     * Recursive algorithm to get a unique name
     * 
     * @param key the string got be accessed
     * @param depth the recursion depth
     * @return String unique name
     */
    private String modifyKeyString(String key, int depth) {
        String temp = key;
        if (mainScene.getObjectByName(key) != null && key.charAt(key.length()-1) == ' ') {
            temp = modifyKeyString(key + (depth + 1), depth + 1);
        }else if (mainScene.getObjectByName(key) != null) {
            temp = modifyKeyString(key.substring(0, key.length()-1) + (depth + 1), depth + 1);
        }
        return temp;
    }
    
    /**
     * Render the image
     */
    private void render() {
        // render and return the time it took
        long time;
        System.out.println("Engine: "+renderEngine);
        time = renderer.render(true, stratified,renderEngine);
        lblRightStatus.setText("Time: "+time+" milliseconds");
        BufferedImage image = renderer.save();
        // output image converted from buferedimage to javafx image
        if (image != null) imgResult.setImage(SwingFXUtils.toFXImage(image, null));
    }
    
    public void initialize(){        
        // create list of elements - needs a new class wrapper
        ObservableList<ObjWrapper> objectList = FXCollections.observableArrayList();
        ObservableList<String> typeChoiceBoxList = FXCollections.observableArrayList("Diffuse","Reflective","Refractive");
        ObservableList<String> engineList = FXCollections.observableArrayList("Normal", "Banded", "Psycho", "Rasterized");
        listObjectList.setItems(objectList);
        choiceMaterial.setItems(typeChoiceBoxList);
        choiceEngine.setItems(engineList);
        choiceEngine.setValue(engineList.get(0));
        vboxPropertyList.getChildren().remove(HboxRadius);
        vboxPropertyList.getChildren().remove(HboxDTO);
        
        // default objects
        objectList.add(new ObjWrapper("Metal Sphere 1", new Sphere(new Vec3D(-0.75,-1.45,-4.4), 1.05, new DiffuseColor(4, 8, 4), 0,2)));
        objectList.add(new ObjWrapper("Glass sphere 1", new Sphere(new Vec3D(2.0,-2.05,-3.7), 0.5, new DiffuseColor(10, 10, 1), 0,3)));
        objectList.add(new ObjWrapper("Diffuse sphere 1", new Sphere(new Vec3D(-1.75,-1.95,-3.1), 0.6, new DiffuseColor(4, 4, 12), 0,1)));
        objectList.add(new ObjWrapper("bottom plane", new Plane(new Vec3D(0,1,0), 2.5, new DiffuseColor(4.5, 4.5, 4.5), 0,1)));
        objectList.add(new ObjWrapper("back plane", new Plane(new Vec3D(0,0,1), 5.5, new DiffuseColor(6, 6, 6), 0,1)));
        objectList.add(new ObjWrapper("left plane", new Plane(new Vec3D(1,0,0), 2.75, new DiffuseColor(10, 2, 2), 0,1)));
        objectList.add(new ObjWrapper("right plane", new Plane(new Vec3D(-1,0,0), 2.75, new DiffuseColor(2, 10, 2), 0,1)));
        objectList.add(new ObjWrapper("ceiling plane", new Plane(new Vec3D(0,-1,0), 3.0, new DiffuseColor(7, 7, 7), 0,1)));
        objectList.add(new ObjWrapper("front plane", new Plane(new Vec3D(0,0,-1), 0.5, new DiffuseColor(6, 6, 6), 0,1)));
        objectList.add(new ObjWrapper("light sphere 1", new Sphere(new Vec3D(0,1.9,-3), 0.5, new DiffuseColor(12, 12, 12), 10000,1))); 
        
        for (ObjWrapper obj: objectList) {
            mainScene.addObj(obj.getName(), obj.getObj());
        }
        
        /**
         * Set event handler to go back to main window
         */
        menuItemBackToTitle.setOnAction((event)->{
            
            MainApp.switchScene(MainApp.FXMLTitleScene, new FXMLTitleSceneController(primaryStage));
        });
        
        // bind image holder to center it using double binding to get value of viewport dimensions as function of viewport modified
        stackImageHolder.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        scrollImageHolder.getViewportBounds().getHeight(), scrollImageHolder.viewportBoundsProperty()));
        stackImageHolder.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        scrollImageHolder.getViewportBounds().getWidth(), scrollImageHolder.viewportBoundsProperty()));
        
        // set status text to none
        lblLeftStatus.setText("");
        lblRightStatus.setText("");
        
        /**
         * Add a new sphere to the scene
         */
        menuItemAddSphere.setOnAction((event) -> {
            ObjWrapper obj = new ObjWrapper(modifyKeyString("New Sphere ", 0), new Sphere(new Vec3D(0,0,-3), 1.0, new DiffuseColor(8, 0, 0), 0,1));
            objectList.add(obj);
            mainScene.addObj(obj.getName(), obj.getObj());
            if (autoRender) render();
        });
        
        /**
         * Add a new Plane facing the X direction to the Scene
         */
        menuItemAddPlaneX.setOnAction((event) -> {
            ObjWrapper obj = new ObjWrapper(modifyKeyString("New Plane X ", 0), new Plane(new Vec3D(1,0,0), 1, new DiffuseColor(0, 8, 0), 0,1));
            objectList.add(obj);
            mainScene.addObj(obj.getName(), obj.getObj());
            if (autoRender) render();
        });
        
        /**
         * Add a new Plane facing the Y direction to the Scene
         */
        menuItemAddPlaneY.setOnAction((event) -> {
            ObjWrapper obj = new ObjWrapper(modifyKeyString("New Plane Y ", 0), new Plane(new Vec3D(0,1,0), 1, new DiffuseColor(0, 0, 8), 0,1));
            objectList.add(obj);
            mainScene.addObj(obj.getName(), obj.getObj());
            if (autoRender) render();
        });
        
        /**
         * Add a new Plane facing the Z direction to the Scene
         */
        menuItemAddPlaneZ.setOnAction((event) -> {
            ObjWrapper obj = new ObjWrapper(modifyKeyString("New Plane Z ", 0), new Plane(new Vec3D(0,0,1), 3, new DiffuseColor(8, 0, 8), 0,1));
            objectList.add(obj);
            mainScene.addObj(obj.getName(), obj.getObj());
            if (autoRender) render();
        });
        
        /**
         * Remove the selected item from the scene
         */
        menuItemRemoveSelected.setOnAction((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            if (item == null) return;
            mainScene.removeObj(item.getName());
            objectList.remove(item);
            if (autoRender) render();
        });
        
        /**
         * Start the about page popup
         */
        menuItemAboutRendering.setOnAction((event) -> {
            
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLRenderAboutScene.fxml"));
                Pane root = loader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
                stage.setTitle("About Render");
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLRenderSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }    
        });
        
        /**
         * Remove the selected item from the scene
         */
        btnObjDelete.setOnAction((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            if (item == null) return;
            mainScene.removeObj(item.getName());
            objectList.remove(item);
            if (autoRender) render();
        });
        
        /**
         * Render the scene based on user-created parameters
         */
        btnRender.setOnAction((event) -> {
            // render and return the time it took
            render();
        });
        
        /**
         * On action, set the value of stratified to checkbox value 
         */
        checkStratified.setOnAction((event) -> {
            stratified = checkStratified.selectedProperty().get();
        });
        
        /**
         * Update the current SPP - only numbers allowed by textFormatter
         */
        txtSPP.setOnKeyTyped((event) -> {
            renderer.setSPP(Integer.parseInt(doubleFormatterRemoveTrailingPeriod(txtSPP.getText())));
        });
        // filters all incoming characters from getControlNewText() by the regex. Returns null new String is it does not match
        txtSPP.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterIntegerRegex) ? input : null));
        
        /**
         * Update the threads to be spared
         */
        txtThreads.setOnKeyTyped((event) -> {
            renderer.setThreadsRequested((int)Double.parseDouble(doubleFormatterRemoveTrailingPeriod(txtThreads.getText())));
        });
        // filters all incoming characters from getControlNewText() by the regex. Returns null new String is it does not match
        txtThreads.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterIntegerRegex) ? input : null));
        
        /**
         * Update view of current object from list
         */
        listObjectList.setOnMouseClicked((event) -> {
            // this will  alway be an ObjWrapper
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            txtObjectName.setText(item.getName());
            lblObjectType.setText("" + item.getObj().getClass().getSimpleName());
            txtObjectXPos.setText("" + item.getObj().getNormal().getX());
            txtObjectYPos.setText("" + item.getObj().getNormal().getY());
            txtObjectZPos.setText("" + item.getObj().getNormal().getZ());
            txtDTO.setText("" + item.getObj().getDistanceOrigin());
            txtRadius.setText("" + item.getObj().getRadius());
            txtIOR.setText("" + item.getObj().getRefractiveIndex());
            txtEmissiveness.setText("" + item.getObj().getEmission());
            clrObjPicker.setValue(new Color((float) item.getObj().getColor().getR()/12, (float) item.getObj().getColor().getG()/12, (float) item.getObj().getColor().getB()/12, 1.0));
            switch (item.getObj().getType()) {
                case 1 -> choiceMaterial.setValue(typeChoiceBoxList.get(0));
                case 2 -> choiceMaterial.setValue(typeChoiceBoxList.get(1));
                case 3 -> choiceMaterial.setValue(typeChoiceBoxList.get(2));
            }
            switch(item.getObj().getClass().getSimpleName()) {
                case "Plane" -> {
                    if (!vboxPropertyList.getChildren().contains(HboxDTO)) vboxPropertyList.getChildren().add(HboxDTO);
                    HboxDTO.setVisible(true);
                    HboxRadius.setVisible(false);
                    vboxPropertyList.getChildren().remove(HboxRadius);
                }
                case "Sphere" -> {
                    HboxDTO.setVisible(false);
                    vboxPropertyList.getChildren().remove(HboxDTO);
                    if (!vboxPropertyList.getChildren().contains(HboxRadius)) vboxPropertyList.getChildren().add(HboxRadius);
                    HboxRadius.setVisible(true);
                }
            }
        });
        
        /** Update the color of the current object */
        clrObjPicker.setOnAction((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            Color color = clrObjPicker.getValue();
            if (item == null) return;
            // update object in list
            item.getObj().setColor(new DiffuseColor(color.getRed()*12, color.getGreen()*12, color.getBlue()*12));
            if (autoRender) render();
        });
        
        /**
         * Update selected object's name
         */
        txtObjectName.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.setName(txtObjectName.getText());
        });
        
        /**
         * Update selected object's DTO - only numbers allowed by textFormatter
         */
        txtDTO.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().setDistanceOrigin(Double.parseDouble(doubleFormatterRemoveTrailingPeriod(txtDTO.getText())));
            if (autoRender) render();
        });
        txtDTO.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
        
        /**
         * Update selected object's radius - only numbers allowed by textFormatter
         */
        txtRadius.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().setRadius(Double.parseDouble(doubleFormatterRemoveTrailingPeriod(txtRadius.getText())));
            if (autoRender) render();
        });
        txtRadius.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
        
        /**
         * Update selected object's Emissiveness - only numbers allowed by textFormatter
         */
        txtEmissiveness.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().setEmission(Double.parseDouble(doubleFormatterRemoveTrailingPeriod(txtEmissiveness.getText())));
        });
        txtEmissiveness.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
        
        /**
         * Update selected object's IOR - only numbers allowed by textFormatter
         */
        txtIOR.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().setRefractiveIndex(Double.parseDouble(doubleFormatterRemoveTrailingPeriod(txtIOR.getText())));
        });
        txtIOR.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
        
        /**
         * Update selected object's X position - only numbers allowed by textFormatter
         */
        txtObjectXPos.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().getNormal().setX(Double.parseDouble(doubleFormatterRemoveTrailingPeriod(txtObjectXPos.getText())));
            if (autoRender) render();
        });
        txtObjectXPos.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
        
        /**
         * Update selected object's Y position - only numbers allowed by textFormatter
         */
        txtObjectYPos.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().getNormal().setY((Double.parseDouble(doubleFormatterRemoveTrailingPeriod(txtObjectYPos.getText()))));
            if (autoRender) render();
        });
        txtObjectYPos.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
        
        /**
         * Update selected object's Z position - only numbers allowed by textFormatter
         */
        txtObjectZPos.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().getNormal().setZ((Double.parseDouble(txtObjectZPos.getText())));
            if (autoRender) render();
        });
        txtObjectZPos.setTextFormatter(new TextFormatter <> (input -> input.getControlNewText().matches(textFormatterDoubleRegex) ? input : null));
        
        /**
         * On action get value of the material choice for the selected object and modify it
         */
        choiceMaterial.setOnAction((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            if (choiceMaterial.getValue() == "Diffuse") {
                item.getObj().setType(1);
            }else if (choiceMaterial.getValue() == "Reflective") {
                item.getObj().setType(2);
            }else if (choiceMaterial.getValue() == "Refractive") {
                item.getObj().setType(3);
            } 
        });
        
        choiceEngine.setOnAction((event) -> {
            if (choiceEngine.getValue() == "Normal") {
                renderEngine = 0;
                autoRender = false;
            }else if (choiceEngine.getValue() == "Banded") {
               renderEngine = 1;
               autoRender = false;
            }else if (choiceEngine.getValue() == "Psycho") {
                renderEngine = 2;
                autoRender = false;
            }else if (choiceEngine.getValue() == "Rasterized") {
                renderEngine = 3;
                autoRender = true;
            }
        });     
    }
    
    /**
     * Class uniquely for Wrapper objects to contain the elements of the objectList
     */
    class ObjWrapper {
        /** Scene Object Name */
        private String name;
        /** Scene Object instance */
        private SceneObject obj;

        /**
         * Constructor to create new Scene Object wrapper
         * 
         * @param name name of object String
         * @param obj object instance SceneObject
         */
        public ObjWrapper(String name, SceneObject obj) {
            this.name = name;
            this.obj = obj;
        }

        /**
         * Get the name of this object
         * 
         * @return String name
         */
        public String getName() {
            return name;
        }

        /**
         * Get the object instance
         * 
         * @return SceneObject object instance
         */
        public SceneObject getObj() {
            return obj;
        }

        /**
         * Set the name of this object
         * 
         * @param name String
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Set the object instance referenced
         * 
         * @param obj SceneObject
         */
        public void setObj(SceneObject obj) {
            this.obj = obj;
        }

        /**
         * Return the name of the object as toString for list display
         * 
         * @return name String
         */
        @Override
        public String toString() {
            return name;
        }
        
    }

}
