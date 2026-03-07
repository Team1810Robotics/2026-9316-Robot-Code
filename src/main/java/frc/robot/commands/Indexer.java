package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.indexer.IndexerSubsystem;

public class Indexer extends Command {
  private final IndexerSubsystem indexer;

  public Indexer(IndexerSubsystem indexer) {
    this.indexer = indexer;
    addRequirements(indexer);
  }

  @Override
  public void initialize() {
    indexer.SetIndexEnabled(true);
  }

  @Override
  public void end(boolean interrupted) {
    indexer.SetIndexEnabled(false);
  }

  @Override
  public boolean isFinished() {
    return false; // Runs until interrupted
  }
}
