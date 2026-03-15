// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.AimAtHub;
import frc.robot.commands.Climb;
// --COMMANDS--
import frc.robot.commands.Flywheel;
// --SUBSYTEM--
import frc.robot.commands.Hood;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.drive.TunerConstants;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.hood.HoodConstants;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.indexer.IndexerSubsystem;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;
import frc.robot.subsystems.climb.ClimbSubsystem;

@SuppressWarnings("unused")
public class RobotContainer {

  // The robot's subsystems and commands are defined here...
  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();
  private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  private final IndexerSubsystem indexerSubsystem = new IndexerSubsystem();
  private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();

  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem flywheelSubsystem = new FlywheelSubsystem();
  public final LEDSubsystem ledSubsystem = new LEDSubsystem();

  private double MaxSpeed =
      TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed

  private double MaxAngularRate =
      RotationsPerSecond.of(0.45)
          .in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

  private final SwerveRequest.FieldCentricFacingAngle faceAngle =
      new SwerveRequest.FieldCentricFacingAngle()
          .withDeadband(MaxSpeed * 0.1)
          .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

  /* Setting up bindings for necessary control of the swerve drive platform */
  private final SwerveRequest.FieldCentric drive =
      new SwerveRequest.FieldCentric()
          .withDeadband(MaxSpeed * 0.15)
          .withRotationalDeadband(MaxAngularRate * 0.15) // Add a 10% deadband
          .withDriveRequestType(
              DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

  private final CommandXboxController driverXbox = new CommandXboxController(0);
  private final CommandXboxController gamepadManipulator = new CommandXboxController(1);

  //   private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    configureBindings();

    // autoChooser = AutoBuilder.buildAutoChooser();

    // NamedCommands.registerCommand("climb", new Climb(climbSubsystem));
    NamedCommands.registerCommand(
        "StartFlywheel", new Flywheel(flywheelSubsystem, indexerSubsystem, 200).withTimeout(5));
    NamedCommands.registerCommand(
        "StopFlywheel", new Flywheel(flywheelSubsystem, indexerSubsystem, 0).withTimeout(5));
    // NamedCommands.registerCommand("StartIntake", Commands.runOnce(() ->
    // intakeSubsystem.TestingIntakeMotor(0.5), intakeSubsystem));
    // NamedCommands.registerCommand("StopIntake", Commands.runOnce(() ->
    // intakeSubsystem.TestingIntakeMotor(0.0), intakeSubsystem));
    NamedCommands.registerCommand(
        "StartIndexer", Commands.runOnce(() -> indexerSubsystem.runBothForward()));
    NamedCommands.registerCommand(
        "StopIndexer", Commands.runOnce(() -> indexerSubsystem.stopAll()));
    NamedCommands.registerCommand(
        "AimAtHub", new AimAtHub(drivetrain, visionSubsystem, ledSubsystem, () -> 0, () -> 0));
  }

  private void configureBindings() {
    // Default drivetrain command
    // driverXbox
    //     .back()
    //     .onTrue(Commands.runOnce(() -> hoodSubsystem.zeroContinuousHoodEncoder(), hoodSubsystem));

    drivetrain.setDefaultCommand(
        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(-driverXbox.getLeftY() * MaxSpeed)
                    .withVelocityY(-driverXbox.getLeftX() * MaxSpeed)
                    .withRotationalRate(-driverXbox.getRightX() * MaxAngularRate)));



    // Left bumper = reset field-centric heading
    driverXbox.start().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));


