package frc.robot.commands;

import java.util.logging.Level;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class Intake extends Command {
  private IntakeSubsystem intakeSubsystem;
  private double buttonSpeed;
  private double levelSpeed;
  private boolean isIntakeDown;
  public DigitalInput intakeLimitSwitch;
  LevelMode mode;
  

  enum LevelMode {
    Up,
    Down,
    Imobile  
  }

    /**
     * Intake command to run the intake motor
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
   intakeLimitSwitch = new DigitalInput(IntakeConstants.INTAKE_LIMIT_SWITCH);
  }

  @Override
  public void execute() {

    while (intakeLimitSwitch.get() == false) {
      intakeSubsystem.runDOWN(levelSpeed);
    }
    
     if (intakeLimitSwitch.get() == false) {
      //up
      mode = LevelMode.Up;
    } else {
      //down
      mode = LevelMode.Down;
    }

    while (mode == LevelMode.Up) {
            intakeSubsystem.runDOWN(levelSpeed);
    }
    while (mode == LevelMode.Down) {
            intakeSubsystem.runUP(levelSpeed);
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
