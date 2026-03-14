package frc.robot.commands;

import com.ctre.phoenix6.signals.RGBWColor;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.indexer.IndexerSubsystem;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;

public class Flywheel extends Command {
  private final FlywheelSubsystem flywheelSubsystem;
  private final IndexerSubsystem indexerSubsystem;
  private final double fallbackVelocity;

  public Flywheel(
      FlywheelSubsystem flywheelSubsystem,
      IndexerSubsystem indexerSubsystem,
      double fallbackVelocity) {
    this.flywheelSubsystem = flywheelSubsystem;
    this.indexerSubsystem = indexerSubsystem;
    this.fallbackVelocity = fallbackVelocity;

    addRequirements(flywheelSubsystem);
  }

  @Override
  public void initialize() {
    flywheelSubsystem.setDefaultVelocity(fallbackVelocity);
    indexerSubsystem.setShooting(true);
    indexerSubsystem.setShooterReady(false);
  }

  @Override
  public void execute() {
    flywheelSubsystem.runSelectedVelocity();

    indexerSubsystem.setShooterReady(flywheelSubsystem.isAtTargetSpeed());

    LEDConstants.IDLE = false;
    LEDSubsystem.setLEDColor(
        new RGBWColor(LEDConstants.WHITE[0], LEDConstants.WHITE[1], LEDConstants.WHITE[2], 0),
        false);
  }

  @Override
  public void end(boolean interrupted) {
    flywheelSubsystem.stopFlywheel();
    indexerSubsystem.setShooting(false);
    indexerSubsystem.setShooterReady(false);
    LEDConstants.IDLE = true;
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}