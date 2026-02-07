package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// import com.revrobotics.spark.SparkLowLevel.MotorType;
public class ClimbSubsystem extends SubsystemBase {
  private final SparkMax motor1 = new SparkMax(ClimbConstants.motor1ID, MotorType.kBrushless);

  // set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
  // new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  public void Extend() {
    motor1.set(0.25);
  }

  public void Stop() {
    motor1.set(0.0);
  }

  public void Retract() {
    motor1.set(-0.25);
  }
}
