package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.vision.VisionSubsystem; // NEW: import for VisionSubsystem

public class HoodSubsystem extends SubsystemBase {
  public int hoodEncoderRotations = 0;

  private final TalonFX hoodMotor;
  public final DutyCycleEncoder hoodEncoder;

  // NEW: reference to VisionSubsystem so the hood can pull TY directly
  private final VisionSubsystem visionSubsystem;

  private final PIDController hoodPIDController;

  private enum HoodMode {
    IDLE,
    MANUAL,
    POSITION
  }

  private HoodMode hoodMode = HoodMode.IDLE;

  private double manualSpeed = 0.0;
  private double currentSetPoint = HoodConstants.DEFAULT_POSITION;
  // NEW: removed defaultSetPoint, visionSetPoint, and hasVisionTarget —
  // vision targeting is now handled automatically in periodic()

  private double lastRawEncoder = 0.0;
  private boolean encoderInitialized = false;

  private double hoodZeroOffset = 0.0;
  private boolean hoodZeroed = false;

  // NEW: constructor now accepts VisionSubsystem instead of no arguments
  public HoodSubsystem(VisionSubsystem visionSubsystem) {
    this.visionSubsystem = visionSubsystem;

    hoodEncoder = new DutyCycleEncoder(HoodConstants.HOOD_ENCODER_DIO);
    hoodMotor = new TalonFX(HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);
    configureMotor();

    hoodPIDController =
        new PIDController(HoodConstants.kP, HoodConstants.kI, HoodConstants.kD);
    hoodPIDController.setTolerance(HoodConstants.HOOD_TOLERANCE);

    SmartDashboard.putData("Hood Encoder", hoodEncoder);
  }

  private void configureMotor() {
    var outCfg = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Brake);
    var ramps = new OpenLoopRampsConfigs().withDutyCycleOpenLoopRampPeriod(0.25);
    var current = new CurrentLimitsConfigs()
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

    boolean wasHigh = lastRawEncoder > 0.8;
    boolean wasLow = lastRawEncoder < 0.2;
    boolean isHigh = currentRaw > 0.8;
    boolean isLow = currentRaw < 0.2;

    if (wasHigh && isLow) {
      hoodEncoderRotations++;
    } else if (wasLow && isHigh) {
      hoodEncoderRotations--;
    }

    lastRawEncoder = currentRaw;
  }

  public void zeroContinuousHoodEncoder() {
    hoodEncoderRotations = 0;
    lastRawEncoder = hoodEncoder.get();
    encoderInitialized = true;
    hoodZeroOffset = -hoodEncoder.get();
    hoodZeroed = true;
  }

  public double getHoodEncoder() {
    return hoodEncoder.get();
  }

  public double getContinuousHoodEncoder() {
    double rawContinuous = -(hoodEncoderRotations + hoodEncoder.get());
    return rawContinuous - hoodZeroOffset;
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

  public boolean isAtSetPoint() {
    return Math.abs(getContinuousHoodEncoder() - currentSetPoint)
        <= HoodConstants.HOOD_TOLERANCE;
  }

  /**
   * NEW: Computes the desired hood encoder position from Limelight TY
   * using a 5th-degree polynomial best fit curve.
   * y = 1.23 - 0.271x + 0.0609x² - 5.73E-3x³ + 2.48E-4x⁴ - 3.94E-6x⁵
   * where x = TY (degrees), y = hood encoder value
   */
  public double computeHoodSetpointFromTY(double ty) {
    return  1.23
          + (-0.271)   * ty
          + (0.0609)   * Math.pow(ty, 2)
          + (-5.73e-3) * Math.pow(ty, 3)
          + (2.48e-4)  * Math.pow(ty, 4)
          + (-3.94e-6) * Math.pow(ty, 5);
  }

  private void applyMotorOutput(double output) {
    double clamped =
        MathUtil.clamp(output, -HoodConstants.MAX_PID_OUTPUT, HoodConstants.MAX_PID_OUTPUT);
    hoodMotor.set(clamped);
  }

  @Override
  public void periodic() {
    updateEncoderTracking();

    if (encoderInitialized && !hoodZeroed) {
      zeroContinuousHoodEncoder();
    }

    // NEW: if the limelight sees a target, compute and apply the vision-based
    // setpoint every loop cycle so the hood continuously tracks as TY changes.
    // If no target is visible, the hood holds its last setpoint.
    // if (visionSubsystem.targetValid()) {
    //   double ty = visionSubsystem.getTy();
    //   currentSetPoint = computeHoodSetpointFromTY(ty);
    //   hoodMode = HoodMode.POSITION;
    // }

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
    SmartDashboard.putBoolean("Hood Zeroed", hoodZeroed);
    SmartDashboard.putString("Hood Mode", hoodMode.toString());
    SmartDashboard.putNumber("Hood SetPoint", currentSetPoint);
    SmartDashboard.putBoolean("Hood At SetPoint", isAtSetPoint());
    // NEW: replaces the old "Hood Has Vision Target" boolean that relied on the
    // removed hasVisionTarget field — now reads directly from VisionSubsystem
    SmartDashboard.putBoolean("Hood Has Vision Target", visionSubsystem.targetValid());
  }
}