package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbSubsystem extends SubsystemBase {

  private final SparkMax leftMotor =
      new SparkMax(ClimbConstants.LEFT_CLIMB_MOTOR_ID, MotorType.kBrushless);

  private final SparkMax rightMotor =
      new SparkMax(ClimbConstants.RIGHT_CLIMB_MOTOR_ID, MotorType.kBrushless);

  public ClimbSubsystem() {

    // Make the motors oppose each other mechanically
    leftMotor.setInverted(false);
    rightMotor.setInverted(true);
  }

  public void runClimb(double speed) {

    leftMotor.set(speed);
    rightMotor.set(speed);
  }

  public void stopClimb() {

    leftMotor.stopMotor();
    rightMotor.stopMotor();
  }
}
