package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.hood.HoodSubsystem;

public class SetHoodPosition extends Command {
    private final HoodSubsystem hoodSubsystem;
    private final double targetDeg;

    public SetHoodPosition(HoodSubsystem hoodSubsystem, double targetDeg) {
        this.hoodSubsystem = hoodSubsystem;
        this.targetDeg = targetDeg;
        addRequirements(hoodSubsystem);
    }

    @Override
    public void initialize() {
        hoodSubsystem.setTargetDegrees(targetDeg);
    }

    @Override
    public void end(boolean interrupted) {
        hoodSubsystem.stopHood();
    }

    @Override
    public boolean isFinished() {
        return hoodSubsystem.atTarget() || !hoodSubsystem.isZeroed();
    }
}