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
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.Climb;
import frc.robot.commands.Flywheel;
import frc.robot.commands.Intake;
import frc.robot.commands.LEDs;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.flywheel.FlywheelSubsystem;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;

@SuppressWarnings("unused")
public class RobotContainer {

  // The robot's subsystems and commands are defined here...

  private double MaxSpeed =
      TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed

  private double MaxAngularRate =
      RotationsPerSecond.of(0.75)
          .in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

  private final SwerveRequest.FieldCentricFacingAngle faceAngle =
      new SwerveRequest.FieldCentricFacingAngle()
          .withDeadband(MaxSpeed * 0.1)
          .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

  // public static Intake intake = new Intake(intakeSubsystem, intakeSubsystem.Mode.ON);
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

  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem("", drivetrain);
  private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem flywheelSubsystem = new FlywheelSubsystem();

  //   private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    configureBindings();

    // autoChooser = AutoBuilder.buildAutoChooser();

    NamedCommands.registerCommand("climb", new Climb(climbSubsystem));
    NamedCommands.registerCommand(
        "Flywheel", new Flywheel(flywheelSubsystem, 67.0)); // Example: Spin flywheel to 100 RPS
    NamedCommands.registerCommand("StartFlywheel", new Flywheel(flywheelSubsystem, 200));
    NamedCommands.registerCommand("StopFlywheel", new Flywheel(flywheelSubsystem, 0));
    // NamedCommands.registerCommand( "StartIntake", new Intake(intakeSubsystem,
    // IntakeConstants.Mode.ON));
    // NamedCommands.registerCommand("StopIntake", new Intake(intakeSubsystem,
    // IntakeConstants.Mode.STOP));
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
    // spins the flywheel to feed when the X button is held
    // driverXbox.x().whileTrue(FlywheelCommand());
    driverXbox.rightBumper().whileTrue(new Intake(intakeSubsystem, 1, false));
    // sucks ball in
    driverXbox.leftBumper().whileTrue(new Intake(intakeSubsystem, -1, false));
    // spits ball out
    driverXbox.x().onTrue(new Intake(intakeSubsystem, 1, true));

    // driverXbox.x().whileTrue(intakeLevelCommand());

    driverXbox.a().whileTrue(drivetrain.applyRequest(() -> brake));
    driverXbox
        .b()
        .whileTrue(
            drivetrain.applyRequest(
                () ->
                    point.withModuleDirection(
                        new Rotation2d(-driverXbox.getLeftY(), -driverXbox.getLeftX()))));

    /*hypthetically, if some random prankster were to change the button bind so it was some crazy button combo, for example,
     holding LB RT A B and right arrow, this would almost certainly be detrimental to our success in the competition. btw, the name of the
    button is  |
          here \/                                                       */
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
  }

  public Command getAutonomousCommand() {
    // return autoChooser.getSelected();
    return Commands.print("Payton is love. Payton is life.");
  }
}
