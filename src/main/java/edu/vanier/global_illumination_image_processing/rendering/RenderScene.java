package edu.vanier.global_illumination_image_processing.rendering;

import java.util.HashMap;
import java.util.Map;

/**
 * A Scene for the object to live in 
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class RenderScene {
    // TODO potentially change this to a hashset to only have unique elements and easy deletion
    private Map<String, SceneObject> objects;

    /**
     * Constructor to create the list of objects in the scene
     */
    public RenderScene() {
        this.objects = new HashMap<>();
    }
    
    /**
     * Adds an object (shape) to the scene's list of objects
     * @param objName String: name of object
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
        if (!objects.containsKey(objName)) {
            return 1;
        }
        objects.remove(objName);
        return 0;
    }
    
    /**
     * Replace a key with a new one
     * @param oldKey String key to replace
     * @param newKey String key to replace with
     * @return 0 for success and 1 for failure
     */
    public int replaceKey(String oldKey, String newKey) {
        try {
            objects.put(newKey,objects.get(oldKey));
            return 0;
        }catch (Exception e) {
            return 1;
        }
    }
    
    /**
     * Get an object in the scene by name
     * @param objName name of object as key type: String
     * @return the SceneObject found or null
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
