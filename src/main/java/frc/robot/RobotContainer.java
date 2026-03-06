// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.Climb;
import frc.robot.commands.Flywheel;
import frc.robot.commands.Intake;
import frc.robot.commands.VisionLock;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.hood.HoodConstants;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;

@SuppressWarnings("unused")
public class RobotContainer {

  // The robot's subsystems and commands are defined here...
  private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();
  private final LEDSubsystem LEDSubsystem = new LEDSubsystem();
  private final FlywheelSubsystem flywheelSubsystem = new FlywheelSubsystem();
  private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  // VisionSubsystem requires limelight name and drivetrain
  private final VisionSubsystem visionSubsystem =
      new VisionSubsystem("limelight", TunerConstants.createDrivetrain());

  // Speed modes
  private double MaxSpeed =
      TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
  private double slowModeSpeed = MaxSpeed * 0.35; // 35% speed for slow mode

  private double MaxAngularRate =
      RotationsPerSecond.of(0.75)
          .in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

  public static Flywheel FlywheelCommand = new Flywheel();
  public static Climb ClimbCommand = new Climb();

  /* Setting up bindings for necessary control of the swerve drive platform */
  private final SwerveRequest.FieldCentric drive =
      new SwerveRequest.FieldCentric()
          .withDeadband(MaxSpeed * 0.1)
          .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
          .withDriveRequestType(
              DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

  private final Telemetry logger = new Telemetry(MaxSpeed);
  private final CommandXboxController driverXbox = new CommandXboxController(0);
  private final CommandXboxController manipulatorXbox = new CommandXboxController(1);

  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

  public RobotContainer() {
    // Initialize LED subsystem
    LEDSubsystem.StartLEDSubsystem();

    configureBindings();
  }

  private void configureBindings() {
    // Note that X is defined as forward according to WPILib convention,
    // and Y is defined as to the left according to WPILib convention.

    // Default drivetrain command - field centric drive
    // Uses left stick for translation, right stick X for rotation
    drivetrain.setDefaultCommand(
        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(-driverXbox.getLeftY() * MaxSpeed)
                    .withVelocityY(-driverXbox.getLeftX() * MaxSpeed)
                    .withRotationalRate(-driverXbox.getRightX() * MaxAngularRate)));

    // =====================================================
    // DRIVER CONTROLLER (Xbox Controller 0 - White)
    // =====================================================

    // Right Bumper: Reset field-centric heading
    driverXbox.rightBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

    // X (hold): Vision lock - PID rotation onto AprilTag, driver keeps translation
    driverXbox
        .x()
        .whileTrue(
            new VisionLock(
                drivetrain,
                visionSubsystem,
                () -> -driverXbox.getLeftY() * MaxSpeed,
                () -> -driverXbox.getLeftX() * MaxSpeed,
                MaxSpeed,
                MaxAngularRate));

    // B: Deploy intake out (arm to OUT_POSITION)
    driverXbox
        .b()
        .onTrue(new InstantCommand(() -> intakeSubsystem.setPoint(IntakeConstants.OUT_POSITION)));

    // A: Retract intake in (arm to IN_POSITION)
    driverXbox
        .a()
        .onTrue(new InstantCommand(() -> intakeSubsystem.setPoint(IntakeConstants.IN_POSITION)));

    // Left Trigger (hold): Intake IN + LEDs orange
    // Note: Left trigger value ranges from 0 to 1
    Trigger leftTrigger = driverXbox.leftTrigger(0.5);
    // TODO: this needs to be addressed
    // leftTrigger.whileTrue(new Intake(intakeSubsystem, 1, false));
    leftTrigger.onTrue(
        new InstantCommand(() -> LEDSubsystem.setLEDColor(new RGBWColor(255, 128, 0, 0), false)));

    // Right Trigger (hold): Full shoot sequence
    // flywheel + white rollers + wait + full indexer
    Trigger rightTrigger = driverXbox.rightTrigger(0.5);
    rightTrigger.whileTrue(new Flywheel());
    rightTrigger.onTrue(
        new InstantCommand(() -> LEDSubsystem.setLEDColor(new RGBWColor(255, 128, 0, 0), false)));
    rightTrigger.onFalse(
        new InstantCommand(
            () -> {
              // On release: stop flywheel, stop indexer, LEDs green
              flywheelSubsystem.stopThrowing();
              LEDSubsystem.setLEDColor(new RGBWColor(0, 255, 0, 0), false);
            }));

    // D-Pad Down: Intake eject (reverse wheels)
    driverXbox.povDown().whileTrue(new Intake(intakeSubsystem, -1, Intake.RunType.UseManual));

    // D-Pad Up: Intake 
    driverXbox.povUp().whileTrue(new Intake(intakeSubsystem, 1, Intake.RunType.UseManual));


    // D-Pad Right: Hood up (stops at upper hardware limit switch)
    // TODO: Address
    driverXbox
        .povRight()
        .whileTrue(
            new InstantCommand(
                () ->
                    hoodSubsystem.runUP(
                        HoodConstants.HOOD_SPEED))); // TODO: dunno if this speed is right

    // D-Pad Left: Hood down (stops at lower hardware limit switch)
    // TODO: Address
    driverXbox
        .povLeft()
        .whileTrue(new InstantCommand(() -> hoodSubsystem.runDOWN(HoodConstants.HOOD_SPEED)));

    // Back (small left button): Slow mode (~35% speed) + LEDs blue
    driverXbox
        .back()
        .onTrue(
            new InstantCommand(
                () -> {
                  MaxSpeed = slowModeSpeed;
                  LEDSubsystem.setLEDColor(new RGBWColor(0, 0, 255, 0), false);
                }));

    // Start (small right button): Fast mode (full speed) + LEDs green
    driverXbox
        .start()
        .onTrue(
            new InstantCommand(
                () -> {
                  MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
                  LEDSubsystem.setLEDColor(new RGBWColor(0, 255, 0, 0), false);
                }));

    // Y: Climb command (for testing/sysid)
    driverXbox.y().whileTrue(ClimbCommand);

    // Register telemetry
    drivetrain.registerTelemetry(logger::telemeterize);

    // =====================================================
    // MANIPULATOR CONTROLLER (Xbox Controller 1)
    // =====================================================

    // Right Trigger (hold): Climb up (Extend)
    manipulatorXbox.rightTrigger(0.5).whileTrue(new InstantCommand(() -> climbSubsystem.Extend()));

    // Left Trigger (hold): Climb down (Retract)
    manipulatorXbox.leftTrigger(0.5).whileTrue(new InstantCommand(() -> climbSubsystem.Retract()));

    // B: Cycle LED animation
    manipulatorXbox
        .b()
        .onTrue(
            new InstantCommand(() -> LEDSubsystem.setLEDColor(new RGBWColor(0, 0, 0, 0), true)));
  }

  public Command getAutonomousCommand() {
    return new InstantCommand();
  }
}

// idk
