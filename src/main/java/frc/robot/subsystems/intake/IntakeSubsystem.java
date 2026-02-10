package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.IntakeConstants;


public class IntakeSubsystem extends SubsystemBase {
  public SparkMax intakeMotor;
  public DigitalInput proximitySensor;
  private IntakeConstants.Mode mode;

  public void setMode(IntakeConstants.Mode mode) {
    this.mode = mode;
  }

  public IntakeSubsystem() {
    intakeMotor = new SparkMax(0, null);
    proximitySensor = new DigitalInput(0);
    // very important... change the id number to test!!!!!!
    intakeMotor.set(0);
    this.mode = IntakeConstants.Mode.OFF; // initialize default0
  }

  public IntakeConstants.Mode getMode() {
    return mode;
  }

  public void run(double speed) {
    intakeMotor.set(speed);
  }

  public void stop() {
    intakeMotor.stopMotor(); // Stop the intake motor
  }

  private final DigitalInput m_proximitySensor = new DigitalInput(0);

  public boolean isObjectDetected() {
    return !m_proximitySensor.get(); // NPN logic: true when object is present
  }
}
