package edu.vanier.global_illumination_image_processing.tests;

import edu.vanier.global_illumination_image_processing.rendering.Vec3D;

/**
 * Test the Vec3D class
 * @author William Carbonneau
 */
public class testVec3D {

    /**
    * Tests for Vec3D
    * It creates two vectors, adds them, and prints the result.
    */
    public static void testAdd() {
        Vec3D v1 = new Vec3D(1, 2, 3);
        Vec3D v2 = new Vec3D(4, 5, 6);
        Vec3D result = v1.add(v2);
        System.out.println("Addition Test:");
        System.out.println("Expected result: (5.0, 7.0, 9.0)");
        System.out.printf("Result: (%f %f, %f)%n", result.getX(), result.getY(), result.getZ());
        System.out.println();
    }

    /**
    * Test method for Vec3D subtraction.
    * It creates two vectors, subtracts them, then prints the result.
    */
    public static void testSubtract() {
        Vec3D v1 = new Vec3D(4, 5, 6);
        Vec3D v2 = new Vec3D(1, 2, 3);
        Vec3D result = v1.subtract(v2);
        System.out.println("Subtraction Test:");
        System.out.println("Expected result: (3.0, 3.0, 3.0)");
        System.out.printf("Result: (%f %f, %f)%n", result.getX(), result.getY(), result.getZ());
        System.out.println();
    }

    /**
     * Test method for Vec3D multiplication by scalar.
     * It creates a vector, multiplies it by a scalar, then prints the result.
     */
    public static void testMultiply() {
        Vec3D v = new Vec3D(1, 2, 3);
        double scalarMultiple = 2;
        Vec3D result = v.multiply(scalarMultiple);
        System.out.println("Multiplication Test:");
        System.out.println("Expected result: (2.0, 4.0, 6.0)");
        System.out.printf("Result: (%f %f, %f)%n", result.getX(), result.getY(), result.getZ());
        System.out.println();
    }

    /**
    * Test method for Vec3D division by scalar.
    * It creates a vector, divides it by a scalar, then prints the result.
    */
    public static void testDivide() {
        Vec3D v = new Vec3D(4, 6, 8);
        double scalar = 2;
        Vec3D result = v.divide(scalar);
        System.out.println("Division Test:");
        System.out.println("Expected result: (2.0, 3.0, 4.0)");
        System.out.printf("Result: (%f %f, %f)%n", result.getX(), result.getY(), result.getZ());
        System.out.println();
    }

    /**
    * Test method for Vec3D Hadamard multiplication.
    * It creates two vectors, performs Hadamard multiplication between them, then prints the result.
    */
    public static void testHadamardMult() {
        Vec3D v1 = new Vec3D(1, 2, 3);
        Vec3D v2 = new Vec3D(4, 5, 6);
        Vec3D result = v1.HadamardMult(v2);
        System.out.println("Hadamard Multiplication Test:");
        System.out.println("Expected result: (4.0, 10.0, 18.0)");
        System.out.printf("Result: (%f %f, %f)%n", result.getX(), result.getY(), result.getZ());
        System.out.println();
    }

    /**
    * Test method for computing the length of a Vec3D.
    * It creates a vector, computes its length, then prints the result.
    */
    public static void testLength() {
        Vec3D v = new Vec3D(3, 4, 5);
        double result = v.length();
        System.out.println("Length Test:");
        System.out.println("Expected result: 7.0710678118654755");
        System.out.println("Result: " + result);
        System.out.println();
    }

    /**
    * Test method for computing the normalized version of a Vec3D.
    * It creates a vector, computes its norm, then prints the result.
    */
    public static void testNorm() {
        Vec3D v = new Vec3D(3, 4, 5);
        Vec3D result = v.norm();
        System.out.println("Test norm():");
        System.out.println("Expected result: (0.4242640687119285, 0.565685424949238, 0.7071067811865475)");
        System.out.printf("Result: (%f, %f, %f)%n", result.getX(), result.getY(), result.getZ());
        System.out.println();
    }

    /**
    * Test method for computing the dot product of two Vec3D vectors.
    * It creates two vectors, computes their dot product, then prints the result.
    */
    public static void testDot() {
        Vec3D v1 = new Vec3D(1, 2, 3);
        Vec3D v2 = new Vec3D(4, 5, 6);
        double result = v1.dot(v2);
        System.out.println("Test dot():");
        System.out.println("Expected result: 32.0");
        System.out.println("Result: " + result);
        System.out.println();
    }

    /**
    * Test method for computing the cross product of two Vec3D vectors.
    * It creates two vectors, computes their cross product, then prints the result.
    */
    public static void testCross() {
        Vec3D v1 = new Vec3D(1, 0, 0);
        Vec3D v2 = new Vec3D(0, 1, 0);
        Vec3D result = v1.cross(v2);
        System.out.println("Test cross():");
        System.out.println("Expected result: (0.0, 0.0, 1.0)");
        System.out.printf("Result: (%f, %f, %f)%n", result.getX(), result.getY(), result.getZ());
        System.out.println();
    }

    /**
    * Test method for getting components of a Vec3D vector by their indices.
    * It creates a vector, retrieves its components by index, then prints the result.
    */
    public static void testGet() {
        Vec3D v = new Vec3D(2, 3, 4);
        double x = v.get(0);
        double y = v.get(1);
        double z = v.get(2);
        System.out.println("Test Get()");
        System.out.println("Expected result: (2.0, 3.0, 4.0)");
        System.out.printf("Result: (%f, %f, %f)%n", x, y, z);
        System.out.println();
    }

    /**
    * Test method for creating an orthonormal system of vectors.
    * It creates two vectors, generates an orthonormal system, then prints the result.
    */
    public static void testOrthonormalSystem() {
        Vec3D v1 = new Vec3D(1, 0, 0);
        Vec3D v2 = new Vec3D(0, 0, 0);
        Vec3D v3 = new Vec3D(0, 0, 0);
        Vec3D.orthonormalSystem(v1, v2, v3);
        System.out.println("Test orthonormalSystem() :");
        System.out.println("Expected something like: (0,1,0) (0,0,1)");
        System.out.println("Result:");
        System.out.println("v1: (" + v1.getX() + ", " + v1.getY() + ", " + v1.getZ() + ")");
        System.out.println("v2: (" + v2.getX() + ", " + v2.getY() + ", " + v2.getZ() + ")");
        System.out.println("v3: (" + v3.getX() + ", " + v3.getY() + ", " + v3.getZ() + ")");
    }
    
    public static void main(String[] args) {
        testAdd();
        testSubtract();
        testMultiply();
        testDivide();
        testHadamardMult();
        testLength();
        testNorm();
        testDot();
        testCross();
        testGet();
        testOrthonormalSystem();
    }
}

       
