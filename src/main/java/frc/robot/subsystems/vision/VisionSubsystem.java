package frc.robot.subsystems.vision;

import dev.doglog.DogLog;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.LimelightHelpers;

public class VisionSubsystem extends SubsystemBase {
  public final String limelightName;

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
      return;
    }

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
