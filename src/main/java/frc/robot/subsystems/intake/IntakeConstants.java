package frc.robot.subsystems.intake;

public class IntakeConstants {
  public static final int INTAKE_MOTOR = 12;
  // main spinny motor for intake
  public static final int INTAKE_MOTOR_L = 10;
  // left motor for raising and lowering the intake
  public static final int INTAKE_MOTOR_R = 11;
  // right motor for raising and lowering the intake
  public static final int INTAKE_ENCODER_DIO = 2;
  // DIO port for the intake encoder, which measures the angle of the intake

  // TODO: Tune PID values for intake level control
  public static final double kP = 1.6;
  public static final double kI = 0.0;
  public static final double kD = 0.0;

  public static final double MAX_PIVOT_OUTPUT =
      0.30; // Limit the maximum output to prevent damage to the mechanism
  public static final double PIVOT_TOLERANCE =
      0.01; // Tolerance for considering the intake to be at the setpoint

  public static final double ROLLER_IN_SPEED = 0.45; // Speed for intaking game pieces
  public static final double ROLLER_OUT_SPEED = -0.2; // Speed for ejecting game pieces
  public static double IN_POSITION =
      0.64; // Set point for the intake being fully retracted (arm up)
  public static double OUT_POSITION =
      0.21; // Set point for the intake being fully deployed (arm down)
  // TODO: tune set points :)
}
