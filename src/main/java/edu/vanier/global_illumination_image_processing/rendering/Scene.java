package edu.vanier.global_illumination_image_processing.rendering;

import java.util.ArrayList;

/**
 * A Scene for the object to live in 
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class Scene {
    // TODO potentially change this to a hashset to only have unique elements and easy deletion
    private ArrayList<SceneObject> objects;

    /**
     * Constructor to create the list of objects in the scene
     */
    public Scene() {
        this.objects = new ArrayList<>();
    }
    
    /**
     * Adds an object (shape) to the scene's list of objects
     * @param object type: SceneObject
     */
    public void  addObj(SceneObject object) {
        objects.add(object);
    }
    
    /**
     * Removes an object (shape) from the scene's list of objects
     * @param object type: SceneObject
     */
    public void removeObj(SceneObject object) {
        objects.remove(object);
    }
    
    /**
     * return the closest intersection object of a ray in the scene
     * object could possibly be null (must handle outside)
     * @param ray Ray
     * @return closest intersection, type: Intersection
     */
    public Intersection intersect(Ray ray) {
        Intersection closestIntersection = new Intersection();
        
        // for all objects test intersection for nearest
        for (int i = 0; i < objects.size(); i++) {
            double temp = objects.get(i).intersect(ray);
            if (temp < closestIntersection.getIntersectDistance() && temp > Intersection.EPS) {
                closestIntersection.setIntersectDistance(temp);
                closestIntersection.setObject(objects.get(i));
            }
        }
        return closestIntersection;
    }
}
