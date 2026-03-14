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

  public VisionSubsystem() {
    this.limelightName = VisionConstants.LIMELIGHT_NAME;

    LimelightHelpers.setPipelineIndex(limelightName, 2);
  }

  @Override
  public void periodic() {
    if (!targetValid()) {
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

    DogLog.log("Vision/TargetValid", targetValid());
    DogLog.log("Vision/TX", getTx());
    DogLog.log("Vision/TY", getTy());
    DogLog.log("Vision/TargetID", getTargetID());
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

}