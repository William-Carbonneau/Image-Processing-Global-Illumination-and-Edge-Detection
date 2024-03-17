package edu.vanier.global_illumination_image_processing.rendering;

import java.util.HashMap;

/**
 * A Scene for the object to live in 
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class Scene {
    // TODO potentially change this to a hashset to only have unique elements and easy deletion
    private HashMap<String, SceneObject> objects;

    /**
     * Constructor to create the list of objects in the scene
     */
    public Scene() {
        this.objects = new HashMap<>();
    }
    
    /**
     * Adds an object (shape) to the scene's list of objects
     * @param object type: SceneObject
     */
    public void  addObj(String objName, SceneObject object) {
        objects.put(objName, object);
    }
    
    /**
     * Removes an object (shape) from the scene's list of objects
     * @param objName object name: String
     * @return return 1 if the key exists and no object was added, else 0 for success
     */
    public int removeObj(String objName) {
        if (objects.containsKey(objName)) {
            return 1;
        }
        objects.remove(objName);
        return 0;
    }
    
    /**
     * Get an object in the scene by name
     * @param objName name of object as key type: String
     * @return the object found or null
     */
    public SceneObject getObjectByName(String objName) {
        return objects.get(objName);
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
        for (String key:objects.keySet()) {
             double temp = objects.get(key).intersect(ray);
             if (temp < closestIntersection.getIntersectDistance() && temp > Intersection.EPS) {
                closestIntersection.setIntersectDistance(temp);
                closestIntersection.setObject(objects.get(key));
            }
        }
        return closestIntersection;
    }
}
