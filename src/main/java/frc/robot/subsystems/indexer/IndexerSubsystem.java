package frc.robot.subsystems.indexer;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase {
  private final SparkMax indexer1Motor =
      new SparkMax(IndexerConstants.INDEXER_1_MOTOR_ID, MotorType.kBrushless);

  private final SparkMax indexer2Motor =
      new SparkMax(IndexerConstants.INDEXER_2_MOTOR_ID, MotorType.kBrushless);

  // private final DigitalInput index2BeamBreak =
  //     new DigitalInput(IndexerConstants.INDEXER_2_BEAM_BREAK_SENSOR_PORT);

  private boolean indexingEnabled = false;
  private boolean shootingEnabled = false;
  private boolean shooterReady = false;
  private boolean reverse1Enabled = false;
  private boolean reverse2Enabled = false;

  public IndexerSubsystem() {}

  // public boolean isIndex2Broken() {
  //   return !index2BeamBreak.get();
  // }

  public void setIndexingEnabled(boolean enabled) {
    indexingEnabled = enabled;
  }

  public void setShooting(boolean enabled) {
    shootingEnabled = enabled;
  }

  public void setShooterReady(boolean ready) {
    shooterReady = ready;
  }

  public void setReverse1(boolean enabled) {
    reverse1Enabled = enabled;
  }

  public void setReverse2(boolean enabled) {
    reverse2Enabled = enabled;
  }

  public void setReverseBoth(boolean enabled) {
    reverse1Enabled = enabled;
    reverse2Enabled = enabled;
  }

  public void stopIndexer1() {
    indexer1Motor.set(0);
  }

  public void stopIndexer2() {
    indexer2Motor.set(0);
  }

  public void stopAll() {
    indexingEnabled = false;
    shootingEnabled = false;
    shooterReady = false;
    reverse1Enabled = false;
    reverse2Enabled = false;

    indexer1Motor.set(0);
    indexer2Motor.set(0);
  }

  // Used for bumper/intake feed
  public void runIndexFeed() {
    indexingEnabled = true;
    shootingEnabled = false;
    shooterReady = false;
    reverse1Enabled = false;
    reverse2Enabled = false;
  }

  // Used for shooter feed after flywheel/hood are ready
  public void runShooterFeed() {
    indexingEnabled = false;
    shootingEnabled = true;
    shooterReady = true;
    reverse1Enabled = false;
    reverse2Enabled = false;
  }

  public void prepareToShoot() {
    indexingEnabled = false;
    shootingEnabled = true;
    shooterReady = false;
    reverse1Enabled = false;
    reverse2Enabled = false;
  }

  public void runBothReverse() {
    indexingEnabled = false;
    shootingEnabled = false;
    shooterReady = false;
    reverse1Enabled = true;
    reverse2Enabled = true;
  }

  @Override
  public void periodic() {
    // Motor 1 logic
    if (reverse1Enabled) {
      indexer1Motor.set(IndexerConstants.INDEXER_1_REVERSE_SPEED);
    } else if (shootingEnabled && shooterReady) {
      indexer1Motor.set(IndexerConstants.INDEXER_1_SPEED);
    } //else if (indexingEnabled) {
      //indexer1Motor.set(IndexerConstants.INDEXER_1_SPEED);}
     else {
      indexer1Motor.set(0);
    }

    // Motor 2 logic
    if (reverse2Enabled) {
      indexer2Motor.set(IndexerConstants.INDEXER_2_REVERSE_SPEED);
    } else if (shootingEnabled && shooterReady) {
      indexer2Motor.set(IndexerConstants.INDEXER_2_SHOOTER_SPEED);
    } //else if (indexingEnabled) {
      //indexer2Motor.set(IndexerConstants.INDEXER_2_SPEED);}
     else {
      indexer2Motor.set(0);
    }
  }
}