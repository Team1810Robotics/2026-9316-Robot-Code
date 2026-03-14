package frc.robot.subsystems.flywheel;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class FlywheelSubsystem extends SubsystemBase {

  private final TalonFX leftMotor;
  private final TalonFX rightMotor;

  // Reuse this control request object
  private final VelocityVoltage velocityControl = new VelocityVoltage(0);

  // Fallback target when no vision target is present
  private double defaultVelocityRPS = FlywheelConstants.IDLE_VELOCITY;

  // Target calculated from limelight/vision later
  private double visionVelocityRPS = FlywheelConstants.IDLE_VELOCITY;

  // Whether vision currently sees a valid target
  private boolean hasVisionTarget = false;

  // What we are currently asking the flywheel to do
  private double activeTargetVelocityRPS = 0.0;

  private enum FlywheelState {
    STOPPED,
    SPINNING_UP,
    AT_SPEED
  }

  private FlywheelState state = FlywheelState.STOPPED;

  public FlywheelSubsystem() {
    leftMotor = new TalonFX(FlywheelConstants.leftMotorID);
    rightMotor = new TalonFX(FlywheelConstants.rightMotorID);

    TalonFXConfiguration rightConfig = new TalonFXConfiguration();

    rightConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;

    // Example gains - tune these on the robot
    rightConfig.Slot0 = new Slot0Configs()
        .withKP(0.12)
        .withKI(0.0)
        .withKD(0.0)
        .withKV(0.12);

    rightMotor.getConfigurator().apply(rightConfig);

    // Left follows right
    leftMotor.setControl(
        new Follower(FlywheelConstants.rightMotorID, MotorAlignmentValue.Opposed));
  }

  // -------------------------
  // Target selection
  // -------------------------

  public void setDefaultVelocity(double velocityRPS) {
    defaultVelocityRPS = velocityRPS;
  }

  public double getDefaultVelocity() {
    return defaultVelocityRPS;
  }

  public void setVisionVelocity(double velocityRPS) {
    visionVelocityRPS = velocityRPS;
  }

  public double getVisionVelocity() {
    return visionVelocityRPS;
  }

  public void setHasVisionTarget(boolean hasTarget) {
    hasVisionTarget = hasTarget;
  }

  public boolean hasVisionTarget() {
    return hasVisionTarget;
  }

  public double getRequestedVelocity() {
    return hasVisionTarget ? visionVelocityRPS : defaultVelocityRPS;
  }

  // -------------------------
  // Flywheel control
  // -------------------------

  public void runSelectedVelocity() {
    activeTargetVelocityRPS = getRequestedVelocity();
    rightMotor.setControl(velocityControl.withVelocity(activeTargetVelocityRPS));
  }

  public void setFlywheelVelocity(double velocityRPS) {
    activeTargetVelocityRPS = velocityRPS;
    rightMotor.setControl(velocityControl.withVelocity(velocityRPS));
  }

  public void stopFlywheel() {
    activeTargetVelocityRPS = 0.0;
    rightMotor.stopMotor();
  }

  public void setFlywheelPower(double powerPercent) {
    activeTargetVelocityRPS = 0.0;
    rightMotor.set(powerPercent);
  }

  public double getCurrentVelocity() {
    return rightMotor.getVelocity().getValueAsDouble();
  }

  public double getTargetVelocity() {
    return activeTargetVelocityRPS;
  }

  public boolean isAtTargetSpeed() {
    double tolerance = 2.0;
    return Math.abs(getCurrentVelocity() - activeTargetVelocityRPS) < tolerance;
  }

  public String getFlywheelState() {
    return state.toString();
  }

  @Override
  public void periodic() {
    if (activeTargetVelocityRPS == 0.0) {
      state = FlywheelState.STOPPED;
    } else if (isAtTargetSpeed()) {
      state = FlywheelState.AT_SPEED;
    } else {
      state = FlywheelState.SPINNING_UP;
    }

    SmartDashboard.putNumber("Flywheel Current RPS", getCurrentVelocity());
    SmartDashboard.putNumber("Flywheel Active Target RPS", activeTargetVelocityRPS);
    SmartDashboard.putNumber("Flywheel Default RPS", defaultVelocityRPS);
    SmartDashboard.putNumber("Flywheel Vision RPS", visionVelocityRPS);
    SmartDashboard.putBoolean("Flywheel Has Vision Target", hasVisionTarget);
    SmartDashboard.putString("Flywheel State", state.toString());
  }
}