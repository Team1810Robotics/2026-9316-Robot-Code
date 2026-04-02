package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
  public SparkFlex intakeMotor;
  public SparkMax intakeMotorL;
  public SparkMax intakeMotorR;
  public DutyCycleEncoder intakeEncoder;

  private final PIDController intakePIDController;

  private double currentSetPoint = 0.0;
  private double manualSpeed = 0.0;

  private enum IntakeMode {
    IDLE,
    MANUAL,
    POSITION
  }

  private IntakeMode intakeMode = IntakeMode.IDLE;

  public IntakeSubsystem() {
    intakeMotor =
        new SparkFlex(
            IntakeConstants.INTAKE_MOTOR, com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);

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

    // If you have a tolerance constant, use it here
    // intakePIDController.setTolerance(IntakeConstants.INTAKE_TOLERANCE);

   // SmartDashboard.putData("Intake Raw Encoder", intakeEncoder);
  }

  // -------------------------
  // Intake wheel motor (Neo Vortex / Spark Flex)
  // -------------------------

  public void run(double speed) {
    intakeMotor.set(speed);
  }

  public void TestingIntakeMotor(double speed) {
    intakeMotor.set(speed);
  }

  public void stopIntake() {
    intakeMotor.stopMotor();
  }

  // -------------------------
  // Intake arm position / motion
  // -------------------------

  public void setPoint(double setpoint) {
    currentSetPoint = setpoint;
    intakeMode = IntakeMode.POSITION;
  }

  public double getSetPoint() {
    return currentSetPoint;
  }

  public void stopIntakeLevel() {
    intakeMode = IntakeMode.IDLE;
    manualSpeed = 0.0;
    intakeMotorL.stopMotor();
    intakeMotorR.stopMotor();
  }

  public void runUP(double speed) {
    intakeMode = IntakeMode.MANUAL;
    manualSpeed = Math.abs(speed);
  }

  public void runDOWN(double speed) {
    intakeMode = IntakeMode.MANUAL;
    manualSpeed = -Math.abs(speed);
  }

  public double getIntakeEncoder() {
    return intakeEncoder.get();
  }

  public boolean isAtSetPoint() {
    double tolerance = 0.02; // move to constants later if you want
    return Math.abs(getIntakeEncoder() - currentSetPoint) <= tolerance;
  }

  private void applyArmOutput(double output) {
    double clamped = MathUtil.clamp(output, -0.9, 0.9);

    intakeMotorL.set(-clamped);
    intakeMotorR.set(clamped);
  }

  @Override
  public void periodic() {
    // SmartDashboard.putBoolean("Intake Encoder Connected", intakeEncoder.isConnected());
    // SmartDashboard.putNumber("Intake Encoder", getIntakeEncoder());
    // SmartDashboard.putString("Intake Mode", intakeMode.toString());
    // SmartDashboard.putNumber("Intake SetPoint", currentSetPoint);
    // SmartDashboard.putBoolean("Intake At SetPoint", isAtSetPoint());

    if (!intakeEncoder.isConnected()) {
      stopIntakeLevel();
      return;
    }

    switch (intakeMode) {
      case MANUAL:
        applyArmOutput(manualSpeed);
        break;

      case POSITION:
        double output = intakePIDController.calculate(getIntakeEncoder(), currentSetPoint);
        applyArmOutput(output);
        break;

      case IDLE:
      default:
        intakeMotorL.stopMotor();
        intakeMotorR.stopMotor();
        break;
    }
  }
}
