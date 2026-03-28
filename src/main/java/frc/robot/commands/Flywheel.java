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
  private final LEDSubsystem ledSubsystem;

  public Flywheel(
      FlywheelSubsystem flywheelSubsystem,
      IndexerSubsystem indexerSubsystem,
      double fallbackVelocity,
      LEDSubsystem ledSubsystem) {
    this.flywheelSubsystem = flywheelSubsystem;
    this.indexerSubsystem = indexerSubsystem;
    this.fallbackVelocity = fallbackVelocity;
    this.ledSubsystem = ledSubsystem;

    addRequirements(flywheelSubsystem, indexerSubsystem);
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

    boolean atSpeed = flywheelSubsystem.isAtTargetSpeed();
    indexerSubsystem.setShooterReady(atSpeed);

    if (atSpeed) {
      //indexerSubsystem.runBothForward();   // or runIndexer1Forward/runIndexer2Forward if separate
      LEDSubsystem.setLEDColor(
          new RGBWColor(
              LEDConstants.GREEN[0],
              LEDConstants.GREEN[1],
              LEDConstants.GREEN[2],
              0),
          false);
    } else {
      //indexerSubsystem.stopAll();
      LEDSubsystem.setLEDColor(
          new RGBWColor(
              LEDConstants.WHITE[0],
              LEDConstants.WHITE[1],
              LEDConstants.WHITE[2],
              0),
          false);
    }

    ledSubsystem.LedIdleChange(false);
  }

  @Override
  public void end(boolean interrupted) {
    flywheelSubsystem.stopFlywheel();
    indexerSubsystem.stopAll();
    indexerSubsystem.setShooting(false);
    indexerSubsystem.setShooterReady(false);
    ledSubsystem.LedIdleChange(true);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}