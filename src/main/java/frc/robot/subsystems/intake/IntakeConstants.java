package frc.robot.subsystems.intake;

public class IntakeConstants {
  public static int INTAKE_MOTOR = 11;
  // main spinny motor for intake
  public static int INTAKE_MOTOR_L = 4; // TODO: Get actual IDs
  // left motor for raising and lowering the intake
  public static int INTAKE_MOTOR_R = 16; // TODO: Get actual IDs
  // right motor for raising and lowering the intake
  // Motor speeds
  public static final double SIDE_MOTOR_SPEED = 0.7; // Speed for side motors
  public static final double WHEEL_MOTOR_SPEED = 0.8; // Speed for wheel motor

  public enum Mode {
    ON,
    OFF,
    STOP
  }
}
