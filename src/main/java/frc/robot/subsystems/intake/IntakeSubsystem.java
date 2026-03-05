package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkFlex;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycle;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
  public SparkFlex intakeMotor;
  public SparkMax intakeMotorL;
  public SparkMax intakeMotorR;
  public DutyCycleEncoder intakeEncoder;
  private final PIDController intakePIDController;
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
    intakeEncoder = new DutyCycleEncoder(0);

    intakePIDController = new PIDController(IntakeConstants.kP, IntakeConstants.kI, IntakeConstants.kD);
    
  }

  public void run(double speed) {
    intakeMotor.set(speed);
  }

  public void setPoint(double setpoint) {
    currentSetPoint = setpoint;
    if (intakeEncoder.isConnected()){
            double output = intakePIDController.calculate(intakeEncoder.get(), setpoint);
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

  public void runUP(double speed) {
    intakeMotorL.set(speed);
    intakeMotorR.set(-speed);
  }

  public void runDOWN(double speed) {
    intakeMotorL.set(-speed);
    intakeMotorR.set(speed);
  }

  public double getIntakeEncoder() {
    return intakeEncoder.get();
  }
}
