package frc.robot.subsystems;

import frc.robot.LimelightHelpers;
import frc.robot.LimelightHelpers.RawDetection;
// DriverStation not used in this subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.Constants;
import frc.robot.Constants.VisionConstants;

/**
 * Vision subsystem adapted to use your LimelightHelpers library, auto-selecting
 * the appropriate alliance botpose entries (wpiblue / wpired) based on DriverStation.
 *
 * Includes a small runtime validator to log raw Limelight arrays and converted poses
 * for on-robot verification.
 */
public class VisionSubsystem extends SubsystemBase {
    // Limelight identifier ("" for default 'limelight' table, or the hostname if using multiple)
    private final String limelightName;

    public VisionSubsystem(String name) {
        this.limelightName = name;

         // Get raw neural detector results
RawDetection[] detections = LimelightHelpers.getRawDetections(limelightName); {
for (RawDetection detection : detections) {
    int classID = detection.classId;
    double txnc = detection.txnc;
    double tync = detection.tync;
    double ta = detection.ta;
    // Access corner coordinates if needed
    double corner0X = detection.corner0_X;
    double corner0Y = detection.corner0_Y;
    // ... corners 1-3 available similarly
}
}

// Seed the internal IMU with your external gyro (call while disabled)
LimelightHelpers.SetIMUMode("", 1);

// Switch to internal IMU with external assist when enabled
LimelightHelpers.SetIMUMode("", 4);
LimelightHelpers.SetIMUAssistAlpha("", 0.001);  // Adjust correction strength



// Set a custom crop window for improved performance (-1 to 1 for each value)
LimelightHelpers.setCropWindow("", -0.5, 0.5, -0.5, 0.5);

// Change the camera pose relative to robot center (x forward, y left, z up, degrees)
LimelightHelpers.setCameraPose_RobotSpace("",
    0.5,    // Forward offset (meters)
    0.0,    // Side offset (meters)
    0.5,    // Height offset (meters)
    0.0,    // Roll (degrees)
    30.0,   // Pitch (degrees)
    0.0     // Yaw (degrees)
);


// Set AprilTag offset tracking point (meters)
LimelightHelpers.setFiducial3DOffset("",
    0.0,    // Forward offset
    0.0,    // Side offset
    0.5     // Height offset
);


// Configure AprilTag detection
LimelightHelpers.SetFiducialIDFiltersOverride("", new int[]{9, 10, 15, 16, 25, 26, 29, 30, 31, 32}); // Only track these tag IDs (CHANGE TO THE ONES YOU WANT)
LimelightHelpers.SetFiducialDownscalingOverride("", 2.0f); // Process at half resolution


// Adjust keystone crop window (-0.95 to 0.95 for both horizontal and vertical)
LimelightHelpers.setKeystone("", 0.1, -0.05);
    }



    /*gets botpose estimates for both alliances with megatag 2 which with IMU creates
    improved localization during rotation, but also autodetects alliance which is cool.*/
@Override
    public void periodic() {
        LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);
        LimelightHelpers.getBotPoseEstimate_wpiRed_MegaTag2(limelightName);
    }


    // Let the current pipeline control the LEDs
    public void setLEDMode_PipelineControl() {
    LimelightHelpers.setLEDMode_PipelineControl(limelightName);
    }
    // Force LEDs on/off/blink

    public void setLEDMode_ForceOn() {
        LimelightHelpers.setLEDMode_ForceOn(limelightName);
    }

    public void setLEDMode_ForceOff() {
        LimelightHelpers.setLEDMode_ForceOff(limelightName);
    }

    public void setLEDMode_ForceBlink() {
        LimelightHelpers.setLEDMode_ForceBlink(limelightName);
    }

    //gets the april tag ID
    /** @return AprilTag / fiducial ID (tid) */
    public int getTargetID() {
        return (int) LimelightHelpers.getFiducialID(limelightName);
    }

     /** @return whether the limelight currently sees a valid target */
    public boolean targetValid() {
        return LimelightHelpers.getTV(limelightName);
    }

    /** @return X offset (degrees) reported by Limelight (tx) */
    public double targetXOffset() {
        return LimelightHelpers.getTX(limelightName);
    }

    /** @return Y offset (degrees) reported by Limelight (ty) */
    public double targetYOffset() {
        return LimelightHelpers.getTY(limelightName);
    }

    /** @return target area (ta) */
    public double targetArea() {
        return LimelightHelpers.getTA(limelightName);
    }


    /**
     * this is the one that matters ;)
     * 676676767676676767676767676767676767676767676767676767676767676767676767676767676767676
     * CALL THIS NUMBER -> (913) 488-2670
     * -sam
     * @return
     */

     //translates the botpose to 2d
    public Pose2d getBotPoseTargetSpace() {
        return LimelightHelpers.toPose3D(LimelightHelpers.getBotPose_TargetSpace(limelightName)).toPose2d();
    }

}