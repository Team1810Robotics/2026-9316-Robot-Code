// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.AimAtHub;
// --COMMANDS--
import frc.robot.commands.Flywheel;
import frc.robot.commands.Hood;
import frc.robot.commands.Indexer;
import frc.robot.commands.Intake;
import frc.robot.commands.FlywheelTune;
// --SUBSYTEM--
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.drive.TunerConstants;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.hood.HoodConstants;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.indexer.IndexerConstants;
import frc.robot.subsystems.indexer.IndexerSubsystem;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;

@SuppressWarnings("unused")
public class RobotContainer {

  // The robot's subsystems and commands are defined here...
  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    private final VisionSubsystem visionSubsystem = new VisionSubsystem();
  // private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
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
NamedCommands.registerCommand("Flywheel", new Flywheel(flywheelSubsystem, indexerSubsystem, 67.0));
NamedCommands.registerCommand("StartFlywheel", new Flywheel(flywheelSubsystem, indexerSubsystem, 200));
NamedCommands.registerCommand("StopFlywheel", new Flywheel(flywheelSubsystem, indexerSubsystem, 0));
    // NamedCommands.registerCommand("StartIntake", new Intake(intakeSubsystem, 1, RunType.Intake));
    // NamedCommands.registerCommand("StopIntake", new Intake(intakeSubsystem, 0, RunType.Intake));
    // NamedCommands.registerCommand("StartIndexer", new Indexer(indexerSubsystem));
    // NamedCommands.registerCommand("StopIndexer", new Indexer(indexerSubsystem));
  }

  private void configureBindings() {
    // Default drivetrain command
    driverXbox.back().onTrue(Commands.runOnce(() -> hoodSubsystem.zeroContinuousHoodEncoder(), hoodSubsystem));

    drivetrain.setDefaultCommand(
        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(-driverXbox.getLeftY() * MaxSpeed)
                    .withVelocityY(-driverXbox.getLeftX() * MaxSpeed)
                    .withRotationalRate(-driverXbox.getRightX() * MaxAngularRate)));

    // ---------------- DRIVER CONTROLS ----------------
    // B = point wheels in joystick direction
    // driverXbox
    //     .b()
    //     .whileTrue(
    //         drivetrain.applyRequest(
    //             () ->
    //                 point.withModuleDirection(
    //                     new Rotation2d(-driverXbox.getLeftY(), -driverXbox.getLeftX()))));

    // X = face target / hub while driving
    // driverXbox
    //     .back()
    //     .whileTrue(
    //         drivetrain.applyRequest(
    //             () ->
    //                 faceAngle
    //                     .withVelocityX(-driverXbox.getLeftY() * MaxSpeed)
    //                     .withVelocityY(-driverXbox.getLeftX() * MaxSpeed)
    //                     .withTargetDirection(drivetrain.getAngleToHub())
    //                     .withHeadingPID(5, 0, 0)));

    // Left bumper = reset field-centric heading
    driverXbox.start().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

    // Right bumper = run flywheel using velocity control / fallback target
    driverXbox.rightTrigger().whileTrue(new FlywheelTune(flywheelSubsystem, indexerSubsystem));

    // ---------------- INTAKE CONTROLS ----------------

    // Right trigger = intake wheels forward
    // Right trigger = intake wheels forward + indexer forward
driverXbox.leftTrigger().whileTrue(
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
driverXbox.leftBumper().whileTrue(
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
    driverXbox.rightBumper().whileTrue(
        Commands.startEnd(
            () -> intakeSubsystem.TestingIntakeMotor(-0.5),
            () -> intakeSubsystem.TestingIntakeMotor(0.0),
            intakeSubsystem));

    // Intake arm out
    driverXbox.x().onTrue(
        new InstantCommand(
            () -> intakeSubsystem.setPoint(IntakeConstants.OUT_POSITION), intakeSubsystem));

    // Intake arm in
    driverXbox.b().onTrue(
        new InstantCommand(
            () -> intakeSubsystem.setPoint(IntakeConstants.IN_POSITION), intakeSubsystem));

    // ---------------- HOOD CONTROLS ----------------

// Manipulator Y = hood up while held
driverXbox.povUp().whileTrue(new Hood(hoodSubsystem, HoodConstants.HOOD_SPEED, false));

// Manipulator A = hood down while held
driverXbox.povDown().whileTrue(new Hood(hoodSubsystem, -HoodConstants.HOOD_SPEED, false));
driverXbox.a().whileTrue((new AimAtHub(drivetrain, visionSubsystem, () -> -driverXbox.getLeftY(), () -> -driverXbox.getLeftX())));
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

    driverXbox
        .povLeft()
        .onTrue(ledSubsystem.runOnce(() -> LEDSubsystem.setLEDColor(null, true)));

    // ---------------- SYSID ----------------

    driverXbox.back().and(driverXbox.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
    driverXbox.back().and(driverXbox.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
    driverXbox
        .start()
        .and(driverXbox.y())
        .whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
    driverXbox
        .start()
        .and(driverXbox.x())
        .whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

    gamepadManipulator.y().onTrue(
    Commands.runOnce(() -> flywheelSubsystem.adjustDashboardTargetVelocity(2.0), flywheelSubsystem));

    gamepadManipulator.a().onTrue(
    Commands.runOnce(() -> flywheelSubsystem.adjustDashboardTargetVelocity(-2.0), flywheelSubsystem));
  }
}