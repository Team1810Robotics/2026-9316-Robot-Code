package frc.robot.subsystems.hood;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.DutyCycle;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class HoodSubsystem extends SubsystemBase {
  private TalonFX hoodMotor;
  private DutyCycleEncoder hoodEncoder;

  public HoodSubsystem() {
    hoodEncoder = new DutyCycleEncoder(0);
    hoodMotor = new TalonFX(HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
  }

  public void run(double speed) {
    hoodMotor.set(speed);
  }

  public void stopHood() {
    hoodMotor.stopMotor(); // Stop the hood motor
  }

    public double getHoodEncoder() {
    
      //figured out pulses per second (1 khz)
      return hoodEncoder.get();
  }
  public void runUP(double speed) {
    hoodMotor.set(speed);
  }

  public void runDOWN(double speed) {
    hoodMotor.set(-speed);
  }
}

  // this code sucks

