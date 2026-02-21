package frc.robot.subsystems.flywheel;

public final class FlywheelConstants {
  public static final int leftMotorID = 13;
  public static final int rightMotorID = 15;
  public static final int FlywheelBeamBreak = 5;
  public static final double IDLE_VELOCITY = 200.0; // Max velocity in rotations per second (RPS)

  //TODO TUNE THIS: PID constants for velocity control 
  public static final double kP = 0.0; // Proportional gain--current
  public static final double kI = 0.0; // Integral gain--past
  public static final double kD = 0.0; // Derivative gain--future
    
  }


 