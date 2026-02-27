package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
  public SparkMax intakeMotor;
  public SparkMax intakeMotorL;
  public SparkMax intakeMotorR;
  public Encoder intakeEncoder;

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
}
