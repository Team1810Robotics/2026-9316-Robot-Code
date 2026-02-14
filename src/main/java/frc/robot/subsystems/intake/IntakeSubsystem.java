package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import com.revrobotics.spark.SparkMax;

public class IntakeSubsystem extends SubsystemBase {
    // Side motors (over-the-bumper intake - move material up into bot)
    private final SparkMax motorLeft;
    private final SparkMax motorRight;
    
    // Wheel motor (spins intake wheels to grab game pieces)
    private final SparkMax motorWheels;
    
    // Proximity sensors (detect when material is present)
    private final DigitalInput proximitySensorLeft;
    private final DigitalInput proximitySensorRight;
    
    // Current intake mode
    private IntakeConstants.Mode mode;

    public IntakeSubsystem() {
        // Initialize side motors (inverted, facing outward)
        motorLeft = new SparkMax(Constants.MotorIDs.INTAKE_MOTOR_LEFT, null);
        motorRight = new SparkMax(Constants.MotorIDs.INTAKE_MOTOR_RIGHT, null);
        // TODO: Set motor inversion via SparkMaxConfig.setInverted() after configuration
        
        // Initialize wheel motor (spins intake wheels)
        motorWheels = new SparkMax(Constants.MotorIDs.INTAKE_MOTOR_WHEELS, null);
        
        // Initialize proximity sensors
        proximitySensorLeft = new DigitalInput(Constants.IntakeConstants.PROXIMITY_SENSOR_PORT_LEFT);
        proximitySensorRight = new DigitalInput(Constants.IntakeConstants.PROXIMITY_SENSOR_PORT_RIGHT);
        
        // Stop all motors
        stop();
        
        // Set default mode
        this.mode = IntakeConstants.Mode.OFF;
    }

    /**
     * Run the intake system
     * First runs side motors, then wheels
     */
    public void run(double sideSpeed, double wheelSpeed) {
        // Run side motors first at same speed
        motorLeft.set(sideSpeed);
        motorRight.set(sideSpeed);
        
        // Then run wheel motor
        motorWheels.set(wheelSpeed);
        
        this.mode = IntakeConstants.Mode.ON;
    }

    /**
     * Run just the side motors (moving up into bot)
     */
    public void runSideMotors(double speed) {
        motorLeft.set(speed);
        motorRight.set(speed);
    }

    /**
     * Run just the wheel motor (spinning intake wheels)
     */
    public void runWheelMotor(double speed) {
        motorWheels.set(speed);
    }

    /**
     * Stop all intake motors
     */
    public void stop() {
        motorLeft.stopMotor();
        motorRight.stopMotor();
        motorWheels.stopMotor();
        this.mode = IntakeConstants.Mode.OFF;
    }

    /**
     * Check if game piece is detected
     * NPN logic: returns true when object is present
     */
    public boolean isObjectDetected() {
        return proximitySensorLeft.get() || proximitySensorRight.get();
    }

    /**
     * Check if left sensor detects object
     */
    public boolean isObjectDetectedLeft() {
        return proximitySensorLeft.get();
    }

    /**
     * Check if right sensor detects object
     */
    public boolean isObjectDetectedRight() {
        return proximitySensorRight.get();
    }

    /**
     * Set intake mode
     */
    public void setMode(IntakeConstants.Mode mode) {
        this.mode = mode;
    }

    /**
     * Get current intake mode
     */
    public IntakeConstants.Mode getMode() {
        return mode;
    }
} 



