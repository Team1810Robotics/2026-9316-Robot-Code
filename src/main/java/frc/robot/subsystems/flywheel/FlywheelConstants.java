package frc.robot.subsystems.flywheel;

public final class FlywheelConstants {
  // Flywheel velocity targets (in rotations per second - RPS)
  public static final double TARGET_VELOCITY_RPS = 80.0;  // Target flywheel speed
  
  // Velocity control tolerance
  public static final double VELOCITY_TOLERANCE_RPS = 2.0;  // Within 2 RPS = at speed
  
  // Fallback power control (if velocity control unavailable)
  public static final double POWER_PERCENTAGE = 0.8;  // 80% power
}
