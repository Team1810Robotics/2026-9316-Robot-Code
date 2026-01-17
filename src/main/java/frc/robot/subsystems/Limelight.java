package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import com.revrobotics.REVLibError;
import com.revrobotics.*;


public class Limelight {

    private static final NetworkTable table =
        NetworkTableInstance.getDefault().getTable("limelight");

    public static boolean hasTarget() {
        return table.getEntry("tv").getDouble(0) == 1;
    }

    public static double getTX() {
        return table.getEntry("tx").getDouble(0);
    }

    public static int getTagID() {
        return (int) table.getEntry("tid").getDouble(-1);
    }
}
public class Limelight extends SubsystemBase {

    private final CANSparkMax turretMotor;
    private final RelativeEncoder encoder;

    private final PIDController pid = new PIDController(0.025, 0, 0);

    private static final double MIN_ANGLE = -90;
    private static final double MAX_ANGLE = 90;
    private static final double MAX_SPEED = 0.5;

    public void Turret() {
        turretMotor = new CANSparkMax(10, MotorType.kBrushless);
        encoder = turretMotor.getEncoder();
        encoder.setPosition(0);
    }

    public double getAngle() {
        return encoder.getPosition(); // degrees
    }

    public void rotate(double speed) {
        double angle = getAngle();

        if ((angle <= MIN_ANGLE && speed < 0) ||
            (angle >= MAX_ANGLE && speed > 0)) {
            turretMotor.set(0);
        } else {
            turretMotor.set(speed);
        }
    }

    public void stop() {
        turretMotor.set(0);
    }

    // ===============================
    // APRILTAG AUTO AIM (180°)
    // ===============================
    public void aimAtAprilTag() {
        if (!Limelight.hasTarget()) {
            stop();
            return;
        }

        double tx = Limelight.getTX();

        // Deadband to prevent shaking
        if (Math.abs(tx) < 0.4) {
            stop();
            return;
        }

        double currentAngle = getAngle();
        double targetAngle = currentAngle + tx;

        targetAngle = MathUtil.clamp(targetAngle, MIN_ANGLE, MAX_ANGLE);

        double output = pid.calculate(currentAngle, targetAngle);
        output = MathUtil.clamp(output, -MAX_SPEED, MAX_SPEED);

        rotate(output);
    }
}
