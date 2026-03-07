package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HoodSubsystem extends SubsystemBase {
  public int hoodEncoderRotations = 0;
  public boolean hoodEncoderWasHigh = false;
  private final TalonFX hoodMotor;
  public final DutyCycleEncoder hoodEncoder;

  /** Creates a new HoodSubsystem. */
  public HoodSubsystem() {
    hoodEncoder = new DutyCycleEncoder(1);
    hoodMotor = new TalonFX(HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
    configureMotor();
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putData("Absolute Encoder", hoodEncoder);
  }

  /*
  public Command AimHood() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    hoodEncoder = new DutyCycleEncoder(0);
    hoodMotor = new TalonFX(Constants.HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
    return runOnce(
        () -> {
          // one-time action goes here
        });
  }
  */
  private void configureMotor() {
    var outCfg = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Brake);

    var ramps =
        new OpenLoopRampsConfigs().withDutyCycleOpenLoopRampPeriod(0.25); // smooth starts/stops

    var current =
        new CurrentLimitsConfigs()
            .withStatorCurrentLimitEnable(true)
            .withStatorCurrentLimit(40) // start conservative
            .withSupplyCurrentLimitEnable(true)
            .withSupplyCurrentLimit(35); // start conservative

    hoodMotor.getConfigurator().apply(outCfg);
    hoodMotor.getConfigurator().apply(ramps);
    hoodMotor.getConfigurator().apply(current);
  }

  public void run(double speed) {
    hoodMotor.set(speed);
  }

  public void stopHood() {
    hoodMotor.stopMotor(); // Stop the hood motor
  }

  public double getHoodEncoder() {

    // figured out pulses per second (1 khz)
    return hoodEncoder.get();
  }

  public void runUP(double speed) {
    hoodMotor.set(speed);
  }

  public void runDOWN(double speed) {
    hoodMotor.set(-speed);
  }
}
