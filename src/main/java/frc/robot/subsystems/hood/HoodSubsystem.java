package frc.robot.subsystems.hood;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class HoodSubsystem extends SubsystemBase {
  public TalonFX hoodMotor;
  public Encoder hoodEncoder;

  public HoodSubsystem() {
    hoodEncoder = new Encoder(0, 1);
    hoodMotor = new TalonFX(Constants.HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
  }

  public void run(double speed) {
    hoodMotor.set(speed);
  }

  public void stopHood() {
    hoodMotor.stopMotor(); // Stop the hood motor
  }

  public double setHoodEncoder() {
    hoodEncoder.getDistance();
    hoodEncoder.setDistancePerPulse(360 / 1000);
    // figured out pulses per second (1 khz)
    return hoodEncoder.getDistance();
  }

  public void runUP(double speed) {
    hoodMotor.set(speed);
  }

  public void runDOWN(double speed) {
    hoodMotor.set(-speed);
  }
}

  // this code sucks
