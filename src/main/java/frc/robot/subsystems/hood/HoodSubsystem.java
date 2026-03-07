package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HoodSubsystem extends SubsystemBase {

  private final TalonFX hoodMotor;
  private final DutyCycleEncoder hoodEncoder;

  
  private final PIDController hoodPIDController;

  //these fields let us track a multi-turn hood position
  private double lastAbsoluteReading = 0.0;
  private int turnCount = 0;
  private double hoodPosition = 0.0;

  //track target position for presets
  private double currentSetPoint = 0.0;

  //allow manual jog mode to temporarily override PID
  private boolean manualMode = false;
  private double manualOutput = 0.0;

  
  public HoodSubsystem() {
    
    hoodEncoder = new DutyCycleEncoder(HoodConstants.HOOD_ENCODER_DIO);

    hoodMotor = new TalonFX(HoodConstants.HOOD_MOTOR_ID);
    hoodMotor.set(0);

    configureMotor();

    hoodPIDController =
        new PIDController(HoodConstants.kP, HoodConstants.kI, HoodConstants.kD);

    hoodPIDController.setTolerance(HoodConstants.HOOD_TOLERANCE);

    lastAbsoluteReading = hoodEncoder.get();
    hoodPosition = lastAbsoluteReading;
    currentSetPoint = hoodPosition;
  }

  private void configureMotor() {
    var outCfg = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Brake);

    var ramps =
        new OpenLoopRampsConfigs().withDutyCycleOpenLoopRampPeriod(0.25);

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

  // MANUAL CONTROL
  public void run(double speed) {
    hoodMotor.set(speed);
  }

  public void stopHood() {
    hoodMotor.stopMotor();
  }

  public void startManualJogUp(double speed) {
    hoodMotor.set(speed);
  }

  public void startManualJogDown(double speed) {
    hoodMotor.set(-speed);
  }

  public void stopManualJog() {
    manualMode = false;
    manualOutput = 0.0;
    stopHood();
  }

  // ENCODER / POSITION

  public double getHoodEncoderRaw() {
    return hoodEncoder.get();
  }

  public double getHoodPosition() {
    return hoodPosition;
  }

  public double getCurrentSetPoint() {
    return currentSetPoint;
  }

 // detect wraparound so one-turn encoder becomes multi-turn-ish
  private void updateHoodPosition() {
    // double currentAbsoluteReading = hoodEncoder.get();

    // if ((lastAbsoluteReading > 0.8) && (currentAbsoluteReading < 0.2)) {
    //   turnCount++;
    // } else if ((lastAbsoluteReading < 0.2) && (currentAbsoluteReading > 0.8)) {
    //   turnCount--;
    // }

    // hoodPosition = turnCount + currentAbsoluteReading;
    // lastAbsoluteReading = currentAbsoluteReading;
  }

  // PRESET POSITIONS

  // public void setHoodSetPoint(double setpoint) {
  //   currentSetPoint = setpoint;
  //   manualMode = false;
  // }

  // public void goToCloseShot() {
  //   setHoodSetPoint(HoodConstants.HOOD_CLOSE_POSITION);
  // }

  // public void goToMidShot() {
  //   setHoodSetPoint(HoodConstants.HOOD_MID_POSITION);
  // }

  // public void goToFarShot() {
  //   setHoodSetPoint(HoodConstants.HOOD_FAR_POSITION);
  // }

  // public boolean isAtSetPoint() {
  //   return hoodPIDController.atSetpoint();
  // }

  // private void runToSetPoint() {
  //   double output = hoodPIDController.calculate(hoodPosition, currentSetPoint);

  //   output =
  //       Math.max(
  //           -HoodConstants.MAX_HOOD_OUTPUT,
  //           Math.min(HoodConstants.MAX_HOOD_OUTPUT, output));

  //   hoodMotor.set(output);
  // }

  @Override
  public void periodic() {
    // updateHoodPosition();

    // //manual jog overrides PID, otherwise PID runs
    // if (manualMode) {
    //   hoodMotor.set(manualOutput);
    // } else {
    //   runToSetPoint();
    // }

    //dashboard output for debugging and tuning
    SmartDashboard.putNumber("Hood Encoder Raw", getHoodEncoderRaw());
    SmartDashboard.putNumber("Hood Position", getHoodPosition());
    SmartDashboard.putNumber("Hood Setpoint", getCurrentSetPoint());
    //SmartDashboard.putBoolean("Hood At SetPoint", isAtSetPoint());
    SmartDashboard.putBoolean("Hood Encoder Connected", hoodEncoder.isConnected());
    SmartDashboard.putNumber("Hood Turn Count", turnCount);
  }
}