package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.indexer.*;

public class Indexer extends Command {
  private final IndexerSubsystem indexer;

  public Indexer(IndexerSubsystem indexer,boolean reverse) {
    if (reverse == true) {
      IndexerConstants.Reverse = true;
    } else {
      IndexerConstants.Reverse = false;
    }
    this.indexer = indexer;
    addRequirements(indexer);
  }

  @Override
  public void initialize() {
    indexer.inde(true);
  }

  @Override
  public void end(boolean interrupted) {
    indexer.indexingEnabled(false);
  }

  @Override
  public boolean isFinished() {
    return false; // Runs until interrupted
  }
}
