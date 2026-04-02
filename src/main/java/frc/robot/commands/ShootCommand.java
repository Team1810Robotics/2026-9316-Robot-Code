package frc.robot.commands;

import com.ctre.phoenix6.signals.RGBWColor;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.hood.HoodConstants;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.indexer.IndexerSubsystem;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;
import us.hebi.quickbuf.Descriptors.Descriptor;

public class ShootCommand extends Command {
  private final VisionSubsystem visionSubsystem;
  private final FlywheelSubsystem flywheelSubsystem;
  private final HoodSubsystem hoodSubsystem;
  private final IndexerSubsystem indexerSubsystem;
  private final LEDSubsystem ledSubsystem;

  private final Debouncer shooterReadyDebouncer = new Debouncer(0.15);

  private boolean useVisionShot = false;
  private double lockedTy = 0.0;
  public double shooterTiming = 1.0;
  public double startTime;
  public double currentTime;
  public boolean waited;

  public ShootCommand(
      VisionSubsystem visionSubsystem,
      FlywheelSubsystem flywheelSubsystem,
      HoodSubsystem hoodSubsystem,
      IndexerSubsystem indexerSubsystem,
      LEDSubsystem ledSubsystem) {
    this.visionSubsystem = visionSubsystem;
    this.flywheelSubsystem = flywheelSubsystem;
    this.hoodSubsystem = hoodSubsystem;
    this.indexerSubsystem = indexerSubsystem;
    this.ledSubsystem = ledSubsystem;

    addRequirements(hoodSubsystem, flywheelSubsystem, indexerSubsystem);
  }

  @Override
  public void initialize() {
    startTime = Timer.getFPGATimestamp();
    useVisionShot = visionSubsystem.targetValid();

    if (useVisionShot) {
      lockedTy = visionSubsystem.getTy();

      double hoodTarget = hoodSubsystem.computeHoodSetpointFromTY(lockedTy);
      hoodSubsystem.setVisionSetPoint(hoodTarget);
      hoodSubsystem.setPoint(hoodTarget);

      double rpm = flywheelSubsystem.computeFlywheelRPMFromTY(lockedTy);
      flywheelSubsystem.setFlywheelVelocity(rpm / 60.0);
    } else {
//  double manualRPS = flywheelSubsystem.getDashboardTargetVelocity();
  hoodSubsystem.setPoint(HoodConstants.DEFAULT_POSITION);
  flywheelSubsystem.setFlywheelVelocity(50);
    }

    indexerSubsystem.setShooting(true);
    indexerSubsystem.setShooterReady(false);

    // SmartDashboard.putBoolean("Shoot Use Vision", useVisionShot);
    // SmartDashboard.putNumber("Shoot Locked TY", lockedTy);
  }

  @Override
  public void execute() {
    boolean hoodReady = hoodSubsystem.isAtSetPoint();
    boolean flywheelReady = flywheelSubsystem.isAtTargetSpeed();
    currentTime = Timer.getFPGATimestamp();

    waited = currentTime - startTime > Timer.getFPGATimestamp();

    boolean aimReady = true;
    if (useVisionShot) {
      aimReady = visionSubsystem.targetValid() && Math.abs(visionSubsystem.getTx()) < 3.0;
    }

boolean rawShooterReady;

    if (useVisionShot) {
        rawShooterReady = hoodReady && flywheelReady && aimReady && waited;
    } else {
        rawShooterReady = currentTime - startTime > shooterTiming;
    }
    boolean debouncedShooterReady = shooterReadyDebouncer.calculate(rawShooterReady);

    indexerSubsystem.setShooterReady(debouncedShooterReady);

    // SmartDashboard.putBoolean("Shoot Hood Ready", hoodReady);
    // SmartDashboard.putBoolean("Shoot Flywheel Ready", flywheelReady);
    // SmartDashboard.putBoolean("Shoot Aim Ready", aimReady);
    // SmartDashboard.putBoolean("Shoot Ready", debouncedShooterReady);

  //   if (debouncedShooterReady && useVisionShot) {
  //     LEDSubsystem.setLEDColor(
  //         new RGBWColor(
  //             LEDConstants.GREEN[0],
  //             LEDConstants.GREEN[1],
  //             LEDConstants.GREEN[2],
  //             0),
  //         false);
  //     LEDSubsystem.setLEDAnimation("SingleFade", false);
  //   } else if (debouncedShooterReady) {
  //     LEDSubsystem.setLEDColor(
  //         new RGBWColor(
  //             LEDConstants.RED[0],
  //             LEDConstants.RED[1],
  //             LEDConstants.RED[2],
  //             0),
  //         false);
  //     LEDSubsystem.setLEDAnimation("SingleFade", false);
  //   }

  
  }

  @Override
  public void end(boolean interrupted) {
    hoodSubsystem.stopHood();
    flywheelSubsystem.runIdle();
    indexerSubsystem.stopAll();
    LEDSubsystem.setLEDAnimation("None", false);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}