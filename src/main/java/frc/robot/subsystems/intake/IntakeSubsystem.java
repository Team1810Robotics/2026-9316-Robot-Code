package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
  public SparkMax intakeMotor;
  public SparkMax intakeMotorL;
  public SparkMax intakeMotorR;
  public Encoder intakeEncoder;
  
  // PID constants for intake arm
  public static final double kP = 0.1;
  public static final double kI = 0.0;
  public static final double kD = 0.0;
  
  // Current arm target position
  private double targetPosition = 0.0;

  public IntakeSubsystem() {
    intakeMotor =
        new SparkMax(
            IntakeConstants.INTAKE_MOTOR, com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
    intakeMotor.set(0);
    intakeMotorL =
        new SparkMax(
            IntakeConstants.INTAKE_MOTOR_L,
            com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
    intakeMotorR =
        new SparkMax(
            IntakeConstants.INTAKE_MOTOR_R,
            com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
    intakeEncoder = new Encoder(0, 1);
  }

  public void run(double speed) {
    intakeMotor.set(speed);
  }

  public void stopIntake() {
    intakeMotor.stopMotor(); // Stop the intake motor
  }

  public void stopIntakeLevel() {
    intakeMotorL.stopMotor();
    intakeMotorR.stopMotor();
  }

  public void runUP(double speed) {
    intakeMotorL.set(speed);
    intakeMotorR.set(-speed);
  }

  public void runDOWN(double speed) {
    intakeMotorL.set(-speed);
    intakeMotorR.set(speed);
  }

  public double setIntakeEncoder() {
    intakeEncoder.getDistance();
    intakeEncoder.setDistancePerPulse(360 / 8192);
    return intakeEncoder.getDistance();
  }
  
  /**
   * Set the intake arm to a target position using simple P control
   * @param position Target position in encoder units
   */
  public void setPosition(double position) {
    targetPosition = position;
    double currentPosition = intakeEncoder.getDistance();
    double error = targetPosition - currentPosition;
    
    // Simple P control - adjust speed based on error
    double output = error * kP;
    output = Math.max(-0.5, Math.min(0.5, output)); // Clamp to max 50% speed
    
    // Run both motors in opposite directions for linear actuation
    intakeMotorL.set(output);
    intakeMotorR.set(-output);
  }
}
