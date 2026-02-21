package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class Intake extends Command {
  private IntakeSubsystem intakeSubsystem;
  private double buttonSpeed;

    /**
     * Intake command to run the intake motor
     * @param intakeSubsystem The IntakeSubsystem to run the command on.
     */

  public Intake(IntakeSubsystem intakeSubsystem, double Speed) {
    this.intakeSubsystem = intakeSubsystem;
    addRequirements(intakeSubsystem);
   buttonSpeed = Speed;
  }

  @Override
  public void execute() {
    intakeSubsystem.run(buttonSpeed);
  }


  @Override
  public void end(boolean interrupted) {
    intakeSubsystem.intakeMotor.stopMotor();
  }
}
