package frc.robot.commands;

// import org.opencv.features2d.FlannBasedMatcher;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climb.ClimbConstants;
import frc.robot.subsystems.climb.ClimbSubsystem;

/** Flywheel command */
public class Climb extends Command {
  private final ClimbSubsystem ClimbSubsystem;

  public Climb(ClimbSubsystem climbSubsystem) {
    this.ClimbSubsystem = climbSubsystem;
  }

  @Override
  public void initialize() {

    if (ClimbSubsystem.isExtended == false) {
      ClimbSubsystem.Extend();
      this.withTimeout(ClimbConstants.time);
      ClimbSubsystem.Stop();

      ClimbSubsystem.isExtended = true;

    } else {
      ClimbSubsystem.Retract();
      this.withTimeout(ClimbConstants.time);
      ClimbSubsystem.Stop();
      ClimbSubsystem.isExtended = false;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}
}
