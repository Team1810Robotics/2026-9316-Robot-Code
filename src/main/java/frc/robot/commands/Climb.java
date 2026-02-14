package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
// *import com.ctre.phoenix6.configs.TalonFXConfigurator;
// *import com.ctre.phoenix6.hardware.TalonFX;
import frc.robot.subsystems.climb.ClimbSubsystem;

/** Flywheel command */
public class Climb extends Command {
  // *private final TalonFX m_motor = new TalonFX(0);
  private final ClimbSubsystem climbSubsystem; // Sam's Fix: Changed from static field to instance field (per Sam's note: "Do not create new subsystems in commands, create 1 in RobotContainer and pass it around")

  // Sam Notes
  // Needs a constructor
  // Main code should be in excecute

  // Sam's Fix: Added constructor to accept ClimbSubsystem as parameter (per Sam's note)
  // This ensures the same subsystem instance from RobotContainer is used
  public Climb(ClimbSubsystem climbSubsystem) {
    this.climbSubsystem = climbSubsystem;
    addRequirements(climbSubsystem);
  }

  // Sam's Fix: Moved Extend() from initialize() to execute() (per Sam's note: "Main code should be in execute")
  @Override
  public void initialize() {
    // Initialization code can go here if needed (ran once when command starts)
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Sam's Fix: Main command logic now runs every scheduler cycle
    climbSubsystem.Extend();
  }

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
