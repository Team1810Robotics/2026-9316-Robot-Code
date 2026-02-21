package frc.robot.commands;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeLevelSubsystem;
import frc.robot.subsystems.intake.IntakeConstants;
import edu.wpi.first.wpilibj.DigitalInput;


public class IntakeLevel extends Command{
  private static IntakeLevelSubsystem IntakeLevelSubsystem;
  public DigitalInput intakeLimitSwitch;

  public IntakeLevel(IntakeLevelSubsystem intakeLevelSubsystem) {
    IntakeLevel.IntakeLevelSubsystem = intakeLevelSubsystem;
    addRequirements(intakeLevelSubsystem);
    intakeLimitSwitch = new DigitalInput(IntakeConstants.INTAKE_LIMIT_SWITCH);

  }

  @Override
  public void execute() {
    if (intakeLimitSwitch.get() == true) {
      //limit switch
      
        if (IntakeLevelSubsystem.isIntakeDown == false) {
            IntakeLevelSubsystem.runDOWN();
        } else if (IntakeLevelSubsystem.isIntakeDown == true) {
            IntakeLevelSubsystem.runUP();
        } else {
            IntakeLevelSubsystem.stop();
        }
    } else {
      //not limit switch
      IntakeLevelSubsystem.stop();
    }
  }
}
