package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class Intake extends Command {
  private IntakeSubsystem intakeSubsystem;
  private double buttonSpeed;
  private double levelSpeed;
  double intakeLevelDegrees;
  LevelMode mode;

  enum LevelMode {
    Up,
    Down,
    Immobile
  }

  /**
   * Intake command to run the intake motor
   *
   * @param intakeSubsystem The IntakeSubsystem to run the command on.
   */
  public Intake(IntakeSubsystem intakeSubsystem, double Speed, boolean isIntakeLevel) {
    this.intakeSubsystem = intakeSubsystem;
    addRequirements(intakeSubsystem);
    if (isIntakeLevel) {
      levelSpeed = Speed;
    } else {
      buttonSpeed = Speed;
    }
  }

  @Override
  public void execute() {
    intakeLevelDegrees = intakeSubsystem.getIntakeEncoder();
    if (intakeLevelDegrees <= 0) {
      mode = LevelMode.Up;
    } else if (intakeLevelDegrees >= 67) {
      mode = LevelMode.Down;
    }

    if (mode == LevelMode.Down) {
      while (intakeLevelDegrees >= 0) {
        intakeSubsystem.runDOWN(levelSpeed);
      }
      mode = LevelMode.Immobile;
      // tweak number
    } else if (mode == LevelMode.Up) {
      while (intakeLevelDegrees <= 67) {
        intakeSubsystem.runUP(levelSpeed);
      }
      mode = LevelMode.Immobile;
      // tweak number
    }

    if (buttonSpeed != 0) {
      intakeSubsystem.run(buttonSpeed);
    }
  }

  @Override
  public void end(boolean interrupted) {
    intakeSubsystem.intakeMotor.stopMotor();
    intakeSubsystem.stopIntakeLevel();
  }
}
