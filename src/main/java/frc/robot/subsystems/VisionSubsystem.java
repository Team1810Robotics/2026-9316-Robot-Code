package frc.robot.subsystems;

import frc.robot.LimelightHelpers;
import frc.robot.LimelightHelpers.RawDetection;
// DriverStation not used in this subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix6.hardware.Pigeon2;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import frc.robot.Constants;
import frc.robot.Constants.VisionConstants;
import frc.robot.Constants.GyroAndIMUConstants;

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

    private final Pigeon2 m_gyro = new Pigeon2(0); // '0' is the CAN ID

    public VisionSubsystem(String name, SwerveDriveKinematics kinematics, SwerveModulePosition[] modulePositions) {
    this.limelightName = name;

    this.m_poseEstimator = new SwerveDrivePoseEstimator(
        kinematics,
        m_gyro.getRotation2d(), // Phoenix 6 helper
        modulePositions,
        new Pose2d()            // Starting position (usually 0,0,0)
    );

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

// Switch to pipeline 0 (list more as needed)
LimelightHelpers.setPipelineIndex(limelightName, 0);

// Seed the internal IMU with your external gyro (call while disabled)
LimelightHelpers.SetIMUMode(limelightName, 1);

// Switch to internal IMU with external assist when enabled
LimelightHelpers.SetIMUMode(limelightName, 4);
LimelightHelpers.SetIMUAssistAlpha(limelightName, 0.001);  // Adjust correction strength



// Set a custom crop window for improved performance (-1 to 1 for each value)
LimelightHelpers.setCropWindow(limelightName, -0.5, 0.5, -0.5, 0.5);

// Change the camera pose relative to robot center (x forward, y left, z up, degrees)
LimelightHelpers.setCameraPose_RobotSpace(limelightName,
    0.5,    // Forward offset (meters)
    0.0,    // Side offset (meters)
    0.5,    // Height offset (meters)
    0.0,    // Roll (degrees)
    30.0,   // Pitch (degrees)
    0.0     // Yaw (degrees)
);


// Set AprilTag offset tracking point (meters)
LimelightHelpers.setFiducial3DOffset(limelightName,
    0.0,    // Forward offset
    0.0,    // Side offset
    0.5     // Height offset
);


// Configure AprilTag detection
LimelightHelpers.SetFiducialIDFiltersOverride(limelightName, new int[]{9, 10, 15, 16, 25, 26, 29, 30, 31, 32}); // Only track these tag IDs (ADD THE ONES YOU WANT)
LimelightHelpers.SetFiducialDownscalingOverride(limelightName, 2.0f); // Process at half resolution


// Adjust keystone crop window (-0.95 to 0.95 for both horizontal and vertical)
LimelightHelpers.setKeystone(limelightName, 0.1, -0.05);

// First, tell Limelight your robot's current orientation
// Add .getValueAsDouble() to the end
double robotYaw = m_gyro.getYaw().getValueAsDouble() + Constants.GyroAndIMUConstants.GYRO_YAW_OFFSET_DEGREES;
LimelightHelpers.SetRobotOrientation(limelightName, robotYaw, 0.0, 0.0, 0.0, 0.0, 0.0);

    }

    /*gets botpose estimates for both alliances with megatag 2 which with IMU creates
    improved localization during rotation, but also autodetects alliance which is cool.*/
private final SwerveDrivePoseEstimator m_poseEstimator;
    @Override
    public void periodic() {
        LimelightHelpers.PoseEstimate limelightMeasurementBlue = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);
        LimelightHelpers.PoseEstimate limelightMeasurementRed = LimelightHelpers.getBotPoseEstimate_wpiRed_MegaTag2(limelightName);

        // Add it to your pose estimator
m_poseEstimator.setVisionMeasurementStdDevs(VecBuilder.fill(.5, .5, 9999999));
m_poseEstimator.addVisionMeasurement(
    limelightMeasurementBlue.pose,
    limelightMeasurementBlue.timestampSeconds
);
m_poseEstimator.addVisionMeasurement(
    limelightMeasurementRed.pose,
    limelightMeasurementRed.timestampSeconds
);
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