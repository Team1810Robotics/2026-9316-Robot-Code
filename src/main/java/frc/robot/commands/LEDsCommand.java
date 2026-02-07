package frc.robot.commands;

import frc.robot.subsystems.LEDSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

public class LEDsCommand extends Command {

    private LEDSubsystem LEDsubsystem;

    public LEDsCommand(LEDSubsystem subsystem /*LEDSubsystem.AnimationType animationType*/) {
        LEDsubsystem = subsystem;
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        LEDsubsystem.StartLEDSubsystem();
    }

    @Override
    public void end(boolean interrupted) {
        LEDsubsystem.StopLEDSubsystem();
    }

    @Override
    public boolean isFinished() {
        return false; // Runs until interrupted
    }
}
