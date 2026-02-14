package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import frc.robot.subsystems.intake.IntakeConstants;         
import com.revrobotics.spark.SparkMax;



public class IntakeSubsystem extends SubsystemBase {
    public SparkMax intakeMotor;
  
    private IntakeConstants.Mode mode;
   
    
    
    public void setMode(IntakeConstants.Mode mode) {
        this.mode = mode;
    }



    public IntakeSubsystem() {
        intakeMotor = new SparkMax(IntakeConstants.INTAKE_MOTOR, null);
   
        intakeMotor.set(0);
        this.mode = IntakeConstants.Mode.OFF; // initialize default0
        

        
        
    }

    public IntakeConstants.Mode getMode() { 
        return mode;
    }

    

    public void run(double speed) {
        intakeMotor.set(speed);
    }


    public void stop() {
        intakeMotor.stopMotor(); // Stop the intake motor
    }


}



