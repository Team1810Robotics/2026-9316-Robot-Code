package frc.robot.subsystems;
import com.revrobotics.spark.SparkMax;
import frc.robot.Constants.ClimbConstants;

public class ClimbSub {
        private final SparkMax motor1 = new SparkMax(ClimbConstants.motor1ID, null);



  public void Extend() {
    motor1.set(0.25);
  }


  public void Stop() {
    motor1.set(0.0);
  }

  public void Retract() {
    motor1.set(-0.25);
  }
}