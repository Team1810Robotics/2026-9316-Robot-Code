package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
//*import com.ctre.phoenix6.configs.TalonFXConfigurator;
//*import com.ctre.phoenix6.hardware.TalonFX;
import frc.robot.subsystems.FlywheelSub;

/** Flywheel command */


public class Flywheel extends Command {
  //*private final TalonFX m_motor = new TalonFX(0);
  public static FlywheelSub FlywheelSub = new FlywheelSub();

@Override
  public void initialize() {FlywheelSub.runFlywheel();}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {FlywheelSub.stopThrowing();}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}