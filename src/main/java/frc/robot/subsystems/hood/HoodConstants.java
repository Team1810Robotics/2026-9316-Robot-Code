package frc.robot.subsystems.hood;

public final class HoodConstants {
  public static final int HOOD_MOTOR_ID = 15;
  public static final int HOOD_ENCODER_DIO = 1;

  // NEW: limit switch DIO port
  public static final int HOOD_LIMIT_SWITCH_DIO = 3;

  // NEW:
  // Set this to true if DigitalInput.get() returns true when the switch is physically pressed.
  // Set this to false if your switch is wired as normally-closed and returns false when pressed.
  public static final boolean HOOD_LIMIT_SWITCH_PRESSED_STATE = false;

  public static final int MINDEGREE_HOOD_ANGLE = 0;
  public static final int MAXDEGREE_HOOD_ANGLE = 30;

  // These now represent CONTINUOUS encoder positions, not raw 0-1 values.
  // Replace these with your real measured values from SmartDashboard.
  public static final double POSITION1 = 0.195;
  public static final double POSITION2 = 1.200;
  public static final double POSITION3 = 2.244;

  public static final double DEFAULT_POSITION = POSITION2;

  public static final double HOOD_SPEED = 0.25;

  // PID values - start conservative and tune on the robot
  public static final double kP = 0.8;
  public static final double kI = 0.0;
  public static final double kD = 0.0;

  // Max output used both for PID and manual mode clamping
  public static final double MAX_PID_OUTPUT = 0.35;

  // How close is "good enough"
  public static final double HOOD_TOLERANCE = 0.03;

  // Wrap detection threshold for a 0-1 absolute encoder
  public static final double ENCODER_WRAP_THRESHOLD = 0.5;
}
