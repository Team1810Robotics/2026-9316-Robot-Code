package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSubsystem extends SubsystemBase {
  public final SparkFlex intakeMotor;
  public final SparkMax intakeMotorL;
  public final SparkMax intakeMotorR;
  public final DutyCycleEncoder intakeEncoder;

  //public final double targetPosition;
  private final PIDController intakePIDController;
 
  // private double kP;
  // private double kI;
  // private double kD;

   private double currentSetPoint = 0;

  public IntakeSubsystem() {
    intakeMotor =
        new SparkFlex(
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

    intakeEncoder = new DutyCycleEncoder(IntakeConstants.INTAKE_ENCODER_DIO);

    intakePIDController =
        new PIDController(IntakeConstants.kP, IntakeConstants.kI, IntakeConstants.kD);

    intakePIDController.setTolerance(IntakeConstants.PIVOT_TOLERANCE); // Set the tolerance for considering the intake to be at the setpoint
  }

  public void runIntakeMotor(double speed) {
    intakeMotor.set(speed);
  }

  public void setPoint(double setpoint) {
    currentSetPoint = setpoint;
    if (intakeEncoder.isConnected()) {
      double output = intakePIDController.calculate(intakeEncoder.get(), setpoint);
      
    output = Math.max(-IntakeConstants.MAX_PIVOT_OUTPUT, Math.min(IntakeConstants.MAX_PIVOT_OUTPUT, output)); // Limit the output to the maximum allowed value
    
      intakeMotorL.set(output);
      intakeMotorR.set(-output);
    } else {
      stopIntakeLevel();
    }
  }

  public void stopIntake() {
    intakeMotor.stopMotor(); // Stop the intake motor
  }

  public void stopIntakeLevel() {
    intakeMotorL.stopMotor();
    intakeMotorR.stopMotor();
  }

  public void runUp(double speed) {
    if (intakeEncoder.isConnected() || intakeEncoder.get() <= IntakeConstants.OUT_POSITION) {
        intakeMotorL.set(-Math.abs(speed)); // Ensure the speed is positive for running up
        intakeMotorR.set(Math.abs(speed));
    }
    
  }

  public void runDown(double speed) {
    if (intakeEncoder.isConnected() || intakeEncoder.get() >= IntakeConstants.IN_POSITION) {
    intakeMotorL.set(Math.abs(speed)); // Ensure the speed is positive for running down
    intakeMotorR.set(-Math.abs(speed));
    }
  }

  public double getIntakeEncoder() {
    return intakeEncoder.get();
  }

  public double getIntakeEncoderDegrees() {
    return intakeEncoder.get() * 360.0; // Convert the duty cycle reading to degrees (assuming 1 rotation = 360 degrees)
  }

  public double getCurrentSetPoint() {
    return currentSetPoint;
  }

  public boolean isAtSetPoint() {
    return intakePIDController.atSetpoint();
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Intake Encoder Raw", getIntakeEncoder());
    SmartDashboard.putNumber("Intake Encoder Degrees", getIntakeEncoderDegrees());
    SmartDashboard.putNumber("Intake Setpoint", getCurrentSetPoint());
    SmartDashboard.putBoolean("Intake At Setpoint", isAtSetPoint());
    SmartDashboard.putBoolean("Intake Encoder Connected", intakeEncoder.isConnected());
  }

}
