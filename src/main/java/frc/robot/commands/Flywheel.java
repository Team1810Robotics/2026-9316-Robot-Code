package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;

/** Flywheel command */
public class Flywheel extends Command {
    private final FlywheelSubsystem flywheelSubsystem;

    public Flywheel(FlywheelSubsystem flywheelSubsystem) {
        this.flywheelSubsystem = flywheelSubsystem;
        addRequirements(flywheelSubsystem);
    }

    @Override
    public void initialize() {
        flywheelSubsystem.setFlywheelVelocity(200.0);
    }

    @Override
    public void execute() {
        if (!flywheelSubsystem.getBeamBreakTriggered()) {
            flywheelSubsystem.setFlywheelVelocity(200.0); // This value needs to be fine tuned.
        } else {
            flywheelSubsystem.setFlywheelVelocity(0.0);
        }
    }

    @Override
    public void end(boolean interrupted) {
        flywheelSubsystem.setFlywheelVelocity(0.0);
    }
}