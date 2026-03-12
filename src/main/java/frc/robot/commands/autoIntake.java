package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class autoIntake extends Command {
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
    autoIntake,
    UsePID,
    MoveIntakeInOrOut
  }

  RunType runType;

  /**
   * autoIntake command to run the intake motor
   *
   * @param intakeSubsystem The IntakeSubsystem to run the command on.
   */
  public autoIntake(IntakeSubsystem intakeSubsystem) {
    this.intakeSubsystem = intakeSubsystem;
    addRequirements(intakeSubsystem);
  }

  @Override
  public void execute() {
    SmartDashboard.putNumber("autoIntake Encoder Raw", intakeSubsystem.intakeEncoder.get());
    intakeSubsystem.run(IntakeConstants.ROLLER_IN_SPEED);
    
  }
    // public class intake extends IntakeSubsystem {{
    //   new InstantCommand(() -> intakeSubsystem.run(0.5));
      
    //   }
    // }

  @Override
  public void end(boolean interrupted) {
    intakeSubsystem.intakeMotor.stopMotor();
    intakeSubsystem.stopIntakeLevel();
  }
}

