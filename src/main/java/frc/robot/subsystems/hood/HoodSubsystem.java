package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HoodSubsystem extends SubsystemBase {
  public int hoodEncoderRotations = 0;

  private final TalonFX hoodMotor;
  public final DutyCycleEncoder hoodEncoder;

  private final DigitalInput hoodLimitSwitch;

  private final PIDController hoodPIDController;

  private enum HoodMode {
    IDLE,
    MANUAL,
    POSITION
  }

  private HoodMode hoodMode = HoodMode.IDLE;

  private double manualSpeed = 0.0;
  private double currentSetPoint = HoodConstants.DEFAULT_POSITION;
  private double defaultSetPoint = HoodConstants.DEFAULT_POSITION;
  private double visionSetPoint = HoodConstants.DEFAULT_POSITION;
  private boolean hasVisionTarget = false;

  private double lastRawEncoder = 0.0;
  private boolean encoderInitialized = false;

  private double hoodZeroOffset = 0.0;
  private boolean hoodZeroed = false;

  public HoodSubsystem() {
    hoodEncoder = new DutyCycleEncoder(HoodConstants.HOOD_ENCODER_DIO);
    hoodLimitSwitch = new DigitalInput(HoodConstants.HOOD_LIMIT_SWITCH_DIO);
    hoodMotor = new TalonFX(HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
    configureMotor();

    hoodPIDController = new PIDController(HoodConstants.kP, HoodConstants.kI, HoodConstants.kD);
    hoodPIDController.setTolerance(HoodConstants.HOOD_TOLERANCE);

    SmartDashboard.putData("Hood Encoder", hoodEncoder);
  }

  private void configureMotor() {
    var outCfg = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Brake);

    var ramps = new OpenLoopRampsConfigs().withDutyCycleOpenLoopRampPeriod(0.25);

    var current =
        new CurrentLimitsConfigs()
            .withStatorCurrentLimitEnable(true)
            .withStatorCurrentLimit(40)
            .withSupplyCurrentLimitEnable(true)
            .withSupplyCurrentLimit(35);

    hoodMotor.getConfigurator().apply(outCfg);
    hoodMotor.getConfigurator().apply(ramps);
    hoodMotor.getConfigurator().apply(current);
  }

private void updateEncoderTracking() {
  double currentRaw = hoodEncoder.get();

  if (!encoderInitialized) {
    lastRawEncoder = currentRaw;
    encoderInitialized = true;
    return;
  }

  double delta = currentRaw - lastRawEncoder;

  if (delta < -0.5) {
    hoodEncoderRotations++;
  } else if (delta > 0.5) {
    hoodEncoderRotations--;
  }

}



  public void zeroContinuousHoodEncoder() {
    hoodEncoderRotations = 0;
    lastRawEncoder = hoodEncoder.get();
    encoderInitialized = true;
    hoodZeroOffset = -hoodEncoder.get();
    hoodZeroed = true;
    hoodPIDController.reset();
  }

  public boolean isLimitSwitchPressed() {
    return hoodLimitSwitch.get() == HoodConstants.HOOD_LIMIT_SWITCH_PRESSED_STATE;
  }

  public boolean getLimitSwitchRaw() {
    return hoodLimitSwitch.get();
  }

  public void run(double speed) {
    hoodMode = HoodMode.MANUAL;
    manualSpeed = speed;
  }

  public void stopHood() {
    hoodMode = HoodMode.IDLE;
    manualSpeed = 0.0;
    hoodMotor.stopMotor();
  }

  public double getHoodEncoder() {
    return hoodEncoder.get();
  }

  // Continuous value increases when hood goes UP
  public double getContinuousHoodEncoder() {
    double rawContinuous = (hoodEncoderRotations + hoodEncoder.get());
    return rawContinuous - hoodZeroOffset;
  }

  public void runUP(double speed) {
    hoodMode = HoodMode.MANUAL;
    manualSpeed = Math.abs(speed);
  }

  public void runDOWN(double speed) {
    hoodMode = HoodMode.MANUAL;
    manualSpeed = -Math.abs(speed);
  }

  public void setPoint(double setpoint) {
    currentSetPoint = setpoint;
    hoodMode = HoodMode.POSITION;
  }

  public double getSetPoint() {
    return currentSetPoint;
  }

  public void setDefaultSetPoint(double setpoint) {
    defaultSetPoint = setpoint;
  }

  public void setVisionSetPoint(double setpoint) {
    visionSetPoint = setpoint;
  }

  public void setHasVisionTarget(boolean hasTarget) {
    hasVisionTarget = hasTarget;
  }

  public boolean hasVisionTarget() {
    return hasVisionTarget;
  }

  public void runSelectedSetPoint() {
    setPoint(hasVisionTarget ? visionSetPoint : defaultSetPoint);
  }

  public boolean isAtSetPoint() {
    return Math.abs(getContinuousHoodEncoder() - currentSetPoint) <= HoodConstants.HOOD_TOLERANCE;
  }

public double computeHoodSetpointFromTY(double ty) {
  double x = Math.abs(ty);

  double setpoint =
      1.23
          + (-0.271) * x
          + (0.0609) * Math.pow(x, 2)
          + (-5.73e-3) * Math.pow(x, 3)
          + (2.48e-4) * Math.pow(x, 4)
          + (-3.94e-6) * Math.pow(x, 5);

  SmartDashboard.putNumber("Hood TY Raw", ty);
  SmartDashboard.putNumber("Hood TY Used", x);
  SmartDashboard.putNumber("Hood Polynomial Output", setpoint);

  return MathUtil.clamp(setpoint, 0.0, 3.0);
}
  private void applyMotorOutput(double output) {
    double clamped =
        MathUtil.clamp(output, -HoodConstants.MAX_PID_OUTPUT, HoodConstants.MAX_PID_OUTPUT);

    // If the hood is on the zero/home switch, do not allow further downward motion.
    // Negative output is assumed to be DOWN toward the limit switch.
    if (isLimitSwitchPressed() && clamped < 0) {
      clamped = 0.0;
    }

    hoodMotor.set(clamped);
  }

  @Override
public void periodic() {
  if (edu.wpi.first.wpilibj.DriverStation.isDisabled()) {
    stopHood();
    return;
  }

  updateEncoderTracking();

  // Keep the encoder zeroed any time the hood is sitting on the switch.
  if (isLimitSwitchPressed()) {
    zeroContinuousHoodEncoder();
  }

  double currentPosition = getContinuousHoodEncoder();

  switch (hoodMode) {
    case MANUAL:
      applyMotorOutput(manualSpeed);
      break;

    case POSITION:
      double output = hoodPIDController.calculate(currentPosition, currentSetPoint);
      applyMotorOutput(output);
      break;

    case IDLE:
    default:
      hoodMotor.stopMotor();
      break;
  }

SmartDashboard.putNumber("Hood Raw Encoder", getHoodEncoder());
SmartDashboard.putNumber("Hood Continuous Encoder", getContinuousHoodEncoder());
SmartDashboard.putNumber("Hood Encoder Rotations", hoodEncoderRotations);
SmartDashboard.putNumber("Hood Zero Offset", hoodZeroOffset);
SmartDashboard.putNumber("Hood Desired Position", currentSetPoint);
SmartDashboard.putNumber("Hood Vision Calculated", visionSetPoint);
SmartDashboard.putBoolean("Hood Zeroed", hoodZeroed);
SmartDashboard.putBoolean("Hood Limit Switch Raw", getLimitSwitchRaw());
SmartDashboard.putBoolean("Hood Limit Switch Pressed", isLimitSwitchPressed());
SmartDashboard.putString("Hood Mode", hoodMode.toString());
SmartDashboard.putBoolean("Hood At SetPoint", isAtSetPoint());
}
}
