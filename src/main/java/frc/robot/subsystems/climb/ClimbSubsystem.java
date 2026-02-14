package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbSubsystem extends SubsystemBase {
  private final SparkMax motor1 = new SparkMax(Constants.MotorIDs.CLIMB_MOTOR_1, MotorType.kBrushless);
  private final SparkMax motor2 = new SparkMax(Constants.MotorIDs.CLIMB_MOTOR_2, MotorType.kBrushless);

  // Sam Notes
  // Needs a constructor

  // set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
  // new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  // Sam's Fix: Added constructor to initialize motors (per Sam's note)
  public ClimbSubsystem() {
    // Initialize both motors to neutral/zero state
    motor1.set(0.0);
    motor2.set(0.0);
  }

  public void Extend() {
    motor1.set(0.25);
    motor2.set(0.25);
  }

  public void Stop() {
    motor1.set(0.0);
    motor2.set(0.0);
  }

  public void Retract() {
    motor1.set(-0.25);
    motor2.set(-0.25);
  }
}