// Right trigger = run vision-based hood + flywheel targeting, or fallback targets if no tag
driverXbox
    .rightTrigger()
    .whileTrue(
        Commands.run(
                () -> {
                  boolean hasTarget = visionSubsystem.targetValid();

                  if (hasTarget) {
                    double ty = visionSubsystem.getTy();

                    double hoodTarget = hoodSubsystem.computeHoodSetpointFromTY(ty);
                    hoodSubsystem.setVisionSetPoint(hoodTarget);
                    hoodSubsystem.setPoint(hoodTarget);

                    double rpm = flywheelSubsystem.computeFlywheelRPMFromTY(ty);
                    flywheelSubsystem.setFlywheelVelocity(rpm / 60.0);
                  } else {
                    // Fallback/default shot if no valid AprilTag is seen
                    hoodSubsystem.setPoint(HoodConstants.DEFAULT_POSITION);
                    flywheelSubsystem.setFlywheelVelocity(flywheelSubsystem.getDefaultVelocity());
                  }

                  boolean linedUp = hasTarget && Math.abs(visionSubsystem.getTx()) < 1.0;

                  boolean visionReady =
                      hasTarget
                          && linedUp
                          && hoodSubsystem.isAtSetPoint()
                          && flywheelSubsystem.isAtTargetSpeed();

                  boolean fallbackReady =
                      !hasTarget
                          && hoodSubsystem.isAtSetPoint()
                          && flywheelSubsystem.isAtTargetSpeed();

                  if (visionReady) {
                    LEDSubsystem.setLEDColor(
                        new RGBWColor(
                            LEDConstants.GREEN[0],
                            LEDConstants.GREEN[1],
                            LEDConstants.GREEN[2],
                            0),
                        false);
                    LEDSubsystem.setLEDAnimation("SingleFade", false);
                  } else if (fallbackReady) {
                    LEDSubsystem.setLEDColor(
                        new RGBWColor(
                            LEDConstants.RED[0],
                            LEDConstants.RED[1],
                            LEDConstants.RED[2],
                            0),
                        false);
                    LEDSubsystem.setLEDAnimation("SingleFade", false);
                  }
                },
                hoodSubsystem,
                flywheelSubsystem)
            .finallyDo(
                interrupted -> {
                  hoodSubsystem.stopHood();
                  flywheelSubsystem.setFlywheelVelocity(0.0);
                  LEDSubsystem.setLEDAnimation("None", false);
                }));
   
   
                // ---------------- INTAKE CONTROLS ----------------

    // Left trigger = intake wheels pull in & index runs
    // Left bumper = intake wheels push out
    // 'x' intake goes out
    // 'b' intake stows
    driverXbox
        .leftTrigger()
        .whileTrue(
            Commands.startEnd(
                () -> {
                  intakeSubsystem.TestingIntakeMotor(0.5);
                  indexerSubsystem.runBothForward();
                },
                () -> {
                  intakeSubsystem.TestingIntakeMotor(0.0);
                  indexerSubsystem.stopAll();
                },
                intakeSubsystem,
                indexerSubsystem));

    // Left trigger = intake wheels reverse + indexer reverse
    driverXbox
        .leftBumper()
        .whileTrue(
            Commands.startEnd(
                () -> {
                  intakeSubsystem.TestingIntakeMotor(-0.5);
                  indexerSubsystem.runBothForward();
                },
                () -> {
                  intakeSubsystem.TestingIntakeMotor(0.0);
                  indexerSubsystem.stopAll();
                },
                intakeSubsystem,
                indexerSubsystem));

    // Left trigger = intake wheels reverse
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

    //  Dpad up = hood up while held
    driverXbox.povUp().whileTrue(new Hood(hoodSubsystem, HoodConstants.HOOD_SPEED, false));

    //  Dpad down = hood down while held
    driverXbox.povDown().whileTrue(new Hood(hoodSubsystem, -HoodConstants.HOOD_SPEED, false));
    
    // hold a to aim horizontally at hub
    driverXbox
        .a()
        .whileTrue(
            (new AimAtHub(
                drivetrain,
                visionSubsystem,
                ledSubsystem,
                () -> -driverXbox.getLeftY(),
                () -> -driverXbox.getLeftX())));
                
    // ---------------- INDEXER CONTROLS ----------------

    // // POV up = normal indexer run
    // driverXbox.povUp().whileTrue(
    //     Commands.startEnd(
    //         () -> indexerSubsystem.runBothForward(),
    //         () -> indexerSubsystem.stopAll(),
    //         indexerSubsystem));

    // driverXbox.povDown().whileTrue(
    //     Commands.startEnd(
    //         () -> indexerSubsystem.runBothReverse(),
    //         () -> indexerSubsystem.stopAll(),
    //         indexerSubsystem));

    // driverXbox
    //     .povRight()
    //     .onTrue(ledSubsystem.runOnce(() -> LEDSubsystem.setLEDAnimation(null, true)));

    //driverXbox.povLeft().onTrue(ledSubsystem.runOnce(() -> LEDSubsystem.setLEDColor(null, true)));

    // ---------------- SYSID ----------------

    // driverXbox.back().and(driverXbox.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
    // driverXbox.back().and(driverXbox.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
    // driverXbox
    //     .start()
    //     .and(driverXbox.y())
    //     .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
    // driverXbox
    //     .start()
    //     .and(driverXbox.x())
    //     .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));


    // ---------------- CLIMB CONTROLS ----------------

    // D-pad right = climb one direction while held
    driverXbox.povRight().whileTrue(new Climb(climbSubsystem, 0.5));

    // D-pad left = climb opposite direction while held
    driverXbox.povLeft().whileTrue(new Climb(climbSubsystem, -0.5));


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
