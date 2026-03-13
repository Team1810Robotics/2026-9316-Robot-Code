package frc.robot.subsystems.indexer;

import com.ctre.phoenix6.signals.RGBWColor;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.led.*;

import com.revrobotics.spark.config.SparkMaxConfig;

public class IndexerSubsystem extends SubsystemBase {
  private final SparkMax indexer_1_Motor =
      new SparkMax(IndexerConstants.INDEXER_1_MOTOR_ID, MotorType.kBrushless); // White Roller
  private final SparkMax indexer_2_Motor =
      new SparkMax(IndexerConstants.INDEXER_2_MOTOR_ID, MotorType.kBrushless); // Orange Wheels
      SparkMaxConfig config = new SparkMaxConfig();
      config.@idleMode(IdleMode.kBrake);
  private final DigitalInput Index_1_BeamBreak =
      new DigitalInput(
          IndexerConstants.INDEXER_1_BEAM_BREAK_SENSOR_PORT); // Index Beam Break Sensor
  private final DigitalInput Index_2_BeamBreak =
      new DigitalInput(
          IndexerConstants.INDEXER_2_BEAM_BREAK_SENSOR_PORT); // Index Beam Break Sensor

  private boolean LEDChange = false; // To make sure the LEDs aren't changed multiple times
  private static boolean Shooting = false; // Determine if the flywheel is activley shooting
  private boolean IndexingEnabled = false; // If the motors are currently running

  public void RunIndexer_1() { // Starts the white roller motor
    if (IndexerConstants.Reverse == false) {
      indexer_1_Motor.set(IndexerConstants.INDEXER_1_SPEED);
    } else{
       indexer_1_Motor.set(IndexerConstants.INDEXER_1_REVERSE_SPEED);
    }
  }

  public void StopIndexer_1() { // Stops the white roller motor
    indexer_1_Motor.set(0);
  }

  public void RunIndexer_2() { // Starts the orange wheel motor
    if (IndexerConstants.Reverse == false) {
      indexer_2_Motor.set(IndexerConstants.INDEXER_2_SPEED);
    } else {
      indexer_2_Motor.set(IndexerConstants.INDEXER_2_REVERSE_SPEED);
    }

  }

  public void StopIndexer_2() { // Stops the orange wheel motor
    indexer_2_Motor.set(0);
  }

  public void SetIndexEnabled(boolean enabled) { // Sets the indexing enabled variable
    IndexingEnabled = enabled;
  }

  public static void SetShooting(boolean shooting) { // Sets the shooting variable
    Shooting = shooting;
  }

  public boolean GetIndexingEnabled() { // Gets the indexing enabled variable
    return IndexingEnabled;
  }

  public boolean GetShooting() { // Gets the shooting variable
    return Shooting;
  }

  @Override
  public void periodic() {
    boolean index_1_Broken =
       false; // Detects if the beam is broken (ball is present)
    boolean index_2_Broken =
        !Index_2_BeamBreak
            .get(); // Detects if the beam is broken in the second area (ball is present)

    if (index_1_Broken == true) {
      LEDChange = true;
      LEDConstants.IDLE = false;
      LEDSubsystem.setLEDColor(
          new RGBWColor(LEDConstants.ORANGE[0], LEDConstants.ORANGE[1], LEDConstants.ORANGE[2], 0),
          false); // Lets Drive Team know a ball is detected by setting LEDs to orange
      System.out.println("Ball Detected");
    } else {
      if (LEDChange == true) { // Puts the LEDs back into idle
        LEDChange = false;
        LEDConstants.IDLE = true;
      }
    }

    if (IndexingEnabled
        || Shooting) { // Only runs if Indexing is enabled or shooting is enabled ONLY FOR WHITE
      // ROLLERS
      RunIndexer_1();
    } else {
      StopIndexer_1();
    }

    if (Shooting) { // Only runs if shooting is enabled ONLY FOR ORANGE WHEELS
      RunIndexer_2();
    } else if (IndexingEnabled
        && !index_2_Broken) { // Only runs if indexing is enabled and there isn't a ball in the
      // second area (to prevent jamming)
      RunIndexer_2();
    } else {
      StopIndexer_2();
    }

    SmartDashboard.putBoolean("Indexer 1 Broken", index_1_Broken);
    SmartDashboard.putBoolean("Indexer 2 Broken", index_2_Broken);

  }
}

// Code by: Will Edwards (The best programmer in the world)
