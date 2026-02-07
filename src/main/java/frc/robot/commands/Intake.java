package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.IntakeConstants.Mode;
import frc.robot.subsystems.IntakeSubsystem;


public class Intake extends Command {
    private IntakeSubsystem intakeSubsystem;
    private IntakeConstants.Mode mode;


    /**
     * Intake command that runs the intake motor at a certain speed depending on
     * the {@link IntakeConstants.Mode} given.
     *
     * @param intakeSubsystem The IntakeSubsystem to run the command on.
     * @param mode           The mode to run the command in        
     */

     boolean pSensor = intakeSubsystem.proximitySensor.get();


    public Intake(IntakeSubsystem intakeSubsystem, IntakeConstants.Mode mode) {
        this.intakeSubsystem = intakeSubsystem;
        this.mode = mode;
        
        addRequirements(intakeSubsystem);
    }



    @Override
    public void execute() {
        if (mode == Mode.ON && pSensor) {
            intakeSubsystem.intakeMotor.set(1.0); // Run intake at full speed
        } else if (mode == Mode.OFF) {
            intakeSubsystem.intakeMotor.set(0.0); // Stop the intake
        } else if (mode == Mode.STOP) {
            intakeSubsystem.intakeMotor.stopMotor(); // Stop the intake
        }
    }


    @Override
    public void end(boolean interrupted) {
        intakeSubsystem.intakeMotor.stopMotor();
    }
}

