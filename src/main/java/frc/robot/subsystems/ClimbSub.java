package frc.robot.subsystems;
import com.revrobotics.spark.SparkMax;

//import com.revrobotics.spark.SparkLowLevel.MotorType;
public class ClimbSub {
        private final SparkMax m_motor = new SparkMax(0, null);

    //set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
    //new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  public void Extend() {
    m_motor.set(0.25);
  }


  public void Stop() {
    m_motor.set(0.0);
  }

  public void Retract() {
    m_motor.set(-0.25);
  }
}
