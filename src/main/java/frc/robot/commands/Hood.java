package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.hood.HoodSubsystem;

public class Hood extends Command {
  private final HoodSubsystem hoodSubsystem;
  private final double hoodValue;
  private final boolean isGoToPos;

  public Hood(HoodSubsystem hoodSubsystem, double speedOrPosition, boolean isGoToPos) {
    this.hoodSubsystem = hoodSubsystem;
    this.hoodValue = speedOrPosition;
    this.isGoToPos = isGoToPos;
    addRequirements(hoodSubsystem);
  }

  @Override
  public void initialize() {
    if (isGoToPos) {
      hoodSubsystem.setPoint(hoodValue);
    }
  }

  @Override
  public void execute() {
    if (!isGoToPos) {
      if (hoodValue > 0) {
        hoodSubsystem.runUP(hoodValue);
      } else if (hoodValue < 0) {
        hoodSubsystem.runDOWN(Math.abs(hoodValue));
      } else {
        hoodSubsystem.stopHood();
      }
    }
  }

  @Override
  public void end(boolean interrupted) {
    hoodSubsystem.stopHood();
  }

  @Override
  public boolean isFinished() {
    return isGoToPos && hoodSubsystem.isAtSetPoint();
  }
}
