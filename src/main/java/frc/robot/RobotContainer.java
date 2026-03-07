// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.Flywheel;
import frc.robot.commands.Hood;
import frc.robot.commands.Intake;
import frc.robot.commands.Intake.RunType;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.drive.TunerConstants;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.hood.HoodConstants;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;
import edu.wpi.first.wpilibj2.command.StartEndCommand;

import frc.robot.subsystems.indexer.IndexerSubsystem;

@SuppressWarnings("unused")
public class RobotContainer {

  // The robot's subsystems and commands are defined here...
  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
  //   private final VisionSubsystem visionSubsystem = new VisionSubsystem("limelight", drivetrain);
  // private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem flywheelSubsystem = new FlywheelSubsystem();

  private final IndexerSubsystem indexerSubsystem = new IndexerSubsystem();

  public final LEDSubsystem ledSubsystem = new LEDSubsystem();
//i <3 Danielle
  private double MaxSpeed =
      TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed

  private double MaxAngularRate =
      RotationsPerSecond.of(0.5)
          .in(RadiansPerSecond); // 1/2 of a rotation per second max angular velocity, slowed down for testing. Bump back to 0.75 or 1 for comp once we have the robot tuned and driving well.

  private final SwerveRequest.FieldCentricFacingAngle faceAngle =
      new SwerveRequest.FieldCentricFacingAngle()
          .withDeadband(MaxSpeed * 0.1)
          .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

  /* Setting up bindings for necessary control of the swerve drive platform */
  private final SwerveRequest.FieldCentric drive =
      new SwerveRequest.FieldCentric()
          .withDeadband(MaxSpeed * 0.1)
          .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
          .withDriveRequestType(
              DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors

  private final CommandXboxController driverXbox = new CommandXboxController(0);
  private final CommandXboxController gamepadManipulator = new CommandXboxController(1);

  //   private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    configureBindings();

    // autoChooser = AutoBuilder.buildAutoChooser();

    // NamedCommands.registerCommand("climb", new Climb(climbSubsystem));
    // NamedCommands.registerCommand(
    //     "Flywheel", new Flywheel(flywheelSubsystem, 67.0)); // Example: Spin flywheel to 100 RPS
    // NamedCommands.registerCommand("StartFlywheel", new Flywheel(flywheelSubsystem, 200));
    // NamedCommands.registerCommand("StopFlywheel", new Flywheel(flywheelSubsystem, 0));
    // NamedCommands.registerCommand(
    //   "StartIntake", new Intake(intakeSubsystem, 1, RunType.Intake)); // Fix speeds
    // NamedCommands.registerCommand("StopIntake", new Intake(intakeSubsystem, 0, RunType.Intake));
  }

