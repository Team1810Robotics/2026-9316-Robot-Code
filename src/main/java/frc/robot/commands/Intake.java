package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class Intake extends Command {
  private IntakeSubsystem intakeSubsystem;
  private double levelSpeed;
  private double intakeLevelDegrees;
  private LevelState mode;

  enum LevelState {
    Up,
    Down,
    Immobile
  }

  /**
   * Intake command to run the intake motor
   *
   * @param intakeSubsystem The IntakeSubsystem to run the command on.
   */
  public Intake(IntakeSubsystem intakeSubsystem, double Speed) {
    this.intakeSubsystem = intakeSubsystem;
    addRequirements(intakeSubsystem);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    intakeLevelDegrees = intakeSubsystem.getIntakeEncoder();

    // TODO: Gut this, use a PID controller instead. Ask Payton.
    if (intakeLevelDegrees <= 0) {
      mode = LevelState.Up;
    } else if (intakeLevelDegrees >= 67) {
      mode = LevelState.Down;
    }

    if (mode == LevelState.Down) {
      while (intakeLevelDegrees >= 0) {
        intakeSubsystem.runDOWN(levelSpeed);
      }
      mode = LevelState.Immobile;
      // tweak number
    } else if (mode == LevelState.Up) {
      while (intakeLevelDegrees <= 67) {
        intakeSubsystem.runUP(levelSpeed);
      }
      mode = LevelState.Immobile;
      // tweak number
    }
  }

  @Override
  public void end(boolean interrupted) {
    intakeSubsystem.intakeMotor.stopMotor();
    intakeSubsystem.stopIntakeLevel();
  }
}
