package frc.robot.commands;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.drive.TunerConstants;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;
import frc.robot.subsystems.vision.VisionConstants;
import frc.robot.subsystems.vision.VisionSubsystem;
import java.util.function.DoubleSupplier;

public class AimAtHub extends Command {
  private final CommandSwerveDrivetrain drivetrain;
  private final VisionSubsystem visionSubsystem;
  private final LEDSubsystem ledSubsystem;
  private final DoubleSupplier xSupplier;
  private final DoubleSupplier ySupplier;

  private final PIDController rotationPIDController;

  private final SwerveRequest.RobotCentric request = new SwerveRequest.RobotCentric();

  private final double maxTranlationalVelocity =
      TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
  private final double maxRotationalVelocity =
      RotationsPerSecond.of(.5).in(RadiansPerSecond);

  public AimAtHub(
      CommandSwerveDrivetrain drivetrain,
      VisionSubsystem visionSubsystem,
      LEDSubsystem ledSubsystem,
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier) {
    this.drivetrain = drivetrain;
    this.visionSubsystem = visionSubsystem;
    this.ledSubsystem = ledSubsystem;
    this.xSupplier = xSupplier;
    this.ySupplier = ySupplier;

    rotationPIDController =
        new PIDController(VisionConstants.kP, VisionConstants.kI, VisionConstants.kD);
  }

  @Override
  public void execute() {
    double tx = visionSubsystem.getTx();

    if (visionSubsystem.targetValid() && Math.abs(tx) < 1.0) {
      LEDSubsystem.setLEDColor(
          new RGBWColor(LEDConstants.GREEN[0], LEDConstants.GREEN[1], LEDConstants.GREEN[2], 0),
          false);
      LEDSubsystem.setLEDAnimation("None", false);
    }

    double rotationalRate = rotationPIDController.calculate(tx, 0);

    drivetrain.setControl(
        request
            .withVelocityX(xSupplier.getAsDouble() * maxTranlationalVelocity)
            .withVelocityY(ySupplier.getAsDouble() * maxTranlationalVelocity)
            .withRotationalRate(rotationalRate * maxRotationalVelocity));
  }
}