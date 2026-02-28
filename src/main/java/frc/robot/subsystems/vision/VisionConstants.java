package frc.robot.subsystems.vision;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public class VisionConstants {
//TODO: Validate if these are needed
    // Public vision constants — set these to your measured camera offsets.
    public static final double CAMERA_FORWARD_METERS = 0.20; // 20 cm forward
    public static final double CAMERA_SIDE_METERS = 0.0; // centered
    public static final double CAMERA_UP_METERS = 0.45; // 45 cm above robot origin

    public static final double CAMERA_ROLL_DEG = 0.0;
    public static final double CAMERA_PITCH_DEG = 0.0;
    public static final double CAMERA_YAW_DEG = 0.0;
  public static final Matrix<N3, N1> visionMeasurementStdDevs =
      VecBuilder.fill(
          0.1, 0.1, Double.MAX_VALUE); // 10cm and 10 degrees std dev for vision measurements
    // april tag locations visual
    // https://drive.google.com/file/d/1Urb7EcdkFHfVp7dLyGpgPSgWiyvLkA06/view?usp=sharing
 
}