  private void configureBindings() {
    // Note that X is defined as forward according to WPILib convention,
    // and Y is defined as to the left according to WPILib convention.
    drivetrain.setDefaultCommand(
        // Drivetrain will execute this command periodically
        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(
                        -driverXbox.getLeftY()
                            * MaxSpeed) // Drive forward with negative Y (forward)
                    .withVelocityY(
                        -driverXbox.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                    .withRotationalRate(
                        -driverXbox.getRightX()
                            * MaxAngularRate) // Drive counterclockwise with negative X (left)
            ));

   

    //driverXbox.rightBumper().whileTrue(flywheelSubsystem.setDutyCycleCommand(.5));

    driverXbox.a().whileTrue(
        new StartEndCommand(
            () -> intakeSubsystem.runUp(0.20), // manual pivot jog up at 20% speed
            () -> intakeSubsystem.stopIntakeLevel(),
            intakeSubsystem));

    driverXbox.b().whileTrue(
        new StartEndCommand(
            () -> intakeSubsystem.runDown(0.20), // manual pivot jog down at 20% speed
            () -> intakeSubsystem.stopIntakeLevel(),
            intakeSubsystem));

    driverXbox.rightTrigger().whileTrue(
        new StartEndCommand(
            () -> intakeSubsystem.runIntakeMotor(IntakeConstants.ROLLER_IN_SPEED), // run intake motor to intake game piece
            () -> intakeSubsystem.stopIntake(),
            intakeSubsystem));

    driverXbox.leftTrigger().whileTrue(
        new StartEndCommand(
            () -> intakeSubsystem.runIntakeMotor(IntakeConstants.ROLLER_OUT_SPEED), // run intake motor in reverse to eject game piece
            () -> intakeSubsystem.stopIntake(),
            intakeSubsystem));

    //INDEXER CONTROLS ON MANIPULATOR CONTROLLER
    //
    // A = lower indexer forward
    // Back + A = lower indexer reverse
    //
    // B = upper indexer forward
    // Back + B = upper indexer reverse
    //
    // Right bumper = both forward
    // Back + right bumper = both reverse

    gamepadManipulator.a().whileTrue(
        new StartEndCommand(
            () -> {
              if (gamepadManipulator.getHID().getBackButton()) {
                indexerSubsystem.runLowerReverse();
              } else {
                indexerSubsystem.runLowerForward();
              }
            },
            () -> indexerSubsystem.stopLower(),
            indexerSubsystem));

    gamepadManipulator.b().whileTrue(
        new StartEndCommand(
            () -> {
              if (gamepadManipulator.getHID().getBackButton()) {
                indexerSubsystem.runUpperReverse();
              } else {
                indexerSubsystem.runUpperForward();
              }
            },
            () -> indexerSubsystem.stopUpper(),
            indexerSubsystem));

    gamepadManipulator.rightBumper().whileTrue(
        new StartEndCommand(
            () -> {
              if (gamepadManipulator.getHID().getBackButton()) {
                indexerSubsystem.runBothReverse();
              } else {
                indexerSubsystem.runBothForward();
              }
            },
            () -> indexerSubsystem.stopAll(),
            indexerSubsystem));

    // B: Deploy intake out (arm to OUT_POSITION)
    // driverXbox
    //     .b()
    //     .onTrue(new InstantCommand(() -> intakeSubsystem.setPoint(IntakeConstants.OUT_POSITION)));

    // A: Retract intake in (arm to IN_POSITION)
    // driverXbox
    //     .a()
    //     .onTrue(new InstantCommand(() -> intakeSubsystem.setPoint(IntakeConstants.IN_POSITION)));
    // D-Pad Down: Intake eject (reverse wheels)
    // driverXbox.povDown().whileTrue(new Intake(intakeSubsystem, -1, Intake.RunType.UseManual));
    // driverXbox
    //     .x()
    //     .whileTrue(
    //         drivetrain.applyRequest(
    //             () ->
    //                 faceAngle
    //                     .withVelocityX(-driverXbox.getLeftY() * MaxSpeed)
    //                     .withVelocityY(-driverXbox.getLeftX() * MaxSpeed)
    //                     .withTargetDirection(drivetrain.getAngleToHub())
    //                     .withHeadingPID(5, 0, 0)));

    // Run SysId routines when holding back/start and X/Y.
    // Note that each routine should be run exactly once in a single log.
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

    // reset the field-centric heading on left bumper press
    driverXbox.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

    driverXbox.povUp().whileTrue(
        new StartEndCommand(
            () -> hoodSubsystem.startManualJogUp(HoodConstants.HOOD_MANUAL_JOG_SPEED), // manual jog up at defined speed
            () -> hoodSubsystem.stopManualJog(), // stop manual jog on button release
            hoodSubsystem));

    driverXbox.povDown().whileTrue(
        new StartEndCommand(
            () -> hoodSubsystem.startManualJogDown(HoodConstants.HOOD_MANUAL_JOG_SPEED), // manual jog down at defined speed
            () -> hoodSubsystem.stopManualJog(), // stop manual jog on button release
            hoodSubsystem));

    gamepadManipulator.x().onTrue(
        new InstantCommand(() -> hoodSubsystem.goToCloseShot(), hoodSubsystem));
        
    gamepadManipulator.y().onTrue(
        new InstantCommand(() -> hoodSubsystem.goToMidShot(), hoodSubsystem));

    gamepadManipulator.leftBumper().onTrue(
        new InstantCommand(() -> hoodSubsystem.goToFarShot(), hoodSubsystem));

    gamepadManipulator.rightTrigger().onTrue(
    new InstantCommand(() -> flywheelSubsystem.startFlywheel(), flywheelSubsystem));

    gamepadManipulator.leftTrigger().onTrue(    
    new InstantCommand(() -> flywheelSubsystem.stopFlywheel(), flywheelSubsystem));  


    //     // Cycle
    //     driverXbox
    //         .povRight()
    //         .onTrue(ledSubsystem.runOnce(() -> ledSubsystem.setLEDAnimation(null, true)));
    //     driverXbox.povLeft().onTrue(ledSubsystem.runOnce(() -> ledSubsystem.setLEDColor(null, true)));

    //     driverXbox
    //         .rightTrigger()
    //         .onTrue(
    //             ledSubsystem.runOnce(
    //                 () -> ledSubsystem.setLEDColor(new RGBWColor(0, 255, 0, 0), false))); // Green
    //     driverXbox
    //         .leftTrigger()
    //         .onTrue(
    //             ledSubsystem.runOnce(() -> ledSubsystem.setLEDAnimation("Rainbow", false))); // Rainbow
    //     driverXbox
    //         .povDown()
    //         .onTrue(
    //             ledSubsystem.runOnce(() -> ledSubsystem.setLEDAnimation("None", false))); // None (off)
  }

  public Command getAutonomousCommand() {
    // return autoChooser.getSelected();
    return Commands.print("Payton is love. Payton is life.");
  }
}