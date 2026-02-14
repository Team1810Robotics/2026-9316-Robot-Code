package frc.robot.subsystems.hood;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.HoodConstants;

public class HoodSubsystem extends SubsystemBase {
  /** Creates a new HoodSubsystem. */
  public HoodSubsystem() {}
    public TalonFX hoodMotor;
    private HoodConstants.Mode mode;
    public DutyCycleEncoder hoodEncoder;

     public void setMode(HoodConstants.Mode mode) {
        this.mode = mode;
          this.mode = HoodConstants.Mode.OFF; // initialize default0
    }
  
  public Command AimHood() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    
    hoodMotor = new TalonFX(Constants.HoodConstants.HOOD_MOTOR_ID);
        hoodMotor.set(0);
        this.mode = HoodConstants.Mode.OFF; // initialize default0
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
  }



  // this code sucks