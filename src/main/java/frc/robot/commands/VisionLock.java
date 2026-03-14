package frc.robot.commands;

import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.vision.VisionSubsystem;

public class VisionLock extends Command {

  private final CommandSwerveDrivetrain drivetrain;
  private final VisionSubsystem vision;

  private final PIDController rotPID = new PIDController(0.05, 0.0, 0.002);
  private final SwerveRequest.FieldCentric driveRequest = new SwerveRequest.FieldCentric();

  private final java.util.function.DoubleSupplier xSupplier;
  private final java.util.function.DoubleSupplier ySupplier;
  private final double maxAngularRate;

  public VisionLock(
      CommandSwerveDrivetrain drivetrain,
      VisionSubsystem vision,
      java.util.function.DoubleSupplier xSupplier,
      java.util.function.DoubleSupplier ySupplier,
      double maxSpeed,
      double maxAngularRate) {
    this.drivetrain = drivetrain;
    this.vision = vision;
    this.xSupplier = xSupplier;
    this.ySupplier = ySupplier;
    this.maxAngularRate = maxAngularRate;

    rotPID.setSetpoint(0.0);
    rotPID.setTolerance(1.0);

    addRequirements(drivetrain);
  }

  @Override
  public void initialize() {
    rotPID.reset();
  }

  @Override
  public void execute() {
    double rotationalRate = 0.0;

    if (vision.targetValid()) {
      double tx = vision.getTx();

      if (Math.abs(tx) < 0.5) {
        rotationalRate = 0.0;
      } else {
        rotationalRate = -rotPID.calculate(tx);
      }

      rotationalRate = MathUtil.clamp(rotationalRate, -maxAngularRate, maxAngularRate);
    }

    drivetrain.setControl(
        driveRequest
            .withVelocityX(xSupplier.getAsDouble())
            .withVelocityY(ySupplier.getAsDouble())
            .withRotationalRate(rotationalRate));
  }

  @Override
  public void end(boolean interrupted) {
    rotPID.reset();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}