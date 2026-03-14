package frc.robot.subsystems.vision;

import com.ctre.phoenix6.signals.RGBWColor;

import dev.doglog.DogLog;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.util.LimelightHelpers;
import frc.robot.util.LimelightHelpers.PoseEstimate;

public class VisionSubsystem extends SubsystemBase {
  public final String limelightName;

  private boolean LED_CD = false;

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
        drivetrain.getPigeon2().getPitch().getValueAsDouble(),
        0,
        drivetrain.getPigeon2().getRoll().getValueAsDouble(),
        0);

    if (!targetValid() || getBotPoseMT2() == null) {
      DogLog.log("Vision/BotPose", new Pose2d());
      DogLog.log("Vision/TargetValid", false);
      DogLog.log("Vision/TX", 0.0);
      DogLog.log("Vision/TY", 0.0);
      DogLog.log("Vision/TargetID", -1);
      DogLog.log("Vision/TargetDistanceMeters", -1.0);
    if (!LED_CD) {
        LEDConstants.IDLE = true;
        LED_CD = true;
      }
        return;
    }

    LEDConstants.IDLE = false;
    LEDSubsystem.setLEDColor(
        new RGBWColor(LEDConstants.PERRYWINKLE[0], LEDConstants.PERRYWINKLE[1], LEDConstants.PERRYWINKLE[2], 0),
        false);
    LEDSubsystem.setLEDAnimation("Rainbow", false);

    LED_CD = false;

    PoseEstimate botPoseMT2 = getBotPoseMT2();
    drivetrain.addVisionMeasurement(botPoseMT2.pose, botPoseMT2.timestampSeconds);

    DogLog.log("Vision/BotPose", botPoseMT2.pose);
    DogLog.log("Vision/TargetValid", targetValid());
    DogLog.log("Vision/TX", getTx());
    DogLog.log("Vision/TY", getTy());
    DogLog.log("Vision/TargetID", getTargetID());
    DogLog.log("Vision/TargetForwardMeters", getTargetForwardMeters());
    DogLog.log("Vision/TargetLateralMeters", getTargetLateralMeters());
    DogLog.log("Vision/TargetDistanceMeters", getTargetDistanceMeters());
  }

  public int getTargetID() {
    return (int) LimelightHelpers.getFiducialID(limelightName);
  }

  public boolean targetValid() {
    return LimelightHelpers.getTV(limelightName);
  }

  public double getTx() {
    return LimelightHelpers.getTX(limelightName);
  }

  public double getTy() {
    return LimelightHelpers.getTY(limelightName);
  }

  public PoseEstimate getBotPoseMT1() {
    return LimelightHelpers.getBotPoseEstimate_wpiBlue(limelightName);
  }

  public PoseEstimate getBotPoseMT2() {
    return LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(limelightName);
  }

  public Pose2d getBotPoseTargetSpace() {
    return LimelightHelpers.toPose3D(LimelightHelpers.getBotPose_TargetSpace(limelightName))
        .toPose2d();
  }

  /**
   * Returns the raw bot-pose-in-target-space array from Limelight.
   * Useful for debugging axis meanings.
   */
  public double[] getTargetSpaceArray() {
    return LimelightHelpers.getBotPose_TargetSpace(limelightName);
  }

  /**
   * Estimated forward distance from robot/camera to target in meters.
   * Assumes target-space Z is the forward/depth axis.
   */
  public double getTargetForwardMeters() {
    if (!targetValid()) {
      return -1.0;
    }

    double[] targetSpace = getTargetSpaceArray();
    if (targetSpace == null || targetSpace.length < 3) {
      return -1.0;
    }

    return targetSpace[2];
  }

  /**
   * Estimated lateral offset from robot/camera to target in meters.
   * Assumes target-space X is the left/right axis.
   */
  public double getTargetLateralMeters() {
    if (!targetValid()) {
      return -1.0;
    }

    double[] targetSpace = getTargetSpaceArray();
    if (targetSpace == null || targetSpace.length < 3) {
      return -1.0;
    }

    return targetSpace[0];
  }

  /**
   * Estimated planar distance from robot/camera to target in meters.
   * Uses lateral + forward components from target space.
   */
  public double getTargetDistanceMeters() {
    if (!targetValid()) {
      return -1.0;
    }

    double lateral = getTargetLateralMeters();
    double forward = getTargetForwardMeters();

    if (lateral < 0 && forward < 0) {
      return -1.0;
    }

    return Math.hypot(lateral, forward);
  }
  public double getTargetBearingDegrees() {
    if (!targetValid()) return 0.0;

    double lateral = getTargetLateralMeters();
    double forward = getTargetForwardMeters();

    if (lateral == -1.0 && forward == -1.0) return 0.0;

    return Math.toDegrees(Math.atan2(lateral, forward)); // gets the angle of the measurement
  }
}
