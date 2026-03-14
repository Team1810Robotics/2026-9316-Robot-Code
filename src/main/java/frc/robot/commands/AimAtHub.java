package frc.robot.commands;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.swerve.SwerveRequest;

import dev.doglog.DogLog;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.drive.TunerConstants;
import frc.robot.subsystems.vision.VisionConstants;
import frc.robot.subsystems.vision.VisionSubsystem;

public class AimAtHub extends Command {
    private final CommandSwerveDrivetrain drivetrain;
    private final VisionSubsystem visionSubsystem;

    private final DoubleSupplier xSupplier;
    private final DoubleSupplier ySupplier;

    private final PIDController rotationPIDController;

    private final SwerveRequest.RobotCentric request = new SwerveRequest.RobotCentric();

    private final double maxTranlationalVelocity = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
    private final double maxRotationalVelocity = RotationsPerSecond.of(.5).in(RadiansPerSecond);

    // private final DoubleSubscriber kP = DogLog.tunable("Vision/kP", 0.0);
    // private final DoubleSubscriber kI = DogLog.tunable("Vision/kI", 0.0);
    // private final DoubleSubscriber kD = DogLog.tunable("Vision/kD", 0.0);


    public AimAtHub(CommandSwerveDrivetrain drivetrain, VisionSubsystem visionSubsystem, DoubleSupplier xSupplier, DoubleSupplier ySupplier) {
        this.drivetrain = drivetrain;
        this.visionSubsystem = visionSubsystem;
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;

        rotationPIDController = new PIDController(VisionConstants.kP, VisionConstants.kI, VisionConstants.kD);
    }

    @Override
    public void execute() {
        double tx = visionSubsystem.getTx();

        double rotationalRate = rotationPIDController.calculate(tx, 0);

        drivetrain.setControl(
            request.withVelocityX(xSupplier.getAsDouble() * maxTranlationalVelocity)
            .withVelocityY(ySupplier.getAsDouble() * maxTranlationalVelocity).withRotationalRate(rotationalRate * maxRotationalVelocity)
        );
    }
    
}
