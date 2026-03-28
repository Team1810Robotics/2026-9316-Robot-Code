// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import dev.doglog.DogLog;
import dev.doglog.DogLogOptions;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.AimAtHub;
import frc.robot.commands.Hood;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.drive.TunerConstants;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.hood.HoodConstants;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.indexer.IndexerConstants;
import frc.robot.subsystems.indexer.IndexerSubsystem;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;

// import frc.robot.subsystems.climb.ClimbSubsystem;

@SuppressWarnings("unused")
public class RobotContainer {

  // ---------------- SUBSYSTEMS ----------------
  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();
  // private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  private final IndexerSubsystem indexerSubsystem = new IndexerSubsystem();
  private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem flywheelSubsystem = new FlywheelSubsystem();
  public final LEDSubsystem ledSubsystem = new LEDSubsystem();

  // ---------------- DRIVE CONFIG ----------------
  private final double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);

  private final double MaxAngularRate = RotationsPerSecond.of(0.45).in(RadiansPerSecond);

  private final SwerveRequest.FieldCentricFacingAngle faceAngle =
      new SwerveRequest.FieldCentricFacingAngle()
          .withDeadband(MaxSpeed * 0.1)
          .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

  private final SwerveRequest.FieldCentric drive =
      new SwerveRequest.FieldCentric()
          .withDeadband(MaxSpeed * 0.15)
          .withRotationalDeadband(MaxAngularRate * 0.15)
          .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

  // ---------------- CONTROLLERS ----------------
  private final CommandXboxController driverXbox = new CommandXboxController(0);
  private final CommandXboxController gamepadManipulator = new CommandXboxController(1);

  // ---------------- SHOOTER STATE ----------------
  private final Debouncer shooterReadyDebouncer = new Debouncer(0.5);

  private double lastVisionTy = 0.0;
  private boolean hasLockedVisionTarget = false;
  private static final double TY_UPDATE_THRESHOLD = 0.2;

  // ---------------- AUTO ----------------
  private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    configureBindings();
    registerNamedCommands();

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);

    DogLog.setOptions(new DogLogOptions().withCaptureNt(true).withCaptureDs(true).withNtPublish(true));
  }

  private void registerNamedCommands() {
    // Register ALL named commands before building the auto chooser
    // so PathPlanner can resolve them correctly.

    // NamedCommands.registerCommand("climb", new Climb(climbSubsystem));

    NamedCommands.registerCommand(
        "StartFlywheel",
        Commands.runOnce(() -> flywheelSubsystem.runSelectedVelocity(), flywheelSubsystem));

    NamedCommands.registerCommand(
        "StopFlywheel",
        Commands.runOnce(() -> flywheelSubsystem.stopFlywheel(), flywheelSubsystem));

    NamedCommands.registerCommand(
        "Shoot",
        Commands.run(
                    () -> {
                      boolean hasTarget = visionSubsystem.targetValid();

                      if (hasTarget) {
                        double ty = visionSubsystem.getTy();

                        if (!hasLockedVisionTarget
                            || Math.abs(ty - lastVisionTy) > TY_UPDATE_THRESHOLD) {
                          lastVisionTy = ty;
                          hasLockedVisionTarget = true;

                          double hoodTarget = hoodSubsystem.computeHoodSetpointFromTY(lastVisionTy);
                          hoodSubsystem.setVisionSetPoint(hoodTarget);
                          hoodSubsystem.setPoint(hoodTarget);

                          double rpm = flywheelSubsystem.computeFlywheelRPMFromTY(lastVisionTy);
                          flywheelSubsystem.setFlywheelVelocity(rpm / 60.0);
                        }
                      } else {
                        hasLockedVisionTarget = false;

                        // Fallback/default shot if no valid AprilTag is seen
                        hoodSubsystem.setPoint(HoodConstants.DEFAULT_POSITION);
                        flywheelSubsystem.setFlywheelVelocity(
                            flywheelSubsystem.getDefaultVelocity());
                      }

                      boolean linedUp = hasTarget && Math.abs(visionSubsystem.getTx()) < 3.0;

                    //   SmartDashboard.putBoolean("Has Target", hasTarget);
                    //   SmartDashboard.putBoolean("Lined Up", linedUp);
                    //   SmartDashboard.putBoolean("Hood At SetPoint", hoodSubsystem.isAtSetPoint());
                    //   SmartDashboard.putBoolean(
                    //       "Flywheel At Target Speed", flywheelSubsystem.isAtTargetSpeed());

                    //   SmartDashboard.putNumber("TX", visionSubsystem.getTx());
                    //   SmartDashboard.putNumber("TY", visionSubsystem.getTy());
                    //   SmartDashboard.putNumber("Locked TY", lastVisionTy);
                    //   SmartDashboard.putNumber(
                    //       "Hood Position", hoodSubsystem.getContinuousHoodEncoder());
                    //   SmartDashboard.putNumber("Hood SetPoint", hoodSubsystem.getSetPoint());
                    //   SmartDashboard.putNumber(
                    //       "Flywheel Current RPS", flywheelSubsystem.getCurrentVelocity());
                    //   SmartDashboard.putNumber(
                    //       "Flywheel Target RPS", flywheelSubsystem.getTargetVelocity());

                      boolean visionReady =
                          hasTarget
                              && linedUp
                              && hoodSubsystem.isAtSetPoint()
                              && flywheelSubsystem.isAtTargetSpeed();

                      boolean fallbackReady =
                          !hasTarget
                              && hoodSubsystem.isAtSetPoint()
                              && flywheelSubsystem.isAtTargetSpeed();

                      boolean rawShooterReady = visionReady || fallbackReady;
                      boolean debouncedShooterReady =
                          shooterReadyDebouncer.calculate(rawShooterReady);

                    //   SmartDashboard.putBoolean("Vision Ready", visionReady);
                    //   SmartDashboard.putBoolean("Fallback Ready", fallbackReady);
                    //   SmartDashboard.putBoolean("Raw Shooter Ready", rawShooterReady);
                    //   SmartDashboard.putBoolean("Debounced Shooter Ready", debouncedShooterReady);

                      indexerSubsystem.setShooting(true);
                      indexerSubsystem.setShooterReady(debouncedShooterReady);

                      if (debouncedShooterReady && visionReady) {
                        intakeSubsystem.run(IntakeConstants.ROLLER_IN_SPEED);
                        LEDSubsystem.setLEDColor(
                            new RGBWColor(
                                LEDConstants.GREEN[0],
                                LEDConstants.GREEN[1],
                                LEDConstants.GREEN[2],
                                0),
                            false);
                        LEDSubsystem.setLEDAnimation("SingleFade", false);
                      } else if (debouncedShooterReady && fallbackReady) {
                        LEDSubsystem.setLEDColor(
                            new RGBWColor(
                                LEDConstants.RED[0], LEDConstants.RED[1], LEDConstants.RED[2], 0),
                            false);
                        LEDSubsystem.setLEDAnimation("SingleFade", false);
                      }
                    },
                    hoodSubsystem,
                    flywheelSubsystem,
                    indexerSubsystem)
                .finallyDo(
                    interrupted -> {
                      hoodSubsystem.stopHood();
                      flywheelSubsystem.setHasVisionTarget(false);
                        flywheelSubsystem.runSelectedVelocity();
                      indexerSubsystem.stopAll();
                      intakeSubsystem.stopIntake();
                      LEDSubsystem.setLEDAnimation("None", false);
                      hasLockedVisionTarget = false;
                    }));

    NamedCommands.registerCommand(
        "StopIndexer", Commands.runOnce(() -> indexerSubsystem.stopAll(), indexerSubsystem));

    NamedCommands.registerCommand(
        "AimAtHub",
        new AimAtHub(drivetrain, visionSubsystem, ledSubsystem, () -> 0, () -> 0).withTimeout(10));

    NamedCommands.registerCommand(
        "IntakeOut", 
        new InstantCommand(() -> intakeSubsystem.setPoint(IntakeConstants.OUT_POSITION), intakeSubsystem));

    NamedCommands.registerCommand(
        "ZeroHood",
         new Hood(hoodSubsystem, -HoodConstants.HOOD_SPEED, false).withTimeout(1));
   
    NamedCommands.registerCommand(
        "StartIntake",
        Commands.runOnce(() -> intakeSubsystem.TestingIntakeMotor(0.9), intakeSubsystem));
    
    NamedCommands.registerCommand(
        "StopIntake",
        Commands.runOnce(() -> intakeSubsystem.TestingIntakeMotor(0.0), intakeSubsystem));
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }

  public void setFlywheelToIdle() {
    flywheelSubsystem.setHasVisionTarget(false);
    flywheelSubsystem.runSelectedVelocity();
  }

  private void configureBindings() {
    // ---------------- DEFAULT DRIVE COMMAND ----------------
    drivetrain.setDefaultCommand(
        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(-driverXbox.getLeftY() * MaxSpeed)
                    .withVelocityY(-driverXbox.getLeftX() * MaxSpeed)
                    .withRotationalRate(-driverXbox.getRightX() * MaxAngularRate)));

    // Reset field-centric heading
    driverXbox.y().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

    // ---------------- SHOOTER / VISION ----------------
    driverXbox
        .rightTrigger()
        .whileTrue(
            Commands.run(
                    () -> {
                      boolean hasTarget = visionSubsystem.targetValid();

                      if (hasTarget) {
                        double ty = visionSubsystem.getTy();

                        if (!hasLockedVisionTarget
                            || Math.abs(ty - lastVisionTy) > TY_UPDATE_THRESHOLD) {
                          lastVisionTy = ty;
                          hasLockedVisionTarget = true;

                          double hoodTarget = hoodSubsystem.computeHoodSetpointFromTY(lastVisionTy);
                          hoodSubsystem.setVisionSetPoint(hoodTarget);
                          hoodSubsystem.setPoint(hoodTarget);

                          double rpm = flywheelSubsystem.computeFlywheelRPMFromTY(lastVisionTy);
                          flywheelSubsystem.setFlywheelVelocity(rpm / 60.0);
                        }
                      } else {
                        hasLockedVisionTarget = false;

                        // Fallback/default shot if no valid AprilTag is seen
                        hoodSubsystem.setPoint(HoodConstants.DEFAULT_POSITION);
                        flywheelSubsystem.setFlywheelVelocity(
                            flywheelSubsystem.getDefaultVelocity());
                      }

                      boolean linedUp = hasTarget && Math.abs(visionSubsystem.getTx()) < 3.0;

                    //   SmartDashboard.putBoolean("Has Target", hasTarget);
                    //   SmartDashboard.putBoolean("Lined Up", linedUp);
                    //   SmartDashboard.putBoolean("Hood At SetPoint", hoodSubsystem.isAtSetPoint());
                      SmartDashboard.putBoolean(
                          "Flywheel At Target Speed", flywheelSubsystem.isAtTargetSpeed());

                    //   SmartDashboard.putNumber("TX", visionSubsystem.getTx());
                    //   SmartDashboard.putNumber("TY", visionSubsystem.getTy());
                    //   SmartDashboard.putNumber("Locked TY", lastVisionTy);
                    //   SmartDashboard.putNumber(
                    //       "Hood Position", hoodSubsystem.getContinuousHoodEncoder());
                    //   SmartDashboard.putNumber("Hood SetPoint", hoodSubsystem.getSetPoint());
                    //   SmartDashboard.putNumber(
                    //       "Flywheel Current RPS", flywheelSubsystem.getCurrentVelocity());
                    //   SmartDashboard.putNumber(
                    //       "Flywheel Target RPS", flywheelSubsystem.getTargetVelocity());

                      boolean visionReady =
                          hasTarget
                              && linedUp
                              && hoodSubsystem.isAtSetPoint()
                              && flywheelSubsystem.isAtTargetSpeed();

                      boolean fallbackReady =
                          !hasTarget
                              && hoodSubsystem.isAtSetPoint()
                              && flywheelSubsystem.isAtTargetSpeed();

                      boolean rawShooterReady = visionReady || fallbackReady;
                      boolean debouncedShooterReady =
                          shooterReadyDebouncer.calculate(rawShooterReady);

                    //   SmartDashboard.putBoolean("Vision Ready", visionReady);
                    //   SmartDashboard.putBoolean("Fallback Ready", fallbackReady);
                    //   SmartDashboard.putBoolean("Raw Shooter Ready", rawShooterReady);
                    //   SmartDashboard.putBoolean("Debounced Shooter Ready", debouncedShooterReady);

                      indexerSubsystem.setShooting(true);
                      indexerSubsystem.setShooterReady(debouncedShooterReady);

                      if (debouncedShooterReady && visionReady) {
                        //intakeSubsystem.run(IntakeConstants.ROLLER_IN_SPEED);
                        LEDSubsystem.setLEDColor(
                            new RGBWColor(
                                LEDConstants.GREEN[0],
                                LEDConstants.GREEN[1],
                                LEDConstants.GREEN[2],
                                0),
                            false);
                        LEDSubsystem.setLEDAnimation("SingleFade", false);
                      } else if (debouncedShooterReady && fallbackReady) {
                        LEDSubsystem.setLEDColor(
                            new RGBWColor(
                                LEDConstants.RED[0], LEDConstants.RED[1], LEDConstants.RED[2], 0),
                            false);
                        LEDSubsystem.setLEDAnimation("SingleFade", false);
                      }
                    },
                    hoodSubsystem,
                    flywheelSubsystem,
                    indexerSubsystem)
                .finallyDo(
                    interrupted -> {
                      hoodSubsystem.stopHood();
                      flywheelSubsystem.setHasVisionTarget(false);
                        flywheelSubsystem.runSelectedVelocity();
                      indexerSubsystem.stopAll();
                    //   intakeSubsystem.stopIntake();
                      LEDSubsystem.setLEDAnimation("None", false);
                      hasLockedVisionTarget = false;
                    }));
    // ---------------- INTAKE CONTROLS ----------------

    // Intake in + index forward
    driverXbox
        .leftTrigger()
        .whileTrue(
            Commands.startEnd(
                () -> {
                  intakeSubsystem.TestingIntakeMotor(0.6);
                // indexerSubsystem.runBothForward();
        
                },
                () -> {
                  intakeSubsystem.TestingIntakeMotor(0.0);
                //   indexerSubsystem.stopAll();
                },
                intakeSubsystem
                // indexerSubsystem
                ));

    // Intake reverse + index forward
    driverXbox
        .leftBumper()
        .whileTrue(
            Commands.startEnd(
                () -> {
                  intakeSubsystem.TestingIntakeMotor(-0.5);
                  indexerSubsystem.runBothReverse();
                },
                () -> {
                  intakeSubsystem.TestingIntakeMotor(0.0);
                  indexerSubsystem.stopAll();
                },
                intakeSubsystem,
                indexerSubsystem));

    // Intake reverse only
    driverXbox
        .rightBumper()
        .whileTrue(
            Commands.startEnd(
                () -> intakeSubsystem.TestingIntakeMotor(-0.5),
                () -> intakeSubsystem.TestingIntakeMotor(0.0),
                intakeSubsystem));

    // Intake arm out
    driverXbox
        .x()
        .onTrue(
            new InstantCommand(
                () -> intakeSubsystem.setPoint(IntakeConstants.OUT_POSITION), intakeSubsystem));

    // Intake arm in
    driverXbox
        .b()
        .onTrue(
            new InstantCommand(
                () -> intakeSubsystem.setPoint(IntakeConstants.IN_POSITION), intakeSubsystem));

    // ---------------- HOOD CONTROLS ----------------

    // D-pad up = hood up while held
    driverXbox.povUp().whileTrue(new Hood(hoodSubsystem, HoodConstants.HOOD_SPEED, false));

    // D-pad down = hood down while held
    driverXbox.povDown().whileTrue(new Hood(hoodSubsystem, -HoodConstants.HOOD_SPEED, false));

    // Hold A to aim horizontally at hub
    driverXbox
        .a()
        .whileTrue(
            new AimAtHub(
                drivetrain,
                visionSubsystem,
                ledSubsystem,
                () -> -driverXbox.getLeftY(),
                () -> -driverXbox.getLeftX()));

    // ---------------- CLIMB CONTROLS ----------------
    // driverXbox.povRight().whileTrue(new Climb(climbSubsystem, 0.5));
    // driverXbox.povLeft().whileTrue(new Climb(climbSubsystem, -0.5));

    // ---------------- MANIPULATOR CONTROLS ----------------
    gamepadManipulator
        .y()
        .onTrue(
            Commands.runOnce(
                () -> flywheelSubsystem.adjustDashboardTargetVelocity(2.0), flywheelSubsystem));

    gamepadManipulator
        .a()
        .onTrue(
            Commands.runOnce(
                () -> flywheelSubsystem.adjustDashboardTargetVelocity(-2.0), flywheelSubsystem));
  }
}
