package frc.robot.commands;


import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeConstants.Mode;
import frc.robot.subsystems.intake.IntakeLevelSubsystem;




public class IntakeLevel extends Command{
 
    
  private static IntakeLevelSubsystem IntakeLevelSubsystem;


  @Override
  public void execute() {
        if (IntakeLevelSubsystem.isIntakeDown == false) {
            IntakeLevelSubsystem.runDOWN();
        } else {
            IntakeLevelSubsystem.runUP();
        }
        
    }
}
