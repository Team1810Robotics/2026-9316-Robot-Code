package frc.robot.subsystems.flywheel;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// import com.revrobotics.spark.SparkLowLevel.MotorType;

// Beam Break Sensors

public class FlywheelSubsystem extends SubsystemBase {

  private final TalonFX leftMotor = new TalonFX(FlywheelConstants.leftMotorID);
  private final TalonFX rightMotor = new TalonFX(FlywheelConstants.rightMotorID);
  public final DigitalInput Flybreak = new DigitalInput(FlywheelConstants.FlywheelBeamBreak);

  // Beam break logic (Beam break is triggered when FALSE)

  // set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
  // new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  // Sam Notes
  // Needs a constructor
  // Needs a function to return the beambreak value
  // Needs to be abe to set to a specific motor velocity, not just a set power

  public void runFlywheel() {
    leftMotor.set(1.0);
    rightMotor.set(1.0);
  }

  public void stopThrowing() {
    leftMotor.set(0.0);
    rightMotor.set(0.0);
  }
}
