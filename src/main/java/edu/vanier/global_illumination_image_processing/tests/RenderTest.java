package edu.vanier.Global_Illumination_Image_Processing.tests;

import edu.vanier.global_illumination_image_processing.rendering.RenderingEquation;
import edu.vanier.global_illumination_image_processing.rendering.Vec3D;

/**
 * A test class for the rendering, test is qualitative 
 * @author William Carbonneau <2265724 at edu.vaniercollege.ca>
 */
public class RenderTest {
    public static void main(String[] args) {
        RenderingEquation.run(16.0);
    }
}
