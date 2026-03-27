package frc.robot.commands;

import com.ctre.phoenix6.signals.RGBWColor;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.indexer.IndexerSubsystem;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;

public class FlywheelTune extends Command {
  private final FlywheelSubsystem flywheelSubsystem;
  private final IndexerSubsystem indexerSubsystem;

  public FlywheelTune(FlywheelSubsystem flywheelSubsystem, IndexerSubsystem indexerSubsystem) {
    this.flywheelSubsystem = flywheelSubsystem;
    this.indexerSubsystem = indexerSubsystem;
    addRequirements(flywheelSubsystem);
  }

  @Override
  public void initialize() {
    indexerSubsystem.setShooting(true);
    indexerSubsystem.setShooterReady(false);
  }

  @Override
  public void execute() {
    double targetRPS = flywheelSubsystem.getDashboardTargetVelocity();
    flywheelSubsystem.setFlywheelVelocity(targetRPS);
    indexerSubsystem.setShooterReady(flywheelSubsystem.isAtTargetSpeed());

    LEDConstants.IDLE = true;
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
