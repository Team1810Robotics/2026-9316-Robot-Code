package frc.robot.subsystems.vision;

// DriverStation not used in this subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.util.LimelightHelpers;
import frc.robot.util.LimelightHelpers.PoseEstimate;

import org.w3c.dom.ranges.DocumentRange;

import dev.doglog.DogLog;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;


/**
 * Vision subsystem adapted to use your LimelightHelpers library, auto-selecting
 * the appropriate alliance botpose entries (wpiblue / wpired) based on DriverStation.
 *
 * Includes a small runtime validator to log raw Limelight arrays and converted poses
 * for on-robot verification.
 */
public class VisionSubsystem extends SubsystemBase {
    public final String limelightName;

    private final CommandSwerveDrivetrain drivetrain;


    public VisionSubsystem(String name, CommandSwerveDrivetrain drivetrain) {
        this.limelightName = name;
        this.drivetrain = drivetrain;

        LimelightHelpers.setPipelineIndex(limelightName, 0);
        LimelightHelpers.SetIMUAssistAlpha(limelightName, .001);
    }

    @Override
    public void periodic() {
        if (DriverStation.isDisabled()) {
            LimelightHelpers.SetIMUMode(limelightName, 1);
        } else {
            LimelightHelpers.SetIMUMode(limelightName, 4);
        }

        LimelightHelpers.SetRobotOrientation(
            limelightName, 
            drivetrain.getState().Pose.getRotation().getDegrees(), 
            drivetrain.getState().Speeds.omegaRadiansPerSecond, 
            drivetrain.getPigeon2().getPitch().getValueAsDouble(), 0, 
            drivetrain.getPigeon2().getRoll().getValueAsDouble(), 0);

        if (!targetValid()) {
            DogLog.log("Vision/BotPose", new Pose2d());
            
            return;
        }
            
        PoseEstimate botPoseMT2 = getBotPoseMT2();

        drivetrain.addVisionMeasurement(botPoseMT2.pose, botPoseMT2.timestampSeconds);

        DogLog.log("Vision/BotPose", getBotPoseMT2().pose);
    }

    //gets the april tag ID
    /** @return AprilTag / fiducial ID (tid) */
    public int getTargetID() {
        return (int) LimelightHelpers.getFiducialID(limelightName);
    }

    public boolean targetValid() {
        return LimelightHelpers.getTV(limelightName);
    }


    public PoseEstimate getBotPoseMT1() {
        return LimelightHelpers.getBotPoseEstimate_wpiBlue(limelightName);
    }

    public PoseEstimate getBotPoseMT2() {
        return LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);
    }

}