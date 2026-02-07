package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.FlywheelConstants;
import frc.robot.Constants;
import com.ctre.phoenix6.hardware.TalonFX;

//import com.revrobotics.spark.SparkLowLevel.MotorType;

    // Beam Break Sensors

public class FlywheelSub extends SubsystemBase {


  
  private final TalonFX leftMotor = new TalonFX(FlywheelConstants.leftMotorID);
  private final TalonFX rightMotor = new TalonFX(FlywheelConstants.rightMotorID);
  public final DigitalInput Flybreak = new DigitalInput(Constants.FlywheelBeamBreak);
        // Beam break logic (Beam break is triggered when FALSE)



    //set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
    //new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  public void runFlywheel() {
    leftMotor.set(1.0);
    rightMotor.set(1.0);
  }



  public void stopThrowing() {
    leftMotor.set(0.0);
    rightMotor.set(0.0);
  }
}