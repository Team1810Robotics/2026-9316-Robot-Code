package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HoodSubsystem extends SubsystemBase {
  private int hoodEncoderRotations = 0;

  private final TalonFX hoodMotor;
  private final DutyCycleEncoder hoodEncoder;
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

  // Encoder tracking uses a transformed wrapped value that INCREASES when the hood goes UP.
  private double lastWrappedEncoder = 0.0;
  private boolean encoderInitialized = false;

  // Offset captured when zero/home is hit
  private double hoodZeroOffset = 0.0;
  private boolean hoodZeroed = false;

  // Zero only once per press
  private boolean lastLimitSwitchPressed = false;

  // Debug
  private double lastTYRaw = 0.0;
  private double lastTYUsed = 0.0;
  private double lastPolynomialOutput = 0.0;
  private double lastPolynomialClamped = 0.0;
  private double lastEncoderDelta = 0.0;
  private double lastPidOutput = 0.0;

  public HoodSubsystem() {
    hoodEncoder = new DutyCycleEncoder(HoodConstants.HOOD_ENCODER_DIO);
    hoodLimitSwitch = new DigitalInput(HoodConstants.HOOD_LIMIT_SWITCH_DIO);
    hoodMotor = new TalonFX(HoodConstants.HOOD_MOTOR_ID);

    configureMotor();

    hoodPIDController = new PIDController(HoodConstants.kP, HoodConstants.kI, HoodConstants.kD);
    hoodPIDController.setTolerance(HoodConstants.HOOD_TOLERANCE);

    hoodMotor.stopMotor();

    // SmartDashboard.putData("Hood Encoder", hoodEncoder);

    // SmartDashboard.putNumber("Hood Raw Encoder", 0.0);
    // // SmartDashboard.putNumber("Hood Wrapped Encoder", 0.0);
    // SmartDashboard.putNumber("Hood Continuous Encoder", 0.0);
    // SmartDashboard.putNumber("Hood Encoder Rotations", 0.0);
    // // SmartDashboard.putNumber("Hood Zero Offset", 0.0);
    // // SmartDashboard.putNumber("Hood Desired Position", currentSetPoint);
    // // SmartDashboard.putNumber("Hood Vision Calculated", visionSetPoint);
    // SmartDashboard.putNumber("Hood TY Raw", 0.0);
    // SmartDashboard.putNumber("Hood TY Used", 0.0);
    // SmartDashboard.putNumber("Hood Polynomial Output", 0.0);
    // SmartDashboard.putNumber("Hood Polynomial Clamped", 0.0);
    // SmartDashboard.putNumber("Hood Raw Delta", 0.0);
    // SmartDashboard.putNumber("Hood PID Error", 0.0);
    // SmartDashboard.putNumber("Hood PID Output", 0.0);

    // SmartDashboard.putBoolean("Hood Zeroed", false);
    // SmartDashboard.putBoolean("Hood Limit Switch Raw", false);
    SmartDashboard.putBoolean("Hood Limit Switch Pressed", true);
    // SmartDashboard.putBoolean("Hood Has Vision Target", false);
    // SmartDashboard.putBoolean("Hood At SetPoint", false);
    // SmartDashboard.putBoolean("Hood Encoder Connected", false);

    SmartDashboard.putString("Hood Mode", hoodMode.toString());
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

  /**
   * Returns a wrapped encoder position in [0,1) that increases as the hood moves UP.
   *
   * <p>You observed that raw encoder decreases when hood goes up, so we invert it here.
   */
  private double getWrappedHoodPosition() {
    double wrapped = 1.0 - hoodEncoder.get();

    // Keep in [0,1)
    if (wrapped >= 1.0) {
      wrapped -= 1.0;
    } else if (wrapped < 0.0) {
      wrapped += 1.0;
    }

    return wrapped;
  }

  private void updateEncoderTracking() {
    double currentWrapped = getWrappedHoodPosition();

    if (!encoderInitialized) {
      lastWrappedEncoder = currentWrapped;
      encoderInitialized = true;
      lastEncoderDelta = 0.0;
      return;
    }

    double delta = currentWrapped - lastWrappedEncoder;
    lastEncoderDelta = delta;

    // Ignore tiny jitter
    if (Math.abs(delta) < 0.02) {
      lastWrappedEncoder = currentWrapped;
      return;
    }

    // Handle rollover on transformed encoder
    // Example:
    // moving up through wrap might go 0.98 -> 0.02, delta = -0.96, which means +1 turn
    if (delta < -0.7) {
      hoodEncoderRotations++;
    } else if (delta > 0.7) {
      hoodEncoderRotations--;
    }

    lastWrappedEncoder = currentWrapped;
  }

  public void zeroContinuousHoodEncoder() {
    hoodEncoderRotations = 0;
    lastWrappedEncoder = getWrappedHoodPosition();
    encoderInitialized = true;

    hoodZeroOffset = getWrappedHoodPosition();
    hoodZeroed = true;

    hoodPIDController.reset();
  }

  public boolean isLimitSwitchPressed() {
    return hoodLimitSwitch.get() == HoodConstants.HOOD_LIMIT_SWITCH_PRESSED_STATE;
  }

  public boolean getLimitSwitchRaw() {
    return hoodLimitSwitch.get();
  }

  public double getHoodRawEncoder() {
    return hoodEncoder.get();
  }

  public double getWrappedEncoder() {
    return getWrappedHoodPosition();
  }

  /**
   * Continuous hood position in "encoder turns from home", where: home ≈ 0 moving hood up increases
   * position
   */
  public double getContinuousHoodEncoder() {
    double continuous = hoodEncoderRotations + getWrappedHoodPosition();
    return continuous - hoodZeroOffset;
  }

  public void run(double speed) {
    hoodMode = HoodMode.MANUAL;
    manualSpeed = speed;
  }

  public void runUP(double speed) {
    hoodMode = HoodMode.MANUAL;
    manualSpeed = Math.abs(speed);
  }

  public void runDOWN(double speed) {
    hoodMode = HoodMode.MANUAL;
    manualSpeed = -Math.abs(speed);
  }

  public void stopHood() {
    hoodMode = HoodMode.IDLE;
    manualSpeed = 0.0;
    lastPidOutput = 0.0;
    hoodMotor.stopMotor();
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
        0.892
            + (-0.271) * x
            + (0.0609) * Math.pow(x, 2)
            + (-5.73e-3) * Math.pow(x, 3)
            + (2.48e-4) * Math.pow(x, 4)
            + (-3.94e-6) * Math.pow(x, 5);

    double clamped = MathUtil.clamp(setpoint, 0.0, 3.0);

    lastTYRaw = ty;
    lastTYUsed = x;
    lastPolynomialOutput = setpoint;
    lastPolynomialClamped = clamped;

    visionSetPoint = clamped;
    return clamped;
  }

  private void applyMotorOutput(double output) {
    double clamped =
        MathUtil.clamp(output, -HoodConstants.MAX_PID_OUTPUT, HoodConstants.MAX_PID_OUTPUT);

    // Assumption:
    // negative output drives hood DOWN toward the home switch
    if (isLimitSwitchPressed() && clamped < 0) {
      clamped = 0.0;
    }

    hoodMotor.set(clamped);
  }

  @Override
  public void periodic() {
    updateEncoderTracking();

    boolean limitPressed = isLimitSwitchPressed();

    // Zero once when switch transitions to pressed
    if (limitPressed && !lastLimitSwitchPressed) {
      zeroContinuousHoodEncoder();
    }
    lastLimitSwitchPressed = limitPressed;

    if (DriverStation.isDisabled()) {
      stopHood();
    } else {
      double currentPosition = getContinuousHoodEncoder();

      switch (hoodMode) {
        case MANUAL:
          lastPidOutput = 0.0;
          applyMotorOutput(manualSpeed);
          break;

        case POSITION:
          lastPidOutput = hoodPIDController.calculate(currentPosition, currentSetPoint);
          applyMotorOutput(lastPidOutput);
          break;

        case IDLE:
        default:
          lastPidOutput = 0.0;
          hoodMotor.stopMotor();
          break;
      }
    }

    // SmartDashboard.putNumber("Hood Raw Encoder", getHoodRawEncoder());
    // // SmartDashboard.putNumber("Hood Wrapped Encoder", getWrappedEncoder());
    // SmartDashboard.putNumber("Hood Continuous Encoder", getContinuousHoodEncoder());
    // SmartDashboard.putNumber("Hood Encoder Rotations", hoodEncoderRotations);
    // // SmartDashboard.putNumber("Hood Zero Offset", hoodZeroOffset);
    // // SmartDashboard.putNumber("Hood Desired Position", currentSetPoint);
    // // SmartDashboard.putNumber("Hood Vision Calculated", visionSetPoint);
    // SmartDashboard.putNumber("Hood TY Raw", lastTYRaw);
    // SmartDashboard.putNumber("Hood TY Used", lastTYUsed);
    // // SmartDashboard.putNumber("Hood Polynomial Output", lastPolynomialOutput);
    // // SmartDashboard.putNumber("Hood Polynomial Clamped", lastPolynomialClamped);
    // // SmartDashboard.putNumber("Hood Raw Delta", lastEncoderDelta);
    // // SmartDashboard.putNumber("Hood PID Error", currentSetPoint - getContinuousHoodEncoder());
    // // SmartDashboard.putNumber("Hood PID Output", lastPidOutput);

    // // SmartDashboard.putBoolean("Hood Zeroed", hoodZeroed);
    // SmartDashboard.putBoolean("Hood Limit Switch Raw", getLimitSwitchRaw());
    // SmartDashboard.putBoolean("Hood Limit Switch Pressed", isLimitSwitchPressed());
    //   SmartDashboard.putBoolean("Hood Has Vision Target", hasVisionTarget);
    //   SmartDashboard.putBoolean("Hood At SetPoint", isAtSetPoint());
    //   SmartDashboard.putBoolean("Hood Encoder Connected", hoodEncoder.isConnected());

    //   SmartDashboard.putString("Hood Mode", hoodMode.toString());
  }
}
