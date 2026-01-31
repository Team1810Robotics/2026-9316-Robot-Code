package frc.robot.subsystems;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.DigitalInput;

public class Indexer extends SubsystemBase {
    private final SparkMax indexerMotor1 = new SparkMax(0,null); // The Left roller motor
    private final SparkMax indexerMotor2 = new SparkMax(0,null); //The right roller motor
    private final SparkMax indexerMotor3 = new SparkMax(0,null); //The left orange wheel motor
    private final SparkMax indexerMotor4 = new SparkMax(0,null); //The right orange wheel motor

    private final DigitalInput IndexBeamBreak = new DigitalInput(0); //Index Beam Break Sensor

    public void RunIndexer() {
        indexerMotor1.set(0.5);
        indexerMotor2.set(0.5);
        indexerMotor3.set(0.5);
        indexerMotor4.set(0.5);
    }
    public void StopIndexer() {
        indexerMotor1.set(0);
        indexerMotor2.set(0);
        indexerMotor3.set(0);
        indexerMotor4.set(0);
    }
    public void BeamBreakStop() {
        indexerMotor3.set(0);
        indexerMotor4.set(0);
    }

    @Override
    public void periodic() {
        boolean indexBroken = !IndexBeamBreak.get();

        if (indexBroken) {
            BeamBreakStop();
        }
    }

}
