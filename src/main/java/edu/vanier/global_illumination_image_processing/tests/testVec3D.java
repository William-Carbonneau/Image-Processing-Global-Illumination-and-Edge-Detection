package edu.vanier.Global_Illumination_Image_Processing.tests;

import edu.vanier.global_illumination_image_processing.rendering.Vec3D;

/**
 * Test the Vec3D class, TODO, update the tests to make more verbose
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class testVec3D {

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

    public static void testAdd() {
        Vec3D v1 = new Vec3D(1, 2, 3);
        Vec3D v2 = new Vec3D(4, 5, 6);
        Vec3D result = v1.add(v2);
        System.out.println("Addition Test:");
        System.out.println("Expected: (5.0, 7.0, 9.0)");
        System.out.println("Result: (" + result.getX() + ", " + result.getY() + ", " + result.getZ() + ")");
        System.out.println();
    }

    public static void testSubtract() {
        Vec3D v1 = new Vec3D(4, 5, 6);
        Vec3D v2 = new Vec3D(1, 2, 3);
        Vec3D result = v1.subtract(v2);
        System.out.println("Subtraction Test:");
        System.out.println("Expected: (3.0, 3.0, 3.0)");
        System.out.println("Result: (" + result.getX() + ", " + result.getY() + ", " + result.getZ() + ")");
        System.out.println();
    }

    public static void testMultiply() {
        Vec3D v = new Vec3D(1, 2, 3);
        double scalar = 2;
        Vec3D result = v.multiply(scalar);
        System.out.println("Multiplication Test:");
        System.out.println("Expected: (2.0, 4.0, 6.0)");
        System.out.println("Result: (" + result.getX() + ", " + result.getY() + ", " + result.getZ() + ")");
        System.out.println();
    }

    public static void testDivide() {
        Vec3D v = new Vec3D(4, 6, 8);
        double scalar = 2;
        Vec3D result = v.divide(scalar);
        System.out.println("Division Test:");
        System.out.println("Expected: (2.0, 3.0, 4.0)");
        System.out.println("Result: (" + result.getX() + ", " + result.getY() + ", " + result.getZ() + ")");
        System.out.println();
    }

    public static void testHadamardMult() {
        Vec3D v1 = new Vec3D(1, 2, 3);
        Vec3D v2 = new Vec3D(4, 5, 6);
        Vec3D result = v1.HadamardMult(v2);
        System.out.println("Hadamard Multiplication Test:");
        System.out.println("Expected: (4.0, 10.0, 18.0)");
        System.out.println("Result: (" + result.getX() + ", " + result.getY() + ", " + result.getZ() + ")");
        System.out.println();
    }

    public static void testLength() {
        Vec3D v = new Vec3D(3, 4, 5);
        double result = v.length();
        System.out.println("Length Test:");
        System.out.println("Expected: 7.0710678118654755");
        System.out.println("Result: " + result);
        System.out.println();
    }

    public static void testNorm() {
        Vec3D v = new Vec3D(3, 4, 5);
        Vec3D result = v.norm();
        System.out.println("Normalization Test:");
        System.out.println("Expected: (0.4242640687119285, 0.565685424949238, 0.7071067811865475)");
        System.out.println("Result: (" + result.getX() + ", " + result.getY() + ", " + result.getZ() + ")");
        System.out.println();
    }

    public static void testDot() {
        Vec3D v1 = new Vec3D(1, 2, 3);
        Vec3D v2 = new Vec3D(4, 5, 6);
        double result = v1.dot(v2);
        System.out.println("Dot Product Test:");
        System.out.println("Expected: 32.0");
        System.out.println("Result: " + result);
        System.out.println();
    }

    public static void testCross() {
        Vec3D v1 = new Vec3D(1, 0, 0);
        Vec3D v2 = new Vec3D(0, 1, 0);
        Vec3D result = v1.cross(v2);
        System.out.println("Cross Product Test:");
        System.out.println("Expected: (0.0, 0.0, 1.0)");
        System.out.println("Result: (" + result.getX() + ", " + result.getY() + ", " + result.getZ() + ")");
        System.out.println();
    }

        public static void testGet() {
        Vec3D v = new Vec3D(2, 3, 4);
        double x = v.get(0);
        double y = v.get(1);
        double z = v.get(2);
        System.out.println("Get Component Test:");
        System.out.println("Expected (x, y, z): (2.0, 3.0, 4.0)");
        System.out.println("Result: (" + x + ", " + y + ", " + z + ")");
        System.out.println();
    }

    public static void testOrthonormalSystem() {
        Vec3D v1 = new Vec3D(1, 0, 0);
        Vec3D v2 = new Vec3D(0, 0, 0);
        Vec3D v3 = new Vec3D(0, 0, 0);
        Vec3D.orthonormalSystem(v1, v2, v3);
        System.out.println("Orthonormal System Test:");
        System.out.println("Expected: (0,1,0) (0,0,1)");
        System.out.println("Result:");
        System.out.println("v1: (" + v1.getX() + ", " + v1.getY() + ", " + v1.getZ() + ")");
        System.out.println("v2: (" + v2.getX() + ", " + v2.getY() + ", " + v2.getZ() + ")");
        System.out.println("v3: (" + v3.getX() + ", " + v3.getY() + ", " + v3.getZ() + ")");
    }
}

       
