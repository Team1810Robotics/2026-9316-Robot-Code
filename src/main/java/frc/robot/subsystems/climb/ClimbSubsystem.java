package frc.robot.subsystems.climb;

import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.ctre.phoenix6.signals.RGBWColor;
import com.revrobotics.spark.SparkLowLevel.MotorType;


public class ClimbSubsystem extends SubsystemBase {
  // private final SparkMax motor1;
  // private final SparkMax motor2;

  // public boolean isExtended = false;

  public ClimbSubsystem() {
    // motor1 = new SparkMax(ClimbConstants.motor1ID, MotorType.kBrushless);
    /* motor2 = new SparkMax(ClimbConstants.motor2ID, MotorType.kBrushless);

     SparkMaxConfig config = new SparkMaxConfig();
     config.follow(motor1.getDeviceId());
    */
    // config.inverted(true);

    // motor2.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void Extend() {
  //  motor1.set(ClimbConstants.SPEED);
  // motor2.set(-ClimbConstants.SPEED);
  // LEDConstants.IDLE = false;
  // LEDSubsystem.setLEDColor(
  //     new RGBWColor(LEDConstants.GREEN[0], LEDConstants.GREEN[1], LEDConstants.GREEN[2], 0),
  //      false);
  //  LEDSubsystem.setLEDAnimation("Fire", false);
  }

  public void Stop() {
 //   motor1.set(0.0);
 //   motor2.set(0);
  }

  public void Retract() {
  //  motor1.set(-ClimbConstants.SPEED);
  //  motor2.set(ClimbConstants.SPEED);
  //  LEDConstants.IDLE = true;
  }
}
