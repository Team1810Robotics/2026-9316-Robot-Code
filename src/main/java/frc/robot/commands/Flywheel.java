package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
// *import com.ctre.phoenix6.configs.TalonFXConfigurator;
// import frc.robot.Constants;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;

/** Flywheel command */
public class Flywheel extends Command {
  private static FlywheelSubsystem flywheelSubsystem = new FlywheelSubsystem();

  @Override
  public void initialize() {
    flywheelSubsystem.runFlywheel();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (flywheelSubsystem.Flybreak.get() == false) {
      flywheelSubsystem.runFlywheel();
    } else {
      flywheelSubsystem.stopThrowing();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}
}
