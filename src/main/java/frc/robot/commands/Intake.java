package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.*;

public class Intake extends Command {
    private final IntakeSubsystem intakeSubsystem;
    private final IntakeConstants.Mode mode;
    private final double sideSpeed;
    private final double wheelSpeed;

    /**
     * Intake command - over-the-bumper intake with side motors and wheel motor
     * 
     * Sequence:
     * 1. Side motors move game piece up into robot
     * 2. Wheel motor spins intake wheels to grab piece
     *
     * @param intakeSubsystem The IntakeSubsystem to run the command on
     * @param mode The mode to run in (ON, OFF, STOP)
     * @param sideSpeed Speed for side motors (-1.0 to 1.0)
     * @param wheelSpeed Speed for wheel motor (-1.0 to 1.0)
     */
    public Intake(IntakeSubsystem intakeSubsystem, IntakeConstants.Mode mode, 
                  double sideSpeed, double wheelSpeed) {
        this.intakeSubsystem = intakeSubsystem;
        this.mode = mode;
        this.sideSpeed = sideSpeed;
        this.wheelSpeed = wheelSpeed;

        addRequirements(intakeSubsystem);
    }

    @Override
    public void initialize() {
        // Reset mode when command starts
        intakeSubsystem.setMode(mode);
    }

    @Override
    public void execute() {
        // Check if object is detected on either side
        boolean objectDetected = intakeSubsystem.isObjectDetected();

        if (mode == IntakeConstants.Mode.ON) {
            // Run intake if object detected or commanded on
            if (objectDetected) {
                intakeSubsystem.run(sideSpeed, wheelSpeed);
            } else {
                // Can still run manually with this command
                intakeSubsystem.run(sideSpeed, wheelSpeed);
            }
        } else if (mode == IntakeConstants.Mode.OFF) {
            // Stop the intake
            intakeSubsystem.stop();
        } else if (mode == IntakeConstants.Mode.STOP) {
            // Explicit stop
            intakeSubsystem.stop();
        }
    }

    @Override
    public void end(boolean interrupted) {
        // Stop all motors when command ends
        intakeSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        // Command runs until interrupted
        return false;
    }
}
