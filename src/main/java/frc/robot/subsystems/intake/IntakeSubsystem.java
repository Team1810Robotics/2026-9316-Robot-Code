package frc.robot.subsystems.intake;


import edu.wpi.first.wpilibj2.command.SubsystemBase;

//import com.ctre.phoenix6.sim.TalonFXSimState.MotorType;
import com.revrobotics.spark.SparkMax;




public class IntakeSubsystem extends SubsystemBase {
    public SparkMax intakeMotor;
    public SparkMax intakeMotor_L;
    public SparkMax intakeMotor_R;
    public IntakeSubsystem() {
        intakeMotor = new SparkMax(IntakeConstants.INTAKE_MOTOR, com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
        // intakeMotor_L = new SparkMax(IntakeConstants.INTAKE_MOTOR_L, com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
        // intakeMotor_R = new SparkMax(IntakeConstants.INTAKE_MOTOR_R, com.revrobotics.spark.SparkLowLevel.MotorType.kBrushless);
        intakeMotor.set(0);
        // intakeMotor_L.set(0);
        // intakeMotor_R.set(0);
    }

    public void run(double speed) {
        intakeMotor.set(speed);
        // intakeMotor_L.set(speed);
        // intakeMotor_R.set(speed);
    }


    public void stop() {
        intakeMotor.stopMotor(); // Stop the intake motor
        // intakeMotor_L.stopMotor();
        // intakeMotor_R.stopMotor();
    }


}



