package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class Intake extends Command {
  private IntakeSubsystem intakeSubsystem;
  private double buttonSpeed;
  private double levelSpeed;
  private double PIDSetpoint;
  double intakeLevelDegrees;
  LevelMode mode;

  enum LevelMode {
    Up,
    Down,
    Immobile
  }

  public enum RunType {
    Intake,
    UsePID,
    UseManual
  }

  RunType runType;

  /**
   * Intake command to run the intake motor
   *
   * @param intakeSubsystem The IntakeSubsystem to run the command on.
   */
  public Intake(IntakeSubsystem intakeSubsystem, double SpeedOrSetPoint, RunType runType) {
    this.intakeSubsystem = intakeSubsystem;
    addRequirements(intakeSubsystem);
    if (this.runType == RunType.UseManual) {
      levelSpeed = SpeedOrSetPoint;
    } else if (this.runType == RunType.Intake) {
      buttonSpeed = SpeedOrSetPoint;
    } else if (this.runType == RunType.UsePID) {
      PIDSetpoint = SpeedOrSetPoint;
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

    if (PIDSetpoint != 0) {
      intakeSubsystem.setPoint(PIDSetpoint);
    }
  }

  @Override
  public void end(boolean interrupted) {
    intakeSubsystem.intakeMotor.stopMotor();
    intakeSubsystem.stopIntakeLevel();
  }
}
