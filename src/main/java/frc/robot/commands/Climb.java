package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climb.*;

public class Climb extends Command {
  private final ClimbSubsystem climbSubsystem;

  public Climb(ClimbSubsystem climbSubsystem) {
    this.climbSubsystem = climbSubsystem;
   // addRequirements(climbSubsystem);
  }

  @Override
   public void initialize() {
    if (climbSubsystem.isExtended == false) {
      climbSubsystem.Extend();
      this.withTimeout(ClimbConstants.time);
      climbSubsystem.Stop();

      climbSubsystem.isExtended = true;

    } else {
      climbSubsystem.Retract();
      this.withTimeout(ClimbConstants.time);
      climbSubsystem.Stop();
      climbSubsystem.isExtended = false;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    climbSubsystem.Extend();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}
}
