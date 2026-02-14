package frc.robot.subsystems.indexer;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.IndexerConstants;

/**
 * IndexerSubsystem - Single conveyor belt motor for indexing game pieces
 * Similar to luggage conveyor at airport - moves pieces through the robot
 */
public class IndexerSubsystem extends SubsystemBase {
  // Single conveyor belt motor
  private final SparkMax indexerMotor = new SparkMax(Constants.MotorIDs.INDEXER_MOTOR, null);

  // Beam break sensor - detects when something is in the indexer
  private final DigitalInput beamBreak = new DigitalInput(IndexerConstants.INDEXER_BEAM_BREAK_SENSOR_PORT);

  /**
   * Constructor - initializes motor to stopped state
   */
  public IndexerSubsystem() {
    indexerMotor.set(0);
  }

  /**
   * Run the conveyor belt at configured speed
   */
  public void RunIndexer() {
    indexerMotor.set(IndexerConstants.INDEXER_SPEED);
  }

  /**
   * Stop the conveyor belt
   */
  public void StopIndexer() {
    indexerMotor.set(0);
  }

  /**
   * Check if beam break is triggered (piece detected in indexer)
   * NPN logic: returns true when blocked (no signal)
   */
  public boolean isBeamBroken() {
    return !beamBreak.get();
  }

  @Override
  public void periodic() {
    // Auto-stop conveyor when something is detected at the end
    if (isBeamBroken()) {
      StopIndexer();
    }
  }
}
