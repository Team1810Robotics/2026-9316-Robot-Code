package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class Intake extends Command {
  private IntakeSubsystem intakeSubsystem;
  
  public static float intakeSpeed;
    private double buttonSpeed;

    /**
     * Intake command that runs the intake motor at a certain speed depending on
     * @param intakeSubsystem The IntakeSubsystem to run the command on.
     */


  public Intake(IntakeSubsystem intakeSubsystem, double Speed) {
    this.intakeSubsystem = intakeSubsystem;
    addRequirements(intakeSubsystem);

   buttonSpeed = Speed;

  }

  @Override
  public void execute() {
    // intakeSubsystem.intakeMotor.set(intakeSpeed);
    intakeSubsystem.run(buttonSpeed);
  }


  @Override
  public void end(boolean interrupted) {
    intakeSubsystem.intakeMotor.stopMotor();
  }
}
