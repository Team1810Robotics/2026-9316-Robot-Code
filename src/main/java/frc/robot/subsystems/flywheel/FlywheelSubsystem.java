package frc.robot.subsystems.flywheel;

import com.ctre.phoenix6.configs.MotorOutputConfigs; 
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class FlywheelSubsystem extends SubsystemBase {

  private final TalonFX leftMotor;
  private final TalonFX rightMotor;

  public FlywheelSubsystem() {
    leftMotor = new TalonFX(FlywheelConstants.LEFT_FLYWHEEL_MOTOR_ID);
    rightMotor = new TalonFX(FlywheelConstants.RIGHT_FLYWHEEL_MOTOR_ID);



    configureMotors();
  }

  private void configureMotors() {
    leftMotor.getConfigurator().apply(
        new MotorOutputConfigs()
            .withNeutralMode(NeutralModeValue.Coast)
            .withInverted(InvertedValue.CounterClockwise_Positive));

    rightMotor.getConfigurator().apply(
        new MotorOutputConfigs()
            .withNeutralMode(NeutralModeValue.Coast)
            .withInverted(InvertedValue.Clockwise_Positive));
  }

  public void startFlywheel() {
    leftMotor.set(FlywheelConstants.FLYWHEEL_SPEED);
    rightMotor.set(FlywheelConstants.FLYWHEEL_SPEED);
  }

  public void stopFlywheel() {
    leftMotor.stopMotor();
    rightMotor.stopMotor();
  }

  public void setFlywheelSpeed(double speed) {
    leftMotor.set(speed);
    rightMotor.set(speed);
  }
}