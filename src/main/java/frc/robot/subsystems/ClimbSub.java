package frc.robot.subsystems;


import com.ctre.phoenix6.hardware.TalonFX;
public class ClimbSub {
        private final TalonFX m_motor = new TalonFX(0);

    //set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
    //new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  public void Extend() {
    m_motor.set(0.25);
  }



  public void Retract() {
    m_motor.set(-0.25);
  }
}
