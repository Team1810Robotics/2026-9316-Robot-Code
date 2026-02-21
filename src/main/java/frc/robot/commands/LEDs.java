package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.led.LEDSubsystem;

public class LEDs extends Command {

  private LEDSubsystem LEDsubsystem;

  public LEDs(LEDSubsystem subsystem /*LEDSubsystem.AnimationType animationType*/) {
    LEDsubsystem = subsystem;
    addRequirements(subsystem);
  }

  @Override
  public void initialize() {
    LEDsubsystem.StartLEDSubsystem(LEDsubsystem.visionSubsystem);
  }

  @Override
  public void end(boolean interrupted) {
    LEDsubsystem.StopLEDSubsystem();
  }

  @Override
  public boolean isFinished() {
    return false; // Runs until interrupted
  }
}
