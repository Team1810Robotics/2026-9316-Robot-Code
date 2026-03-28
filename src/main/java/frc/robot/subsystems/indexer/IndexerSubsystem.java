package frc.robot.subsystems.indexer;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase {
  private final SparkMax indexer1Motor =
      new SparkMax(IndexerConstants.INDEXER_1_MOTOR_ID, MotorType.kBrushless);

  private final SparkMax indexer2Motor =
      new SparkMax(IndexerConstants.INDEXER_2_MOTOR_ID, MotorType.kBrushless);

  private boolean shootingEnabled = false;
  private boolean shooterReady = false;
  private boolean reverseEnabled = false;

  public IndexerSubsystem() {}

  public void setShooting(boolean enabled) {
    shootingEnabled = enabled;
  }

  public void setShooterReady(boolean ready) {
    shooterReady = ready;
  }

  public void setReverseBoth(boolean enabled) {
    reverseEnabled = enabled;
  }

  public void stopIndexer1() {
    indexer1Motor.set(0);
  }

  public void stopIndexer2() {
    indexer2Motor.set(0);
  }

  public void stopAll() {
    shootingEnabled = false;
    shooterReady = false;
    reverseEnabled = false;

    indexer1Motor.set(0);
    indexer2Motor.set(0);
  }

  public void runBothForward() {
    shootingEnabled = true;
    shooterReady = true;
    reverseEnabled = false;
  }

  public void runBothReverse() {
    shootingEnabled = false;
    shooterReady = false;
    reverseEnabled = true;
  }

  @Override
  public void periodic() {
    if (reverseEnabled) {
      indexer1Motor.set(IndexerConstants.INDEXER_1_REVERSE_SPEED);
      indexer2Motor.set(IndexerConstants.INDEXER_2_REVERSE_SPEED);
    } else if (shootingEnabled && shooterReady) {
      indexer1Motor.set(IndexerConstants.INDEXER_1_SPEED);
      indexer2Motor.set(IndexerConstants.INDEXER_2_SHOOTER_SPEED);
    } else {
      indexer1Motor.set(0);
      indexer2Motor.set(0);
    }

    // SmartDashboard.putBoolean("Shooting Enabled", shootingEnabled);
    // SmartDashboard.putBoolean("Shooter Ready", shooterReady);
    // SmartDashboard.putBoolean("Reverse Enabled", reverseEnabled);
  }
}