package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
// *import com.ctre.phoenix6.configs.TalonFXConfigurator;
// *import com.ctre.phoenix6.hardware.TalonFX;
import frc.robot.subsystems.climb.ClimbSubsystem;

/** Flywheel command */
public class Climb extends Command {
  // *private final TalonFX m_motor = new TalonFX(0);
  public static ClimbSubsystem climbSubsystem = new ClimbSubsystem(); // Do not create new subsystems in commands, create 1 in RobotContainer and pass it around

  // Sam Notes
  // Needs a constructor
  // Main code should be in excecute

  
  @Override
  public void initialize() {
    climbSubsystem.Extend();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    climbSubsystem.Retract();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
