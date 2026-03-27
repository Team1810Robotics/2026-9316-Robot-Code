package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.indexer.IndexerSubsystem;

public class FlywheelTune extends Command {
  private final FlywheelSubsystem flywheelSubsystem;
  private final IndexerSubsystem indexerSubsystem;

  public FlywheelTune(FlywheelSubsystem flywheelSubsystem, IndexerSubsystem indexerSubsystem) {
    this.flywheelSubsystem = flywheelSubsystem;
    this.indexerSubsystem = indexerSubsystem;
    addRequirements(flywheelSubsystem);
  }

  @Override
  public void initialize() {
    indexerSubsystem.setShooting(true);
    indexerSubsystem.setShooterReady(false);
  }

  @Override
  public void execute() {
    double targetRPS = flywheelSubsystem.getDashboardTargetVelocity();
    flywheelSubsystem.setFlywheelVelocity(targetRPS);
    indexerSubsystem.setShooterReady(flywheelSubsystem.isAtTargetSpeed());
  }

  @Override
  public void end(boolean interrupted) {
    flywheelSubsystem.stopFlywheel();
    indexerSubsystem.setShooting(false);
    indexerSubsystem.setShooterReady(false);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
