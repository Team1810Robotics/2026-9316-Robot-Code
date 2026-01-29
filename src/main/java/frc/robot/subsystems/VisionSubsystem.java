package frc.robot.subsystems;

import frc.robot.LimelightHelpers;
// DriverStation not used in this subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.geometry.Pose2d;


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


    /** @return AprilTag / fiducial ID (tid) */
    public int getTargetID() {
        return (int) LimelightHelpers.getFiducialID(limelightName);
    }

    /**
     * this is the one that matters ;)
     * 676676767676676767676767676767676767676767676767676767676767676767676767676767676767676
     * CALL THIS NUMBER -> (913) 488-2670
     * -sam
     * @return
     */
    public Pose2d getBotPoseTargetSpace() {
        return LimelightHelpers.toPose3D(LimelightHelpers.getBotPose_TargetSpace(limelightName)).toPose2d();
    }

}