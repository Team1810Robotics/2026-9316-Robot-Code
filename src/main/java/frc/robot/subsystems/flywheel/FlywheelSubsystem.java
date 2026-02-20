package frc.robot.subsystems.flywheel;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.VelocityVoltage;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class FlywheelSubsystem extends SubsystemBase {

  private final TalonFX leftMotor = new TalonFX(Constants.MotorIDs.FLYWHEEL_MOTOR_LEFT);
  private final TalonFX rightMotor = new TalonFX(Constants.MotorIDs.FLYWHEEL_MOTOR_RIGHT);
  public final DigitalInput Flybreak = new DigitalInput(Constants.FlywheelConstants.BEAM_BREAK_PORT);

  // VelocityVoltage controller for precise RPM control (TalonFX built-in)
  private final VelocityVoltage velocityControl = new VelocityVoltage(0);

  // Target velocity in rotations per second (RPS)
  private double targetVelocity = 0.0;

  // Flywheel state tracking for diagnostics and control
  private enum FlywheelState {
    STOPPED, SPINNING_UP, AT_SPEED
  }
  private FlywheelState state = FlywheelState.STOPPED;

  public FlywheelSubsystem() {
    // Initialize both motors to neutral/zero state
    leftMotor.set(0.0);
    rightMotor.set(0.0);
  }

  public boolean getBeamBreakTriggered() {
    // Beam break is triggered when FALSE (NPN sensor logic)
    return !Flybreak.get();
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
    // Convert percentage (0.0 to 1.0) to raw power command
    leftMotor.set(powerPercent);
    rightMotor.set(powerPercent);
  }

  // Legacy methods for compatibility
  public void runFlywheel() {
    setFlywheelPower(1.0);
  }

  public void stopThrowing() {
    setFlywheelVelocity(0.0);
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
  public boolean isAtTargetSpeed() {
    double tolerance = 2.0; // RPS tolerance (adjust as needed)
    return Math.abs(getCurrentVelocity() - targetVelocity) < tolerance;
  }

  // Get current state for debugging
  public String getFlywheelState() {
    return state.toString();
  }

  @Override
  public void periodic() {
    // Update state machine based on current performance
    if (targetVelocity == 0.0) {
      state = FlywheelState.STOPPED;
    } else if (isAtTargetSpeed()) {
      state = FlywheelState.AT_SPEED;
    } else {
      state = FlywheelState.SPINNING_UP;
    }
  }
}
