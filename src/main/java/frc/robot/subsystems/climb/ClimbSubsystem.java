package frc.robot.subsystems.climb;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbSubsystem extends SubsystemBase {
  private final SparkMax motor1;
  private final SparkMax motor2;

  public boolean isExtended = false;

  public ClimbSubsystem() {
    motor1 = new SparkMax(ClimbConstants.motor1ID, MotorType.kBrushless);
    motor2 = new SparkMax(ClimbConstants.motor2ID, MotorType.kBrushless);

    SparkMaxConfig config = new SparkMaxConfig();
    config.follow(motor1.getDeviceId());
    config.inverted(true);

    motor2.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

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
