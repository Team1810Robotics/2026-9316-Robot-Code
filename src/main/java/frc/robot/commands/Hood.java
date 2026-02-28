package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.hood.HoodConstants;

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
  private HoodSubsystem hoodSubsystem;
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
  public void execute() {
    hoodDegrees = hoodSubsystem.setHoodEncoder();
    if (hoodDegrees <= 0) {
      mode = HoodMode.Up;
    } else if (hoodDegrees >= 67) {
      mode = HoodMode.Down;
    }

    if (mode == HoodMode.Down) {
      while (hoodDegrees >= 0) {
        hoodSubsystem.runDOWN(hoodSpeed);
      }
      mode = HoodMode.Immobile;
      // tweak number
    } else if (mode == HoodMode.Up) {
      while (hoodDegrees <= 67) {
        hoodSubsystem.runUP(hoodSpeed);
      }
      mode = HoodMode.Immobile;
      // tweak number
    }

    if (hoodSpeed != 0) {
      while (mode == HoodMode.goToPos1 && hoodDegrees <= HoodConstants.POSITION1) {
          hoodSubsystem.runUP(otherHoodSpeed);
          if (hoodDegrees >= HoodConstants.POSITION1) {
            mode = HoodMode.Immobile;
          }
        } while (mode == HoodMode.goToPos1 && hoodDegrees >= HoodConstants.POSITION1) {
          hoodSubsystem.runDOWN(otherHoodSpeed);
          if (hoodDegrees <= HoodConstants.POSITION1) {
            mode = HoodMode.Immobile;
          }
        } while (mode == HoodMode.goToPos2 && hoodDegrees <= HoodConstants.POSITION2) {
          hoodSubsystem.runUP(otherHoodSpeed);
          if (hoodDegrees >= HoodConstants.POSITION2) {
            mode = HoodMode.Immobile;
          }
        } while (mode == HoodMode.goToPos2 && hoodDegrees >= HoodConstants.POSITION2) {
          hoodSubsystem.runDOWN(otherHoodSpeed);
          if (hoodDegrees <= HoodConstants.POSITION2) {
            mode = HoodMode.Immobile;
          }
        } while (mode == HoodMode.goToPos3 && hoodDegrees <= HoodConstants.POSITION3) {
          hoodSubsystem.runUP(otherHoodSpeed);
          if (hoodDegrees >= HoodConstants.POSITION3) {
            mode = HoodMode.Immobile;
          }
        } while (mode == HoodMode.goToPos3 && hoodDegrees >= HoodConstants.POSITION3) {
          hoodSubsystem.runDOWN(otherHoodSpeed);
            if (hoodDegrees <= HoodConstants.POSITION3) {
                mode = HoodMode.Immobile;
            }
        } 
    }
    // tweak all numbers for positions
  }

  @Override
  public void end(boolean interrupted) {
    hoodSubsystem.stopHood();
  }
}
