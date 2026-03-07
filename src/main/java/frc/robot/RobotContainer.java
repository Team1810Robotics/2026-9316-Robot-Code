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

@SuppressWarnings("unused")
public class RobotContainer {

  // The robot's subsystems and commands are defined here...
  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
//   private final VisionSubsystem visionSubsystem = new VisionSubsystem("limelight", drivetrain);
  // private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem flywheelSubsystem = new FlywheelSubsystem();
  public final LEDSubsystem ledSubsystem = new LEDSubsystem();

  private double MaxSpeed =
      TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed

  private double MaxAngularRate =
      RotationsPerSecond.of(0.75)
          .in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

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
        "Flywheel", new Flywheel(flywheelSubsystem, 67.0)); // Example: Spin flywheel to 100 RPS
    NamedCommands.registerCommand("StartFlywheel", new Flywheel(flywheelSubsystem, 200));
    NamedCommands.registerCommand("StopFlywheel", new Flywheel(flywheelSubsystem, 0));
    // NamedCommands.registerCommand("StartIntake", new Intake(intakeSubsystem, 1, RunType.Intake)); // Fix speeds
    // NamedCommands.registerCommand("StopIntake", new Intake(intakeSubsystem, 0, RunType.Intake));
    // NamedCommands.registerCommand("StartIndexer", new Indexer(indexerSubsystem));
    // NamedCommands.registerCommand("StopIndexer", new Indexer(indexerSubsystem));
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

    // levels the intake up and down
    gamepadManipulator.y().onTrue(new Hood(hoodSubsystem, HoodConstants.HOOD_SPEED, false));
    //TODO: no manipulator 

    driverXbox.b().onTrue(new Hood(hoodSubsystem, 1, true));

    driverXbox.a().whileTrue(drivetrain.applyRequest(() -> brake));
    driverXbox
        .b()
        .whileTrue(
            drivetrain.applyRequest(
                () ->
                    point.withModuleDirection(
                        new Rotation2d(-driverXbox.getLeftY(), -driverXbox.getLeftX()))));

    driverXbox.rightBumper().whileTrue(flywheelSubsystem.setDutyCycleCommand(.75));

    // B: Deploy intake out (arm to OUT_POSITION)
    // driverXbox
    //     .b()
    //     .onTrue(new InstantCommand(() -> intakeSubsystem.setPoint(IntakeConstants.OUT_POSITION)));

    // A: Retract intake in (arm to IN_POSITION)
    //driverXbox.a().onTrue(new InstantCommand(() -> IntakeSubsystem.setPoint(IntakeConstants.IN_POSITION)));
    //D-Pad Down: Intake eject (reverse wheels)
    //driverXbox.leftTrigger().whileTrue(new Intake(intakeSubsystem, -1, Intake.RunType.UseManual));
    //driverXbox.rightTrigger().whileTrue(new Intake(intakeSubsystem, 1, Intake.RunType.UseManual));
    driverXbox.rightTrigger().whileTrue(new InstantCommand(() -> intakeSubsystem.TestingIntakeMotor(0.5)));
    driverXbox.leftTrigger().whileTrue(new InstantCommand(() -> intakeSubsystem.runDOWN(-.5)));
    driverXbox
        .x()
        .whileTrue(
            drivetrain.applyRequest(
                () ->
                    faceAngle
                        .withVelocityX(-driverXbox.getLeftY() * MaxSpeed)
                        .withVelocityY(-driverXbox.getLeftX() * MaxSpeed)
                        .withTargetDirection(drivetrain.getAngleToHub())
                        .withHeadingPID(5, 0, 0)));

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

    // Cycle
    driverXbox
        .povRight()
        .onTrue(ledSubsystem.runOnce(() -> ledSubsystem.setLEDAnimation(null, true)));
    driverXbox.povLeft().onTrue(ledSubsystem.runOnce(() -> ledSubsystem.setLEDColor(null, true)));

    // driverXbox
    //     .rightTrigger()
    //     .onTrue(
    //         ledSubsystem.runOnce(
    //             () -> ledSubsystem.setLEDColor(new RGBWColor(0, 255, 0, 0), false))); // Green
    // driverXbox
    //     .leftTrigger()
    //     .onTrue(
    //         ledSubsystem.runOnce(() -> ledSubsystem.setLEDAnimation("Rainbow", false))); // Rainbow
    // driverXbox
    //     .povDown()
    //     .onTrue(
    //         ledSubsystem.runOnce(() -> ledSubsystem.setLEDAnimation("None", false))); // None (off)

  }

  public Command getAutonomousCommand() {
    // return autoChooser.getSelected();
    return Commands.print("Payton is love. Payton is life.");
  }
}
