// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import frc.robot.Constants.OperatorConstants;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.LEDsCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.LEDSubsystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
//import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
//import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.IntakeConstants.Mode;
import frc.robot.commands.Intake;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.commands.Climb;
import frc.robot.commands.Flywheel;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.commands.AimHood;

public class RobotContainer {

    // The robot's subsystems and commands are defined here...
    private static IntakeSubsystem intakeSubsystem;
    private static HoodSubsystem hoodSubsystem;
    private final LEDSubsystem m_LEDSubsystem = new LEDSubsystem();
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
    public static Flywheel Flywheel = new Flywheel();
    public static Climb Climb = new Climb();
    public static Intake intake = new Intake(intakeSubsystem, Mode.ON);
    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);
    private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
    private final CommandXboxController driverXbox = new CommandXboxController(0);
    private final CommandXboxController gamepadManipulator = new CommandXboxController(1);


    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    public RobotContainer() {


        configureBindings();
    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-driverXbox.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward)
                    .withVelocityY(-driverXbox.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                    .withRotationalRate(-driverXbox.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X (left)
            )
        );
            //spins the flywheel to feed when the X button is held
          driverXbox.x().whileTrue(
            FlywheelCommand()
            );
          driverXbox.y().whileTrue(
            ClimbCommand()
            );
        driverXbox.rightBumper().whileTrue(
            intakeCommand()
        );

        driverXbox.a().whileTrue(drivetrain.applyRequest(() -> brake));
        driverXbox.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-driverXbox.getLeftY(), -driverXbox.getLeftX()))
        ));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        driverXbox.back().and(driverXbox.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        driverXbox.back().and(driverXbox.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        driverXbox.start().and(driverXbox.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        driverXbox.start().and(driverXbox.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        driverXbox.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);

          // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

     gamepadManipulator.b().onTrue(new LEDsCommand(m_LEDSubsystem));
    }

    public Command getAutonomousCommand() {
 // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
    //makes the flywheel command
    public Command FlywheelCommand() {
    return new Flywheel();
    }
    public Command ClimbCommand() {
    return new Climb();
    }

    public Command intakeCommand() {
        if (intakeSubsystem.getMode() == Mode.OFF) {
            return new Intake(intakeSubsystem, Mode.ON);
        } else {
            return new Intake(intakeSubsystem, Mode.OFF);
        }
    }
    
}