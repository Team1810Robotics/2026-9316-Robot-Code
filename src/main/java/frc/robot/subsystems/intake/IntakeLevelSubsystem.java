package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.IntakeConstants;


public class IntakeLevelSubsystem extends SubsystemBase {

    public SparkMax intakeMotorL;
    public SparkMax intakeMotorR;
    public DutyCycleEncoder encoderL;
    public DutyCycleEncoder encoderR;

    public boolean isIntakeDown;

    public IntakeLevelSubsystem() {
        this.intakeMotorL = new SparkMax(IntakeConstants.INTAKE_MOTOR_L, null);
        this.intakeMotorR = new SparkMax(IntakeConstants.INTAKE_MOTOR_R, null);
        this.encoderL = new DutyCycleEncoder(0); // TODO: Get actual ports
        this.encoderR = new DutyCycleEncoder(1); // TODO: Get actual ports

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