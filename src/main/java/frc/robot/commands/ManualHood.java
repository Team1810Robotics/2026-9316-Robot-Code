package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.hood.HoodSubsystem;

public class ManualHood extends Command {
    private final HoodSubsystem hoodSubsystem;
    private final DoubleSupplier speedSupplier;

    public ManualHood(HoodSubsystem hoodSubsystem, DoubleSupplier speedSupplier) {
        this.hoodSubsystem = hoodSubsystem;
        this.speedSupplier = speedSupplier;
        addRequirements(hoodSubsystem);
    }

    @Override
    public void execute() {
        hoodSubsystem.setManualOutput(speedSupplier.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        hoodSubsystem.stopHood();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}