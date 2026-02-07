package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
//*import com.ctre.phoenix6.configs.TalonFXConfigurator;
//*import com.ctre.phoenix6.hardware.TalonFX;
import frc.robot.subsystems.ClimbSub;

/** Flywheel command */


public class ClimbUp extends Command {
  //*private final TalonFX m_motor = new TalonFX(0);
  public static ClimbSub ClimbSub = new ClimbSub();

@Override
  public void initialize() {ClimbSub.Extend();}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {ClimbSub.Stop();}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}