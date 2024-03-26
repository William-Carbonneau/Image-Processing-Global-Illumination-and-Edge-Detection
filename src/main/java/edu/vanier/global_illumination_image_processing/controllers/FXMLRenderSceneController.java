package edu.vanier.global_illumination_image_processing.controllers;

import edu.vanier.global_illumination_image_processing.rendering.DiffuseColor;
import edu.vanier.global_illumination_image_processing.rendering.RenderWrapper;
import edu.vanier.global_illumination_image_processing.rendering.Scene;
import edu.vanier.global_illumination_image_processing.rendering.SceneObject;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;
import edu.vanier.global_illumination_image_processing.rendering.objects.Plane;
import edu.vanier.global_illumination_image_processing.rendering.objects.Sphere;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * The controller for the rendering scene
 * 
 * @author Zachary Tremblay and William Carbonneau
 */
public class FXMLRenderSceneController {
    @FXML VBox RootVBox;
    @FXML MenuItem BackToTitleMenuItem;
    @FXML ListView listObjectList;
    @FXML Label lblObjectType;
    @FXML TextField txtObjectName;
    @FXML TextField txtObjectXPos;
    @FXML TextField txtObjectYPos;
    @FXML TextField txtObjectZPos;
    @FXML TextField txtDTO;
    @FXML TextField txtEmissiveness;
    @FXML TextField txtIOR;
    @FXML TextField txtSPP; // TODO get and set value from GUI
    @FXML ChoiceBox choiceMaterial;
    @FXML Button btnRender;

    
    public void initialize(){
        /** create the render scene */
        Scene mainScene = new Scene();
        /** The renderer instance TODO modify width/height */
        RenderWrapper renderer = new RenderWrapper(800, 800, mainScene, 8.0);
        
        // create list of elements - needs a new class wrapper
        ObservableList<ObjWrapper> objectList = FXCollections.observableArrayList();
        ObservableList<String> typeChoiceBoxList = FXCollections.observableArrayList("Diffuse","Reflective","Refractive");
        listObjectList.setItems(objectList);
        choiceMaterial.setItems(typeChoiceBoxList);
        
        // temporary list of default objects
        objectList.add(new ObjWrapper("Metal Sphere 1", new Sphere(new Vec3D(-0.75,-1.45,-4.4), 1.05, new DiffuseColor(4, 8, 4), 0,2)));
        objectList.add(new ObjWrapper("Glass sphere 1", new Sphere(new Vec3D(2.0,-2.05,-3.7), 0.5, new DiffuseColor(10, 10, 1), 0,3)));
        objectList.add(new ObjWrapper("Diffuse sphere 1", new Sphere(new Vec3D(-1.75,-1.95,-3.1), 0.6, new DiffuseColor(4, 4, 12), 0,1)));
        objectList.add(new ObjWrapper("bottom plane", new Plane(new Vec3D(0,1,0), 2.5, new DiffuseColor(6, 6, 6), 0,1)));
        objectList.add(new ObjWrapper("back plane", new Plane(new Vec3D(0,0,1), 5.5, new DiffuseColor(6, 6, 6), 0,1)));
        objectList.add(new ObjWrapper("left plane", new Plane(new Vec3D(1,0,0), 2.75, new DiffuseColor(10, 2, 2), 0,1)));
        objectList.add(new ObjWrapper("right plane", new Plane(new Vec3D(-1,0,0), 2.75, new DiffuseColor(2, 10, 2), 0,1)));
        objectList.add(new ObjWrapper("ceiling plane", new Plane(new Vec3D(0,-1,0), 3.0, new DiffuseColor(6, 6, 6), 0,1)));
        objectList.add(new ObjWrapper("front plane", new Plane(new Vec3D(0,0,-1), 0.5, new DiffuseColor(6, 6, 6), 0,1)));
        objectList.add(new ObjWrapper("light sphere 1", new Sphere(new Vec3D(0,1.9,-3), 0.5, new DiffuseColor(0, 0, 0), 10000,1)));
        objectList.get(9).getObj().setRefractiveIndex(1.9);   
        
        for (ObjWrapper obj: objectList) {
            mainScene.addObj(obj.getName(), obj.getObj());
        }
        
        BackToTitleMenuItem.setOnAction((event)->{
            try {
                System.out.println("Back to Title clicked");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLTitleScene.fxml"));
                loader.setController(new FXMLTitleSceneController());
                Pane root = loader.load();
                RootVBox.getChildren().setAll(root);
            } catch (IOException ex) {
                Logger.getLogger(FXMLConvolutionsSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        /**
         * Render the scene based on user-created parameters
         */
        btnRender.setOnAction((event) -> {
            renderer.render(true, true, 0);
            renderer.save();
        });
        
        /**
         * Update the current SPP TODO only numbers allowed
         */
        txtSPP.setOnKeyTyped((event) -> {
            renderer.setSPP(Double.parseDouble(txtSPP.getText()));
        });
        
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
            txtIOR.setText("" + item.getObj().getRefractiveIndex());
            txtEmissiveness.setText("" + item.getObj().getEmission());
            switch (item.getObj().getType()) {
                case 1 -> choiceMaterial.setValue(typeChoiceBoxList.get(0));
                case 2 -> choiceMaterial.setValue(typeChoiceBoxList.get(1));
                case 3 -> choiceMaterial.setValue(typeChoiceBoxList.get(2));
            }
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
         * Update selected object's DTO TODO only numbers allowed
         */
        txtDTO.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().setDistanceOrigin(Double.parseDouble(txtDTO.getText()));
        });
        
        /**
         * Update selected object's Emissiveness TODO only numbers allowed
         */
        txtEmissiveness.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().setEmission(Double.parseDouble(txtEmissiveness.getText()));
        });
        
        /**
         * Update selected object's IOR TODO only numbers allowed
         */
        txtIOR.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().setRefractiveIndex(Double.parseDouble(txtIOR.getText()));
        });
        
        /**
         * Update selected object's X position TODO only numbers allowed
         */
        txtObjectXPos.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().getNormal().setX((Double.parseDouble(txtObjectXPos.getText())));
        });
        
        /**
         * Update selected object's Y position TODO only numbers allowed
         */
        txtObjectYPos.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().getNormal().setY((Double.parseDouble(txtObjectYPos.getText())));
        });
        
        /**
         * Update selected object's Z position TODO only numbers allowed
         */
        txtObjectZPos.setOnKeyTyped((event) -> {
            ObjWrapper item = (ObjWrapper) listObjectList.getSelectionModel().getSelectedItem();
            
            if (item == null) return;
            
            // update object in list
            item.getObj().getNormal().setZ((Double.parseDouble(txtObjectZPos.getText())));
        });
        
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
