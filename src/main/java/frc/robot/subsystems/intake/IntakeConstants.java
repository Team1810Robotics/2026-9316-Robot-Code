package frc.robot.subsystems.intake;

public class IntakeConstants {
  public static int INTAKE_MOTOR = 12;
  // main spinny motor for intake
  public static int INTAKE_MOTOR_L = 11;
  // left motor for raising and lowering the intake
  public static int INTAKE_MOTOR_R = 9;
  // right motor for raising and lowering the intake
  public static double kP = 0.1;
  public static double kI = 0.0;
  public static double kD = 0.0;
  // TODO: Tune PID values for intake level control
  public static double IN_POSITION = 0;
  public static double OUT_POSITION = 90;
  // TODO: tune set points :)
}
