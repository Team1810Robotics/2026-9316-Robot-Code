package frc.robot.subsystems;
import com.revrobotics.spark.SparkMax;
import frc.robot.Constants.ClimbConstants;
//import com.revrobotics.spark.SparkLowLevel.MotorType;
public class ClimbSub {
        private final SparkMax motor2 = new SparkMax(ClimbConstants.motor2ID, null);
        private final SparkMax motor1 = new SparkMax(ClimbConstants.motor1ID, null);

    //set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
    //new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  public void Extend() {
    motor1.set(0.25);
    motor2.set(0.25);
  }


  public void Stop() {
    motor1.set(0.0);
    motor2.set(0.0);
  }

  public void Retract() {
    motor1.set(-0.25);
    motor2.set(-0.25);
  }
}
