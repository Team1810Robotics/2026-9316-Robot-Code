package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
//*import com.ctre.phoenix6.configs.TalonFXConfigurator;
import frc.robot.subsystems.FlywheelSub;
//import frc.robot.Constants;

/** Flywheel command */


public class Flywheel extends Command {
  private static FlywheelSub FlywheelSub = new FlywheelSub();


@Override
  public void initialize() {FlywheelSub.runFlywheel();}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (FlywheelSub.Flybreak.get() == false) {
      FlywheelSub.runFlywheel();
    }else {
      FlywheelSub.stopThrowing();
    }
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted){
  }
  
}