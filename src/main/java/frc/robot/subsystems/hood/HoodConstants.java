package frc.robot.subsystems.hood;

public final class HoodConstants {
  private HoodConstants() {}

  // Kraken / Talon driving the hood
  public static final int HOOD_MOTOR_ID = 15;

  // REV through-bore encoder DIO port
  public static final int HOOD_ENCODER_DIO = 1; // CHANGE: replace if needed

  // PID values for hood position control
  public static final double kP = 0.6;
  public static final double kI = 0.0;
  public static final double kD = 0.0;

  // Clamp motor output while tuning so the hood does not slam
  public static final double MAX_HOOD_OUTPUT = 0.25;

  // How close is "good enough"
  public static final double HOOD_TOLERANCE = 0.03;

  // Manual jog speed for D-pad up/down
  public static final double HOOD_MANUAL_JOG_SPEED = 0.15;

  // Placeholder preset positions in continuous hood-position units
  // TODO: replace with real measured values from the robot
  public static final double HOOD_CLOSE_POSITION = 0.35;
  public static final double HOOD_MID_POSITION = 1.20;
  public static final double HOOD_FAR_POSITION = 2.10;
}