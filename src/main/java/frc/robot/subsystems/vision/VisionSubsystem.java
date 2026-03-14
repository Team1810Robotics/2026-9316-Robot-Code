package frc.robot.subsystems.vision;

import com.ctre.phoenix6.signals.RGBWColor;

import dev.doglog.DogLog;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.util.LimelightHelpers;

public class VisionSubsystem extends SubsystemBase {
  public final String limelightName;

  private boolean LED_CD = false;

  private final CommandSwerveDrivetrain drivetrain;

  public VisionSubsystem(String name, CommandSwerveDrivetrain drivetrain) {
    this.limelightName = name;
    this.drivetrain = drivetrain;

  public VisionSubsystem() {
    this.limelightName = VisionConstants.LIMELIGHT_NAME;
    LimelightHelpers.setPipelineIndex(limelightName, 2);
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
          DogLog.log("Vision/HoodSetpoint", 0.0);
        return;
    }

    LEDConstants.IDLE = false;
    LEDSubsystem.setLEDColor(
        new RGBWColor(LEDConstants.PERRYWINKLE[0], LEDConstants.PERRYWINKLE[1], LEDConstants.PERRYWINKLE[2], 0),
        false);
    LEDSubsystem.setLEDAnimation("Rainbow", false);

   

    PoseEstimate botPoseMT2 = getBotPoseMT2();
    drivetrain.addVisionMeasurement(botPoseMT2.pose, botPoseMT2.timestampSeconds);

    LED_CD = false;

  
    DogLog.log("Vision/TX", getTx());
    DogLog.log("Vision/TY", getTy());
    DogLog.log("Vision/TargetID", getTargetID());
    DogLog.log("Vision/TargetDistanceMeters", getTargetDistanceMeters());
    // Log the computed hood setpoint every loop so you can verify it in AdvantageScope
    DogLog.log("Vision/HoodSetpoint", getHoodSetpointFromTY());
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

  /**
   * Computes desired hood encoder position from current TY using a 5th-degree polynomial fit.
   * y = 1.23 - 0.271x + 0.0609x² - 5.73E-3x³ + 2.48E-4x⁴ - 3.94E-6x⁵
   * Only call this when targetValid() is true.
   */
  public double getHoodSetpointFromTY() {
    double x = getTy();
    return  1.23
          + (-0.271)   * x
          + (0.0609)   * Math.pow(x, 2)
          + (-5.73e-3) * Math.pow(x, 3)
          + (2.48e-4)  * Math.pow(x, 4)
          + (-3.94e-6) * Math.pow(x, 5);
  }

  /** Estimated forward distance (Z axis in target space) to target in meters. */
  public double getTargetForwardMeters() {
    if (!targetValid()) return -1.0;
    double[] ts = LimelightHelpers.getBotPose_TargetSpace(limelightName);
    if (ts == null || ts.length < 3) return -1.0;
    return ts[2];
  }

  /** Estimated lateral offset (X axis in target space) to target in meters. */
  public double getTargetLateralMeters() {
    if (!targetValid()) return -1.0;
    double[] ts = LimelightHelpers.getBotPose_TargetSpace(limelightName);
    if (ts == null || ts.length < 3) return -1.0;
    return ts[0];
  }

  /** Planar distance to target using forward + lateral components. */
  public double getTargetDistanceMeters() {
    double lat = getTargetLateralMeters();
    double fwd = getTargetForwardMeters();
    if (lat == -1.0 && fwd == -1.0) return -1.0;
    return Math.hypot(lat, fwd);
  }
  public double getTargetBearingDegrees() {
    if (!targetValid()) return 0.0;

    double lateral = getTargetLateralMeters();
    double forward = getTargetForwardMeters();

    if (lateral == -1.0 && forward == -1.0) return 0.0;

    return Math.toDegrees(Math.atan2(lateral, forward)); // gets the angle of the measurement
  }
}
