package frc.robot.commands;

import org.opencv.features2d.FlannBasedMatcher;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.hood.HoodSubsystem;

/*
 * notes go here:
 *
 * kraken x44 motor
 *
 * encoder (tell where the hood is based on how many rotations it has done)
 *   0 = hood is fully down, 1 = hood is fully up (or something like that)
 *
 * up and down movement
 *
 */

public class Hood extends Command {
  private static HoodSubsystem hoodSubsystem;
  private double hoodSpeed;
  private double otherHoodSpeed;
  double hoodDegrees;
  HoodMode mode;

  enum HoodMode {
    Up,
    Down,
    Immobile,
    goToPos1,
    goToPos2,
    goToPos3
  }

  public Hood(HoodSubsystem hoodSubsystem, double Speed, boolean isGoToPos) {
    this.hoodSubsystem = hoodSubsystem;
    addRequirements(hoodSubsystem);

    if (isGoToPos) {
      otherHoodSpeed = Speed;
    } else {
      hoodSpeed = Speed;
    }
  }

  @Override
  public void end(boolean interrupted) {
    hoodSubsystem.stopHood();
  }

  @Override
  public void execute() {
   if (hoodSubsystem.hoodEncoder.get() > 0.9 && hoodSubsystem.hoodEncoderWasHigh) {
    hoodSubsystem.hoodEncoderWasHigh = false;
    hoodSubsystem.hoodEncoderRotations++;
}
  else if (hoodSubsystem.hoodEncoder.get() < 0.1 && !hoodSubsystem.hoodEncoderWasHigh) {
    hoodSubsystem.hoodEncoderWasHigh = true;
}