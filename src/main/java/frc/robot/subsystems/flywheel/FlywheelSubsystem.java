package frc.robot.subsystems.flywheel;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class FlywheelSubsystem extends SubsystemBase {

  private final TalonFX leftMotor;
  private final TalonFX rightMotor;

  private final VelocityVoltage velocityControl = new VelocityVoltage(0);

  private double defaultVelocityRPS = 2200.0 / 60.0; // Convert from RPM to RPS, top # is the target RPM for the flywheel;
  private double visionVelocityRPS = FlywheelConstants.IDLE_VELOCITY;
  private boolean hasVisionTarget = false;
  private double activeTargetVelocityRPS = 2200.0 / 60.0; // Initial value matches default, but will be updated when runSelectedVelocity is called;

  private static final String SHOOTER_TARGET_RPS_KEY = "Shooter Target RPS";
  private static final double DEFAULT_TUNING_RPS = 52.0;

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

    rightConfig.Slot0 = new Slot0Configs().withKP(0.12).withKI(0.0).withKD(0.0).withKV(0.12);

    rightMotor.getConfigurator().apply(rightConfig);

    leftMotor.setControl(new Follower(FlywheelConstants.rightMotorID, MotorAlignmentValue.Opposed));

    SmartDashboard.putNumber(SHOOTER_TARGET_RPS_KEY, DEFAULT_TUNING_RPS);
  }

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

  public double getDashboardTargetVelocity() {
    return SmartDashboard.getNumber(SHOOTER_TARGET_RPS_KEY, DEFAULT_TUNING_RPS);
  }

  public void setDashboardTargetVelocity(double velocityRPS) {
    SmartDashboard.putNumber(SHOOTER_TARGET_RPS_KEY, velocityRPS);
  }

  public void adjustDashboardTargetVelocity(double deltaRPS) {
    setDashboardTargetVelocity(getDashboardTargetVelocity() + deltaRPS);
  }

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
    double tolerance = 5.0; 
    return Math.abs(getCurrentVelocity() - activeTargetVelocityRPS) < tolerance;
  }

  public String getFlywheelState() {
    return state.toString();
  }

  public double computeFlywheelRPMFromTY(double ty) {
    double x = Math.abs(ty);
    return 3065 + (-70.4) * x + (5.9) * Math.pow(x, 2) + (-0.122) * Math.pow(x, 3);
  }

  @Override
  public void periodic() {
    if (DriverStation.isEnabled()) {
      rightMotor.setControl(velocityControl.withVelocity(activeTargetVelocityRPS));
    } else {
      stopFlywheel();
    }

    if (activeTargetVelocityRPS == 0.0) {
      state = FlywheelState.STOPPED;
    } else if (isAtTargetSpeed()) {
      state = FlywheelState.AT_SPEED;
    } else {
      state = FlywheelState.SPINNING_UP;
    }

      // SmartDashboard.putNumber("Flywheel Current RPS", getCurrentVelocity());
      // SmartDashboard.putNumber("Flywheel Active Target RPS", activeTargetVelocityRPS);
      // SmartDashboard.putNumber("Flywheel Default RPS", defaultVelocityRPS);
      // SmartDashboard.putNumber("Flywheel Vision RPS", visionVelocityRPS);
      // SmartDashboard.putNumber("Flywheel Dashboard Target RPS", getDashboardTargetVelocity());
      // SmartDashboard.putNumber("Flywheel Current RPM", getCurrentVelocity() * 60.0);
      // SmartDashboard.putNumber("Flywheel Target RPM", activeTargetVelocityRPS * 60.0);
      // SmartDashboard.putBoolean("Flywheel Has Vision Target", hasVisionTarget);
      // SmartDashboard.putBoolean("Flywheel At Speed", isAtTargetSpeed());
      // SmartDashboard.putString("Flywheel State", state.toString());
    }
  }
}
