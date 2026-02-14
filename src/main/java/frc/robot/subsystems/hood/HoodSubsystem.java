package frc.robot.subsystems.hood;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.HoodConstants;

public class HoodSubsystem extends SubsystemBase {
  /** Creates a new HoodSubsystem. */
  public HoodSubsystem() {}
    public TalonFX hoodMotor;
    private HoodConstants.Mode mode;

    public void setMode(HoodConstants.Mode mode) {
      this.mode = mode;
    }

    // Sam Notes
    // Looks like this is still in progress


  /**
   * Example command factory method.
   *
   * @return a command
   */
  public Command AimHood() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    hoodMotor = new TalonFX(Constants.MotorIDs.HOOD_MOTOR);
    hoodMotor.set(0);
    this.mode = HoodConstants.Mode.OFF;
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }

   public void run(double speed) {
        hoodMotor.set(speed);
    }

    public void stop() {
        hoodMotor.stopMotor(); // Stop the hood motor

    }
  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
