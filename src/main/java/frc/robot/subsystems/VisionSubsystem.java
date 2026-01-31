package frc.robot.subsystems;

import frc.robot.LimelightHelpers;
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
    }

    //gets botpose estimates for both alliances, but also autodetects alliance which is cool.
@Override
    public void periodic() {
        LimelightHelpers.getBotPoseEstimate_wpiBlue(limelightName);
        LimelightHelpers.getBotPoseEstimate_wpiRed(limelightName);
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