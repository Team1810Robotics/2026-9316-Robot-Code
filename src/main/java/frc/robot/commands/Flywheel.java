package frc.robot.commands;

import com.ctre.phoenix6.signals.RGBWColor;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.flywheel.FlywheelConstants;
// *import com.ctre.phoenix6.configs.TalonFXConfigurator;
// import frc.robot.Constants;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.indexer.*;
import frc.robot.subsystems.led.*;

/** Flywheel command */
public class Flywheel extends Command {
  private final FlywheelSubsystem flywheelSubsystem;
  private final double targetVelocity;

  public Flywheel(FlywheelSubsystem flywheelSubsystem, double targetVelocity) {
    this.flywheelSubsystem = flywheelSubsystem;
    this.targetVelocity = targetVelocity;

    addRequirements(flywheelSubsystem);
  }

  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled
  @Override
  public void execute() {
    flywheelSubsystem.setFlywheelVelocity(targetVelocity);
    IndexerSubsystem.SetShooting(true);
    LEDConstants.IDLE =
        false; // Set idle to false to prevent the periodic method in the LED subsystem from
    // changing the LED color
    LEDSubsystem.setLEDColor(
        new RGBWColor(LEDConstants.WHITE[0], LEDConstants.WHITE[1], LEDConstants.WHITE[2], 0),
        false); // Set LEDs to white when shooting
  }

  // Called once the command ends or is interrupted
  @Override
  public void end(boolean interrupted) {
    flywheelSubsystem.setFlywheelVelocity(
        FlywheelConstants.IDLE_VELOCITY); // Stop flywheel when command ends
    IndexerSubsystem.SetShooting(false);
    LEDConstants.IDLE =
        true; // Set idle to true to allow the periodic method in the LED subsystem to change the
    // LED color back to the default
  }
}
