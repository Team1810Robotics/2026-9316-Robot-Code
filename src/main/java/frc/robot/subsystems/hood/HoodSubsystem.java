package frc.robot.subsystems.hood;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class HoodSubsystem extends SubsystemBase {
  /** Creates a new HoodSubsystem. */
  
  public TalonFX hoodMotor;
  public Encoder hoodEncoder;
  
  // Hood speed constant
  public static final double HOOD_SPEED = 0.5;

  public HoodSubsystem() {
    hoodMotor = new TalonFX(Constants.HoodConstants.HOOD_MOTOR_ID);
    hoodEncoder = new Encoder(0, 1);
    hoodEncoder.setDistancePerPulse(360.0 / 600.0);
  }

  public Command AimHood() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }

  public void run(double speed) {
    hoodMotor.set(speed);
  }

  public void stop() {
    hoodMotor.stopMotor(); // Stop the hood motor
  }

  public void stopHood() {
    hoodMotor.stopMotor();
  }

  public void moveUp() {
    hoodMotor.set(HOOD_SPEED);
  }

  public void moveDown() {
    hoodMotor.set(-HOOD_SPEED);
  }

  public void setEncoder() {
    hoodEncoder.getDistance();
    hoodEncoder.setDistancePerPulse(360 / 600);
  }
}
