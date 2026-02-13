package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.indexer.IndexerSubsystem;

public class Indexer extends Command {
  private final IndexerSubsystem indexer;

  public Indexer(IndexerSubsystem indexer) {
    this.indexer = indexer;
  }

  //Sam Notes
  // main code should be in execute, not initialize
  
  @Override
  public void initialize() {
    indexer.RunIndexer();
  }

  @Override
  public void end(boolean interrupted) {
    indexer.StopIndexer();
  }

  @Override
  public boolean isFinished() {
    return false; // Runs until interrupted
  }
}
