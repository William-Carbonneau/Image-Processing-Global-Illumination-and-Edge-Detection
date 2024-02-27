package edu.vanier.global_illumination_image_processing.rendering;

/**
 * Represents the color of a diffuse material.
 * This class stores the red, green, and blue components of the color.
 * These components should be 0-255
 * @author William Carbonneau
 */
public class diffuseColor {
    private int r; // Red component of the color
    private int g; // Green component of the color
    private int b; // Blue component of the color

    /**
     * Constructs a new DiffuseColor object with the specified RGB components.
     * 
     * @param r The red component of the color (0-255).
     * @param g The green component of the color (0-255).
     * @param b The blue component of the color (0-255).
     */
    public diffuseColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Retrieves the red component of the color.
     * 
     * @return The red component of the color (0-255).
     */
    public int getR() {
        return r;
    }

    /**
     * Sets the red component of the color.
     * 
     * @param r The new value for the red component of the color (0-255).
     */
    public void setR(int r) {
        this.r = r;
    }

    /**
     * Retrieves the green component of the color.
     * 
     * @return The green component of the color (0-255).
     */
    public int getG() {
        return g;
    }

    /**
     * Sets the green component of the color.
     * 
     * @param g The new value for the green component of the color (0-255).
     */
    public void setG(int g) {
        this.g = g;
    }

    /**
     * Retrieves the blue component of the color.
     * 
     * @return The blue component of the color (0-255).
     */
    public int getB() {
        return b;
    }

    /**
     * Sets the blue component of the color.
     * 
     * @param b The new value for the blue component of the color (0-255).
     */
    public void setB(int b) {
        this.b = b;
    }
}
