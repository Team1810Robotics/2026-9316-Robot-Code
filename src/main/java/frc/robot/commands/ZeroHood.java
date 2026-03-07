package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.hood.HoodSubsystem;

public class ZeroHood extends InstantCommand {
    public ZeroHood(HoodSubsystem hoodSubsystem) {
        super(hoodSubsystem::zeroCurrentPosition, hoodSubsystem);
    }
}