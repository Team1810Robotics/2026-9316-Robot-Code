package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class Intake extends Command {
  private IntakeSubsystem intakeSubsystem;
  private double intakeManualSpeed;
  private double intakePositionSpeed;
  private double PIDSetpoint;
  double intakeLevelDegrees;
  LevelMode mode;

  public enum LevelMode {
    Up,
    Down,
    Immobile
  }

  public enum RunType {
    Intake,
    UsePID,
    MoveIntakeInOrOut
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

    if (this.runType == RunType.MoveIntakeInOrOut) {
      intakePositionSpeed = SpeedOrSetPoint;

    } else if (this.runType == RunType.Intake) {
      intakeManualSpeed = SpeedOrSetPoint;

    } else if (this.runType == RunType.UsePID) {
      PIDSetpoint = SpeedOrSetPoint;
    }
  }

  @Override
  public void execute() {
    SmartDashboard.putNumber("Intake Encoder Raw", intakeSubsystem.intakeEncoder.get());
    /*


    //TODO: use instant commands to run intake and use this command to run the intake posiiton using PID and encoder


    */
    intakeSubsystem.run(intakeManualSpeed);
    if (intakePositionSpeed <= 0.0) intakeLevelDegrees = intakeSubsystem.getIntakeEncoder();
    // if (intakeLevelDegrees <= 0) {
    //   mode = LevelMode.Up;
    // } else if (intakeLevelDegrees >= 67) {
    //   mode = LevelMode.Down;
    // }

    // if (mode == LevelMode.Down) {
    //   while (intakeLevelDegrees >= 0) {
    //     intakeSubsystem.runDOWN(intakePositionSpeed);
    //   }
    //   mode = LevelMode.Immobile;
    //   // tweak number
    // } else if (mode == LevelMode.Up) {
    //   while (intakeLevelDegrees <= 67) {
    //     intakeSubsystem.runUP(intakePositionSpeed);
    //   }
    //   mode = LevelMode.Immobile;
    //   // tweak number
    // }

    if (intakeManualSpeed != 0) {
      intakeSubsystem.run(intakeManualSpeed);
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
