package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climb.*;

public class Climb extends Command {

  private final ClimbSubsystem climbSubsystem;
  private final double speed;

  public Climb(ClimbSubsystem climbSubsystem, double speed) {
    this.climbSubsystem = climbSubsystem;
    this.speed = speed;

    addRequirements(climbSubsystem);
  }

  @Override
  public void execute() {
    climbSubsystem.runClimb(speed);
  }

  @Override
  public void end(boolean interrupted) {
    climbSubsystem.stopClimb();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
