package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;


import edu.wpi.first.wpilibj2.command.SubsystemBase;



public class IntakeLevelSubsystem extends SubsystemBase {

    public SparkMax intakeMotorL;
    public SparkMax intakeMotorR;
   

    public boolean isIntakeDown;

    public IntakeLevelSubsystem() {
        this.intakeMotorL = new SparkMax(IntakeConstants.INTAKE_MOTOR_L, null);
        this.intakeMotorR = new SparkMax(IntakeConstants.INTAKE_MOTOR_R, null);

        this.isIntakeDown = false;
    }


    public void stop() {
        intakeMotorL.stopMotor();
        intakeMotorR.stopMotor();
    }

     public void runUP() {
        intakeMotorL.set(1);
        intakeMotorR.set(-1);
    }

    
    public void runDOWN() {
        intakeMotorL.set(-1);
        intakeMotorR.set(1);
    }


    

}