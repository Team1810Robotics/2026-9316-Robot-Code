package frc.robot.subsystems.hood;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HoodSubsystem extends SubsystemBase {

    private final TalonFX hoodMotor;
    public final DutyCycleEncoder hoodEncoder;

    private final DutyCycleOut dutyCycleRequest = new DutyCycleOut(0);

    // Temporary: TalonFX is the primary control sensor
    private boolean zeroed = false;

    // Adjust later once measured
    private static final double HOOD_MIN_DEG = 0.0;
    private static final double HOOD_MAX_DEG = 30.0;

    // Placeholder conversion: degrees per motor rotation
    // MUST be measured once robot is in hand
    private static final double DEGREES_PER_MOTOR_ROTATION = 3.0;

    private static final double MAX_MANUAL_OUTPUT = 0.20;
    private static final double MAX_AUTO_OUTPUT = 0.18;
    private static final double kP = 0.03;
    private static final double POSITION_TOLERANCE_DEG = 1.0;

    private double targetDeg = 0.0;
    private boolean closedLoopEnabled = false;

    public HoodSubsystem() {
        hoodMotor = new TalonFX(HoodConstants.HOOD_MOTOR_ID);
        hoodEncoder = new DutyCycleEncoder(1);

        configureMotor();
        hoodMotor.set(0);

        SmartDashboard.putData("Absolute Encoder", hoodEncoder);
    }

    private void configureMotor() {
        var outCfg = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Brake);

        var ramps = new OpenLoopRampsConfigs()
            .withDutyCycleOpenLoopRampPeriod(0.25);

        var current = new CurrentLimitsConfigs()
            .withStatorCurrentLimitEnable(true)
            .withStatorCurrentLimit(40)
            .withSupplyCurrentLimitEnable(true)
            .withSupplyCurrentLimit(35);

        hoodMotor.getConfigurator().apply(outCfg);
        hoodMotor.getConfigurator().apply(ramps);
        hoodMotor.getConfigurator().apply(current);
    }

    public void stopHood() {
        closedLoopEnabled = false;
        hoodMotor.stopMotor();
    }

    public void setManualOutput(double speed) {
        closedLoopEnabled = false;

        if (!zeroed) {
            // Until zeroed, only allow very slow manual movement
            speed = MathUtil.clamp(speed, -0.10, 0.10);
        } else {
            speed = MathUtil.clamp(speed, -MAX_MANUAL_OUTPUT, MAX_MANUAL_OUTPUT);
            speed = applySoftLimits(speed);
        }

        hoodMotor.setControl(dutyCycleRequest.withOutput(speed));
    }

    public void zeroCurrentPosition() {
        hoodMotor.setPosition(0.0);
        zeroed = true;
        targetDeg = 0.0;
    }

    public boolean isZeroed() {
        return zeroed;
    }

    public double getMotorRotations() {
        return hoodMotor.getPosition().getValueAsDouble();
    }

    public double getHoodDegrees() {
        return getMotorRotations() * DEGREES_PER_MOTOR_ROTATION;
    }

    public double getAbsoluteEncoderTurns() {
        return hoodEncoder.get(); // telemetry only for now
    }

    public void setTargetDegrees(double degrees) {
        if (!zeroed) {
            stopHood();
            return;
        }

        targetDeg = MathUtil.clamp(degrees, HOOD_MIN_DEG, HOOD_MAX_DEG);
        closedLoopEnabled = true;
    }

    public boolean atTarget() {
        return Math.abs(targetDeg - getHoodDegrees()) <= POSITION_TOLERANCE_DEG;
    }

    private double applySoftLimits(double requestedOutput) {
        double currentDeg = getHoodDegrees();

        if (currentDeg <= HOOD_MIN_DEG && requestedOutput < 0) {
            return 0.0;
        }

        if (currentDeg >= HOOD_MAX_DEG && requestedOutput > 0) {
            return 0.0;
        }

        // slow down near the edges
        if (currentDeg <= HOOD_MIN_DEG + 2.0 || currentDeg >= HOOD_MAX_DEG - 2.0) {
            requestedOutput = MathUtil.clamp(requestedOutput, -0.08, 0.08);
        }

        return requestedOutput;
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Hood/Zeroed", zeroed);
        SmartDashboard.putNumber("Hood/MotorRotations", getMotorRotations());
        SmartDashboard.putNumber("Hood/Degrees", getHoodDegrees());
        SmartDashboard.putNumber("Hood/AbsoluteTurns", getAbsoluteEncoderTurns());
        SmartDashboard.putNumber("Hood/TargetDeg", targetDeg);

        if (!closedLoopEnabled || !zeroed) {
            return;
        }

        double error = targetDeg - getHoodDegrees();
        double output = error * kP;
        output = MathUtil.clamp(output, -MAX_AUTO_OUTPUT, MAX_AUTO_OUTPUT);
        output = applySoftLimits(output);

        hoodMotor.setControl(dutyCycleRequest.withOutput(output));
    }
}