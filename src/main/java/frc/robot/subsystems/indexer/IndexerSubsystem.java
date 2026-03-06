package frc.robot.subsystems.indexer;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase {
  private final SparkMax indexerMotor1 =
      new SparkMax(0, MotorType.kBrushless); // The Left roller mot
  private final SparkMax indexerMotor2 =
      new SparkMax(1, MotorType.kBrushless); // The right roller motor
  private final SparkMax indexerMotor3 =
      new SparkMax(2, MotorType.kBrushless); // The left orange wheel motor
  private final SparkMax indexerMotor4 =
      new SparkMax(3, MotorType.kBrushless); // The right orange wheel motor

  private final DigitalInput IndexBeamBreak =
      new DigitalInput(IndexerConstants.INDEXER_BEAM_BREAK_SENSOR_PORT); // Index Beam Break Sensor

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
