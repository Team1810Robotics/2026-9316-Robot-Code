package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;
         
import com.revrobotics.spark.SparkMax;



public class IntakeSubsystem extends SubsystemBase {
    public SparkMax intakeMotor;
    public DigitalInput proximitySensorL;
    public DigitalInput proximitySensorR;
    private IntakeConstants.Mode mode;
    
    
    public void setMode(IntakeConstants.Mode mode) {
        this.mode = mode;
    }

    public IntakeSubsystem() {
        intakeMotor = new SparkMax(Constants.IntakeConstants.INTAKE_MOTOR, null);
        proximitySensorL = new DigitalInput(Constants.IntakeConstants.PROXIMITY_SENSOR_PORT_LEFT);
        proximitySensorR = new DigitalInput(Constants.IntakeConstants.PROXIMITY_SENSOR_PORT_RIGHT);
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

    
    public boolean isObjectDetected() {
        return proximitySensorL.get() || proximitySensorR.get(); // NPN logic: true when object is present on either sensor
    }
}



