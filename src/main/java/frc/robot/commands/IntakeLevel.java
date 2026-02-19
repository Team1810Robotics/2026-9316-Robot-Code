package frc.robot.commands;


import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeLevelSubsystem;


public class IntakeLevel extends Command{
 
  private static IntakeLevelSubsystem IntakeLevelSubsystem;


  public IntakeLevel(IntakeLevelSubsystem intakeLevelSubsystem) {
    this.IntakeLevelSubsystem = intakeLevelSubsystem;
    addRequirements(intakeLevelSubsystem);

  }

  @Override
  public void execute() {
        if (IntakeLevelSubsystem.isIntakeDown == false) {
            IntakeLevelSubsystem.runDOWN();
        } else {
            IntakeLevelSubsystem.runUP();
        }
        
    }
}
