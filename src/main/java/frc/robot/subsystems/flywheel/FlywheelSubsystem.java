package frc.robot.subsystems.flywheel;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.VelocityVoltage;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class FlywheelSubsystem extends SubsystemBase {

  private final TalonFX leftMotor;
  private final TalonFX rightMotor;
  private final DigitalInput beamBreak;

  // VelocityVoltage controller for precise RPM control (TalonFX built-in)
  private final VelocityVoltage velocityControl = new VelocityVoltage(0);
  //PID stuff-- have sam check
  private PIDController flywheelPIDController;
  public double currentSetpoint;

  
 
  // Target velocity in rotations per second (RPS)
  private double targetVelocity = 200.0;
  private double CurrentVelocity;

  // Flywheel state tracking for diagnostics and control
  private enum FlywheelState {
    STOPPED, SPINNING_UP, AT_SPEED
  }
  private FlywheelState state = FlywheelState.STOPPED;

  public FlywheelSubsystem() {
    leftMotor = new TalonFX(FlywheelConstants.leftMotorID);
    rightMotor = new TalonFX(FlywheelConstants.rightMotorID);

    beamBreak = new DigitalInput(FlywheelConstants.FlywheelBeamBreak);
    
    //TODO : Tune PID constants for flywheel velocity control
    flywheelPIDController = new PIDController(FlywheelConstants.kP, FlywheelConstants.kI, FlywheelConstants.kD);
    // Calculates the output of the PID algorithm based on the sensor reading
// and sends it to a motor
leftMotor.set(flywheelPIDController.calculate(getCurrentVelocity(), targetVelocity));
rightMotor.set(flywheelPIDController.calculate(getCurrentVelocity(), targetVelocity));

    //TODO: Configure motor settings (inversions, PID gains) here
  }

  public boolean getBeamBreakTriggered() {
    // Beam break is triggered when FALSE (NPN sensor logic)
    return !beamBreak.get();
  }

  // Set flywheel to specific velocity in rotations per second (RPS)
  public void setFlywheelVelocity(double velocityRPS) {
    this.targetVelocity = velocityRPS;
    // Use VelocityVoltage control for precise speed management
    leftMotor.setControl(velocityControl.withVelocity(velocityRPS));
    rightMotor.setControl(velocityControl.withVelocity(velocityRPS));
  }

  // Set flywheel to percentage power (legacy method for simple control)
  public void setFlywheelPower(double powerPercent) {
 
    leftMotor.set(powerPercent);
    rightMotor.set(powerPercent);
  }

  // Get current velocity in RPS (for diagnostics and feedback)
  public double getCurrentVelocity() {
    // Average velocity of both motors
    double leftVel = leftMotor.getVelocity().getValueAsDouble();
    double rightVel = rightMotor.getVelocity().getValueAsDouble();
    return (leftVel + rightVel) / 2.0;
  }

  // Get target velocity
  public double getTargetVelocity() {
    return targetVelocity;
   
  }

  // Check if flywheel is at target speed (within tolerance)
  public boolean isAtTargetVelocity() {
    double tolerance = 2.0; // RPS tolerance (adjust as needed)
    return Math.abs(getCurrentVelocity() - targetVelocity) < tolerance;
  }

  // Get current state for debugging
  public String getFlywheelState() {
    return state.toString();
  }

  @Override
  public void periodic() {
   CurrentVelocity = getCurrentVelocity();
    if (targetVelocity == 0.0) {
      state = FlywheelState.STOPPED;
    } else if (isAtTargetVelocity()) {
      state = FlywheelState.AT_SPEED;
    } else {
      state = FlywheelState.SPINNING_UP;
    }
  }
  
    //not inverted motor-- TODO double check motor ID
    public void run(double setPoint) {
    if (currentSetpoint == setPoint) {
      targetVelocity = setPoint;
    }else if (getCurrentVelocity() != setPoint) {
      rightMotor.set(clamp(flywheelPIDController.calculate(getMeasurment(), setPoint)));
      if (flywheelPIDController.calculate(getMeasurment(), setPoint) > highestPower) {
        highestPower = flywheelPIDController.calculate(getMeasurment(), setPoint);
      }
      
 //inverted motor-- TODO double check motor ID
    public void run(double setPoint) {
    if (currentSetpoint == setPoint) {
      targetVelocity = !setPoint;
    }else if (getCurrentVelocity() != setPoint) {
      pitchMotor.set(clamp(flywheelPIDController.calculate(getMeasurment(), setPoint)));
      if (flywheelPIDController.calculate(getMeasurment(), setPoint) > highestPower) {
        highestPower = flywheelPIDController.calculate(getMeasurment(), setPoint);
      }
}
}
    }
