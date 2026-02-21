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
    if (0 == 0) {
      //TODO: change this 0 and 0 to some variable so it checks if the button x is being pressed or not
      //location check of the intake needed (encoder???? or limit switch?)
        if (IntakeLevelSubsystem.isIntakeDown == false) {
            IntakeLevelSubsystem.runDOWN();
        } else {
            IntakeLevelSubsystem.runUP();
        }
        
    }
  }
}
