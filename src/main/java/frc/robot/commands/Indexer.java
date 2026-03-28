package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.indexer.IndexerSubsystem;

public class Indexer extends Command {
  private final IndexerSubsystem indexer;
  private boolean reverse;

  public Indexer(IndexerSubsystem indexer, boolean reverse) {
    this.indexer = indexer;
    this.reverse = reverse;
    addRequirements(indexer);
  }

  @Override
  public void initialize() {
    if (reverse) {
      indexer.setReverseBoth(true);
    } else {
      indexer.setIndexEnabled(true);// using setIndexEnabled() method which doesnt exist so i changed it, might not be what we want
    }
  }

  @Override
  public void end(boolean interrupted) {
    if (reverse) {
      indexer.setReverseBoth(false);
    } else {
      indexer.setIndexEnabled(false);// using setIndexEnabled() method which doesnt exist so i changed it, might not be what we want
    }
    indexer.stopAll();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
