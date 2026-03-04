package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class HoodSubsystem extends SubsystemBase {
  /** Creates a new HoodSubsystem. */
  public HoodSubsystem() {}

  public TalonFX hoodMotor;
  public DutyCycleEncoder hoodEncoder;

  public Command AimHood() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    hoodEncoder = new DutyCycleEncoder(0);
    hoodMotor = new TalonFX(Constants.HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
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

  public void setEncoder() {
    hoodEncoder.getDistance();

    // TODO: figure out pulses per rotation and set as denomenator should be 600 according to ai
    hoodEncoder.setDistancePerPulse(360 / 600);
  }
}

  // this code sucks
