package edu.vanier.Lumina.rendering;

/**
 * Represents the color of a diffuse material.
 * This class stores the red, green, and blue components of the color.
 * These components should be 0-255
 * @author William Carbonneau
 */
public class DiffuseColor {
    private double r; // Red component of the color
    private double g; // Green component of the color
    private double b; // Blue component of the color

    /**
     * Constructs a new DiffuseColor object with the specified RGB components.
     * 
     * @param r The red component of the color (0-255).
     * @param g The green component of the color (0-255).
     * @param b The blue component of the color (0-255).
     */
    public DiffuseColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * General constructor
     */
    public DiffuseColor() {
        this.r = 0;
        this.g = 0;
        this.b = 0;
    }
    

    /**
     * Retrieves the red component of the color.
     * 
     * @return The red component of the color (0-255).
     */
    public double getR() {
        return r;
    }

    /**
     * Sets the red component of the color.
     * 
     * @param r The new value for the red component of the color (0-255).
     */
    public void setR(double r) {
        this.r = r;
    }

    /**
     * Retrieves the green component of the color.
     * 
     * @return The green component of the color (0-255).
     */
    public double getG() {
        return g;
    }

    /**
     * Sets the green component of the color.
     * 
     * @param g The new value for the green component of the color (0-255).
     */
    public void setG(double g) {
        this.g = g;
    }

    /**
     * Retrieves the blue component of the color.
     * 
     * @return The blue component of the color (0-255).
     */
    public double getB() {
        return b;
    }

    /**
     * Sets the blue component of the color.
     * 
     * @param b The new value for the blue component of the color (0-255).
     */
    public void setB(double b) {
        this.b = b;
    }
    
    /**
     * Adds one DiffuseColor object to the current one and returns a new version.
     * 
     * @param color The new DiffuseColor to be added to the current one.
     * @return new DiffuseColor
     */
    public DiffuseColor add(DiffuseColor color) {
        return new DiffuseColor(this.r + color.getR(), this.g + color.getG(), this.b + color.getB());
    }
    
    /**
     * Adds one DiffuseColor object to the current one and modifies the object directly.
     * 
     * @param color The new DiffuseColor to be added to the current one.
     */
    public void addToObject(DiffuseColor color) {
        this.r += color.getR(); 
        this.g += color.getG(); 
        this.b += color.getB();
    }
    
    /**
     * Multiplies a scalar number by the current object and returns a new version.
     * 
     * @param val The new DiffuseColor to be multiplied by the current object.
     * @return new DiffuseColor
     */
    public DiffuseColor multiply(double val) {
        return new DiffuseColor(this.r * val, this.g * val, this.b * val);
    }
    
    /**
     * Multiplies a scalar number by the current object and modifies the object directly.
     * 
     * @param val The new DiffuseColor to be multiplied by the current object.
     */
    public void multiplyToObject(double val) {
        this.r *= val;
        this.g *= val;
        this.b *= val;
    }

    /**
     * Multiplies one DiffuseColor object by the current one and modifies the object directly.
     * 
     * @param color The new DiffuseColor to be multiplied by the current one.
     * @return new DiffuseColor
     */
    public DiffuseColor multiplyColor(DiffuseColor color) {
        return new DiffuseColor(this.r * color.getR(), this.g * color.getG(), this.b * color.getB());
    }
    
    
}
