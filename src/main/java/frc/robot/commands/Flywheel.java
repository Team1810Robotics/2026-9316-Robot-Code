package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.flywheel.FlywheelConstants;
// *import com.ctre.phoenix6.configs.TalonFXConfigurator;
// import frc.robot.Constants;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;

/** Flywheel command */
public class Flywheel extends Command {
  private final FlywheelSubsystem flywheelSubsystem;
  private final double targetVelocity;

  public Flywheel(FlywheelSubsystem flywheelSubsystem, double targetVelocity) {
    this.flywheelSubsystem = flywheelSubsystem;
    this.targetVelocity = targetVelocity;

    addRequirements(flywheelSubsystem);
  }

  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    flywheelSubsystem.setFlywheelVelocity(targetVelocity);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    flywheelSubsystem.setFlywheelVelocity(FlywheelConstants.IDLE_VELOCITY); // Stop flywheel when command ends
  }
}
