package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbSubsystem extends SubsystemBase {
private SparkMax motor1;

public boolean isExtended = false;

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