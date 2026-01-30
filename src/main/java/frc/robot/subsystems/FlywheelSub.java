package frc.robot.subsystems;


import com.ctre.phoenix6.hardware.TalonFX;
public class FlywheelSub {
        private final TalonFX m_motor = new TalonFX(0);

    //set_parameter(hb, "FlywheelSub", "frc.robot.subsystems.FlywheelSub");
    //new.motorcontrol = "edu.wpi.first.wpilibj.motorcontrol.Kraken";

  public void runFlywheel() {
    m_motor.set(1.0);
  }



  public void stopThrowing() {
    m_motor.set(-0.125);
  }
} 