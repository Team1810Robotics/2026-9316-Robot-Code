package frc.robot.commands;

import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.vision.VisionSubsystem;

/**
 * VisionLock Command While held (X button), locks robot rotation onto the AprilTag target using
 * Limelight TX. Driver retains full translational control with the left stick.
 */
public class VisionLock extends Command {

  private final CommandSwerveDrivetrain drivetrain;
  private final VisionSubsystem vision;

  // PID for rotation — tune kP first, then kD if oscillating
  private final PIDController rotPID = new PIDController(0.05, 0.0, 0.002);

  private final SwerveRequest.FieldCentric driveRequest = new SwerveRequest.FieldCentric();

  private final java.util.function.DoubleSupplier xSupplier;
  private final java.util.function.DoubleSupplier ySupplier;
  private final double maxAngularRate;

  /**
   * @param drivetrain The swerve drivetrain
   * @param vision The vision subsystem (Limelight)
   * @param xSupplier Forward/back velocity supplier (left stick Y)
   * @param ySupplier Left/right velocity supplier (left stick X)
   * @param maxSpeed Max translational speed (m/s)
   * @param maxAngularRate Max angular rate (rad/s) — used as fallback if no target
   */
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

    // Limelight TX ranges roughly -29 to +29 degrees; treat 0 as "on target"
    rotPID.setSetpoint(0.0);
    rotPID.setTolerance(1.0); // within 1 degree = close enough

    addRequirements(drivetrain);
  }

  @Override
  public void execute() {
    double rotationalRate = 0.0;

    if (vision.targetValid()) {
      // Get horizontal offset from target (degrees)
      double tx = frc.robot.util.LimelightHelpers.getTX("limelight");
      // PID output — negate because positive TX means target is to the right,
      // so we need to rotate right (negative in WPILib field-centric convention)
      rotationalRate = -rotPID.calculate(tx);
      // Clamp to max angular rate
      rotationalRate = Math.max(-maxAngularRate, Math.min(maxAngularRate, rotationalRate));
    }
    // If no target, rotational rate stays 0 — robot holds heading, driver steers translation

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
    return false; // runs until button released (whileTrue)
  }
}
