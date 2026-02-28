package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;


import edu.wpi.first.wpilibj.DutyCycle;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class HoodSubsystem extends SubsystemBase {
  public TalonFX hoodMotor;
  public Encoder hoodEncoder;

  public HoodSubsystem() {
    hoodEncoder = new Encoder(0, 1);
    hoodMotor = new TalonFX(Constants.HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
  }

   // Safe starter config; tune as you learn the mechanism.
private void configureMotor() {
   
    var outCfg = new MotorOutputConfigs()
        .withNeutralMode(NeutralModeValue.Brake);

    var ramps = new OpenLoopRampsConfigs()
        .withDutyCycleOpenLoopRampPeriod(0.25); // smooth starts/stops

    var current = new CurrentLimitsConfigs()
        .withStatorCurrentLimitEnable(true)
        .withStatorCurrentLimit(40)  // start conservative
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

