package edu.vanier.global_illumination_image_processing.rendering;

/**
 * A vector3D class containing all the vector math needed for this project.
 * This class represents a 3D vector with components x, y, and z.
 * It provides various mathematical operations: addition, subtraction,
 * scalar multiplication, division, normalization, dot product, cross product, Hadamard multiplication, magnitude, orthonormal system.
 * 
 * @author William Carbonneau
 */
public class Vec3D {
    private double x, y, z; // Components of the vector

    /**
     * Constructs a new Vec3D object with the specified components.
     * 
     * @param x The x-component of the vector.
     * @param y The y-component of the vector.
     * @param z The z-component of the vector.
     */
    public Vec3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Retrieves the x-component of the vector.
     * 
     * @return The x-component of the vector.
     */
    public double getX() {
        return x;
    }

    /**
     * Retrieves the y-component of the vector.
     * 
     * @return The y-component of the vector.
     */
    public double getY() {
        return y;
    }

    /**
     * Retrieves the z-component of the vector.
     * 
     * @return The z-component of the vector.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the x-component of the vector.
     * 
     * @param x The new value for the x-component of the vector.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y-component of the vector.
     * 
     * @param y The new value for the y-component of the vector.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Sets the z-component of the vector.
     * 
     * @param z The new value for the z-component of the vector.
     */
    public void setZ(double z) {
        this.z = z;
    }

    // Mathematical operations

    /**
     * Adds another vector to this vector.
     * 
     * @param j The vector to be added.
     * @return The resulting vector after addition.
     */
    public Vec3D add(Vec3D j) {
        return new Vec3D(this.x + j.getX(), this.y + j.getY(), this.z + j.getZ());
    }

    /**
     * Subtracts another vector from this vector.
     * 
     * @param j The vector to be subtracted.
     * @return The resulting vector after subtraction.
     */
    public Vec3D subtract(Vec3D j) {
        return new Vec3D(this.x - j.getX(), this.y - j.getY(), this.z - j.getZ());
    }

    /**
     * Multiplies this vector by a scalar value.
     * 
     * @param i The scalar value.
     * @return The resulting vector after multiplication.
     */
    public Vec3D multiply(double i) {
        return new Vec3D(this.x * i, this.y * i, this.z * i);
    }

    /**
     * Divides this vector by a scalar value.
     * 
     * @param i The scalar value.
     * @return The resulting vector after division.
     */
    public Vec3D divide(double i) {
        return new Vec3D(this.x / i, this.y / i, this.z / i);
    }

    /**
     * Computes the Hadamard (element-wise) product of this vector and another vector.
     * 
     * @param j The vector to be multiplied element-wise.
     * @return The resulting vector after Hadamard multiplication.
     */
    public Vec3D HadamardMult(Vec3D j) {
        return new Vec3D(this.x * j.getX(), this.y * j.getY(), this.z * j.getZ());
    }

    /**
     * Computes the length (magnitude) of this vector.
     * 
     * @return The length of this vector.
     */
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     * Computes the normalized version of this vector.
     * 
     * @return The normalized version of this vector.
     */
    public Vec3D norm() {
        // Handle case where the vector may be zero to avoid null or divide by zero error
        if (this.x == 0.0 && this.y == 0.0 && this.z == 0.0) return this;
        return this.multiply(1 / this.length());
    }

    /**
     * Computes the dot product of this vector and another vector.
     * 
     * @param j The other vector.
     * @return The dot product of this vector and the other vector.
     */
    public double dot(Vec3D j) {
        return ((this.x * j.getX()) + (this.y * j.getY()) + (this.z * j.getZ()));
    }

    /**
     * Computes the cross product of this vector and another vector.
     * 
     * @param j The other vector.
     * @return The cross product of this vector and the other vector.
     */
    public Vec3D cross(Vec3D j) {
        return new Vec3D((this.y * j.getZ()) - (this.z * j.getY()),
                         (this.z * j.getX()) - (this.x * j.getZ()),
                         (this.x * j.getY()) - (this.y * j.getX()));
    }

    /**
     * Retrieves the component of this vector at the specified index.
     * 
     * @param i The index of the component (0 for x, 1 for y, 2 for z).
     * @return The component value.
     */
    public double get(int i) {
        return i == 0 ? this.x : (i == 1 ? this.y : this.z);
    }
    
    /**
     * Creates an orthonormal system of vectors, assuming v1 is already normalized.
     * 
     * @param v1 A normalized vector.
     * @param v2 Another vector.
     * @param v3 Another vector.
     */
    public static void orthonormalSystem(Vec3D v1, Vec3D v2, Vec3D v3) {

        if (Math.abs(v1.getX()) > Math.abs(v1.getY())) {
            double targetLength = 1 / Math.sqrt(v1.getX() * v1.getX() + v1.getZ() * v1.getZ());
            v2.setX(-v1.getZ() * targetLength);
            v2.setY(0.0);
            v2.setZ(v1.getX() * targetLength);
        } else {
            double targetLength = 1 / Math.sqrt(v1.getY() * v1.getY() + v1.getZ() * v1.getZ());
            v2.setX(0.0);
            v2.setY(v1.getZ() * targetLength);
            v2.setZ(-v1.getY() * targetLength);
        }
        v3 = v1.cross(v2);
    }
}