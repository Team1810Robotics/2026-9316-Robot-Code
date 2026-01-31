package frc.robot.commands;

import frc.robot.subsystems.Indexer;
import edu.wpi.first.wpilibj2.command.Command;

public class IndexerCommand extends Command {
    private final Indexer indexer;

    public IndexerCommand(Indexer indexer) {
        this.indexer = indexer;
    }

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
