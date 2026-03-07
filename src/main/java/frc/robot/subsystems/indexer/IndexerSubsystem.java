package frc.robot.subsystems.indexer;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase {

  private final SparkMax lowerIndexerMotor =
      new SparkMax(IndexerConstants.LOWER_INDEXER_MOTOR_ID, MotorType.kBrushless);

  private final SparkMax upperIndexerMotor =
      new SparkMax(IndexerConstants.UPPER_INDEXER_MOTOR_ID, MotorType.kBrushless);

  private final DigitalInput beamBreak1 =
      new DigitalInput(IndexerConstants.BEAM_BREAK_1_PORT);

  private final DigitalInput beamBreak2 =
      new DigitalInput(IndexerConstants.BEAM_BREAK_2_PORT);

 
//LOWER INDEXER METHODS

  public void runLowerForward() {
    lowerIndexerMotor.set(IndexerConstants.LOWER_FORWARD_SPEED);
  }

  public void runLowerReverse() {
    lowerIndexerMotor.set(IndexerConstants.LOWER_REVERSE_SPEED);
  }

  public void stopLower() {
    lowerIndexerMotor.stopMotor();
  }

 
  //UPPER INDEXER METHODS

  public void runUpperForward() {
    upperIndexerMotor.set(IndexerConstants.UPPER_FORWARD_SPEED);
  }

  public void runUpperReverse() {
    upperIndexerMotor.set(IndexerConstants.UPPER_REVERSE_SPEED);
  }

  public void feedShooter() {
    upperIndexerMotor.set(IndexerConstants.SHOOT_FEED_SPEED);
  }

  public void stopUpper() {
    upperIndexerMotor.stopMotor();
  }

 
  //BOTH INDEXERS TOGETHER
 
  public void runBothForward() {
    lowerIndexerMotor.set(IndexerConstants.LOWER_FORWARD_SPEED);
    upperIndexerMotor.set(IndexerConstants.UPPER_FORWARD_SPEED);
  }

  public void runBothReverse() {
    lowerIndexerMotor.set(IndexerConstants.LOWER_REVERSE_SPEED);
    upperIndexerMotor.set(IndexerConstants.UPPER_REVERSE_SPEED);
  }

  public void stopAll() {
    lowerIndexerMotor.stopMotor();
    upperIndexerMotor.stopMotor();
  }

  
  //BEAM BREAK STATUS METHODS

  public boolean isFuelAtLowerSensor() {
    return !beamBreak1.get();
  }

  public boolean isFuelPrimedAtShooter() {
    return !beamBreak2.get();
  }

  @Override
  public void periodic() {

    SmartDashboard.putBoolean("Fuel At Lower Sensor", isFuelAtLowerSensor());
    SmartDashboard.putBoolean("Fuel Primed At Shooter", isFuelPrimedAtShooter());
  }
}