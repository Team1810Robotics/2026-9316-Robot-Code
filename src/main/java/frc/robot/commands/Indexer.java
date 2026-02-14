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
  
  // Sam's Fix: Moved RunIndexer from initialize() to execute() (per Sam's note)
  // Main command logic should run in execute(), not initialize()
  @Override
  public void initialize() {
    // Initialization code can go here if needed (ran once when command starts)
  }

  @Override
  public void execute() {
    // Sam's Fix: Main indexer logic now runs every scheduler cycle
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
