package frc.robot.subsystems.indexer;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase {
  private final SparkMax indexerMotor1 = new SparkMax(0, null); // The Left roller mot
  private final SparkMax indexerMotor2 = new SparkMax(0, null); // The right roller motor
  private final SparkMax indexerMotor3 = new SparkMax(0, null); // The left orange wheel motor
  private final SparkMax indexerMotor4 = new SparkMax(0, null); // The right orange wheel motor

  private final DigitalInput IndexBeamBreak =
      new DigitalInput(IndexerConstants.INDEXER_BEAM_BREAK_SENSOR_PORT); // Index Beam Break Sensor

  // Sam Notes
  // Needs a constructor
  // Looks good though

  public void RunIndexer() {
    indexerMotor1.set(IndexerConstants.INDEXER_SPEED);
    indexerMotor2.set(IndexerConstants.INDEXER_SPEED);
    indexerMotor3.set(IndexerConstants.INDEXER_SPEED);
    indexerMotor4.set(IndexerConstants.INDEXER_SPEED);
  }

  public void StopIndexer() {
    indexerMotor1.set(0);
    indexerMotor2.set(0);
    indexerMotor3.set(0);
    indexerMotor4.set(0);
  }

  public void BeamBreakStop() {
    indexerMotor3.set(0);
    indexerMotor4.set(0);
  }

  @Override
  public void periodic() {
    boolean indexBroken = !IndexBeamBreak.get();

    if (indexBroken) {
      BeamBreakStop();
    } else {
      RunIndexer();
    }
  }
}
