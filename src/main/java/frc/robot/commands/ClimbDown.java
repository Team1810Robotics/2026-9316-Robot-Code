package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ClimbSub;

public class ClimbDown extends Command {
//*private final TalonFX m_motor = new TalonFX(0);
  public static ClimbSub ClimbSub = new ClimbSub();

@Override
  public void initialize() {ClimbSub.Retract();}

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
