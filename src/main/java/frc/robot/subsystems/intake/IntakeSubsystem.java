package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
  public SparkFlex intakeMotor;
  public SparkMax intakeMotorL;
  public SparkMax intakeMotorR;
  public DutyCycleEncoder intakeEncoder;
  public double targetPosition;
  private final PIDController intakePIDController;
  private double currentSetPoint = 0;

  public IntakeSubsystem() {
    intakeMotor =
        new SparkFlex(
            IntakeConstants.INTAKE_MOTOR, com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
    // intakeMotor.set(0);
    intakeMotorL =
        new SparkMax(
            IntakeConstants.INTAKE_MOTOR_L,
            com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
    intakeMotorR =
        new SparkMax(
            IntakeConstants.INTAKE_MOTOR_R,
            com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
    intakeEncoder = new DutyCycleEncoder(2);

    intakePIDController =
        new PIDController(IntakeConstants.kP, IntakeConstants.kI, IntakeConstants.kD);

    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putData(
        "Intake Raw Encoder", intakeEncoder);
  }

  public Command run(double speed) {
    intakeMotor.set(speed);
        return null;
  }

  public void setPoint(double setpoint) {
    currentSetPoint = setpoint;
    System.out.println("Hola Como Estas Estoy bien");
    if (intakeEncoder.isConnected()) {
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
