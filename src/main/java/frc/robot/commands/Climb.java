package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climb.ClimbConstants;
import frc.robot.subsystems.climb.ClimbSubsystem;

public class Climb extends Command {
  public static ClimbSubsystem ClimbSub = new ClimbSubsystem();


@Override
  public void initialize() {
    if (ClimbSub.isExtended == false){
      ClimbSub.Extend(); 
      this.withTimeout(ClimbConstants.time);
    ClimbSub.Stop();
    
    ClimbSub.isExtended = true;
      
    } else {
      ClimbSub.Retract();
      this.withTimeout(ClimbConstants.time);
      ClimbSub.Stop();
      ClimbSub.isExtended = false;
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
   // climbSubsystem.Extend();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}
}
