package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
         
import com.revrobotics.spark.SparkMax;



public class IntakeSubsystem extends SubsystemBase {
    public SparkMax intakeMotorL;
    public SparkMax intakeMotorR;
    public SparkMax intakeMotor;
    public DigitalInput proximitySensorL;
    public DigitalInput proximitySensorR;
    private IntakeConstants.Mode mode;
    private PIDController intakePIDController;
    public DutyCycleEncoder encoderL;
    public DutyCycleEncoder encoderR;
    
    
    public void setMode(IntakeConstants.Mode mode) {
        this.mode = mode;
    }

    public IntakeSubsystem() {
        intakeMotor = new SparkMax(Constants.IntakeConstants.INTAKE_MOTOR, null);
        proximitySensorL = new DigitalInput(Constants.IntakeConstants.PROXIMITY_SENSOR_PORT_LEFT);
        proximitySensorR = new DigitalInput(Constants.IntakeConstants.PROXIMITY_SENSOR_PORT_RIGHT);
        intakeMotor.set(0);
        this.mode = IntakeConstants.Mode.OFF; // initialize default0
        

        intakePIDController = new PIDController(IntakeConstants.kP, IntakeConstants.kI, IntakeConstants.kD);
        
    }

    // Sam Notes
    // appears to still be in progress
    // Get Payton's help

    public IntakeConstants.Mode getMode() { 
        return mode;
    }


    public void run(double speed) {
        intakeMotor.set(speed);
    }



    public void runH(double speedH) {
        intakeMotorL.set(speedH);
        intakeMotorR.set(-speedH);
    }


    public void stop() {
        intakeMotor.stopMotor(); // Stop the intake motor

    }

    
    public boolean isObjectDetected() {
        return proximitySensorL.get() || proximitySensorR.get(); // NPN logic: true when object is present on either sensor
    }



    public double getMeasurement(){
        double position = 
    }
}



