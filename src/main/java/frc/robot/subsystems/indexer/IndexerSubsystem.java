package frc.robot.subsystems.indexer;

import com.ctre.phoenix6.signals.RGBWColor;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.IntakeConstants;
import frc.robot.subsystems.led.LEDConstants;
import frc.robot.subsystems.led.LEDSubsystem;

public class IndexerSubsystem extends SubsystemBase {
  // Motor 1 = white rollers
  private final SparkMax indexer1Motor =  new SparkMax(IndexerConstants.INDEXER_1_MOTOR_ID, MotorType.kBrushless);

  // Motor 2 = orange compliance wheels near flywheel
  private final SparkMax indexer2Motor =
      new SparkMax(IndexerConstants.INDEXER_2_MOTOR_ID, MotorType.kBrushless);

  // Only using beam break 2
  private final DigitalInput index2BeamBreak =
      new DigitalInput(IndexerConstants.INDEXER_2_BEAM_BREAK_SENSOR_PORT);

  private boolean ledChange = false;

  // States
  private boolean indexingEnabled = false;
  private static boolean Shooting = false;
  private boolean reverse1Enabled = false;
  private boolean reverse2Enabled = false;

  public IndexerSubsystem() {}

  // -------------------------
  // Sensor helpers
  // -------------------------

  public boolean isIndex2Broken() {
    return !index2BeamBreak.get();
  }

  // -------------------------
  // State setters
  // -------------------------

  public void setIndexingEnabled(boolean enabled) {
    indexingEnabled = enabled;
  }

  public boolean getIndexingEnabled() {
    return indexingEnabled;
  }

  public void setShooting(boolean enabled) {
    Shooting = enabled;
  }

  public boolean getShooting() {
    return Shooting;
  }

  public void setReverse1(boolean enabled) {
    reverse1Enabled = enabled;
  }

  public void setReverse2(boolean enabled) {
    reverse2Enabled = enabled;
  }

  public void setReverseBoth(boolean enabled) {
    reverse1Enabled = enabled;
    reverse2Enabled = enabled;
  }

  public static void SetShooting(boolean shooting) { // Sets the shooting variable
    Shooting = shooting;
  }

  public void stopIndexer1() {
    indexer1Motor.set(0);
  }

  public void stopIndexer2() {
    indexer2Motor.set(0);
  }

  public void stopAll() {
    indexer1Motor.set(0);
    indexer2Motor.set(0);
  }

  // -------------------------
  // Optional direct-run helpers
  // These can still be useful for testing
  // -------------------------

   public void RunIndexer_1() { // Starts the white roller motor
    if (IndexerConstants.Reverse == false) {
      indexer1Motor.set(IndexerConstants.INDEXER_1_SPEED);
    } else{
       indexer1Motor.set(IndexerConstants.INDEXER_1_REVERSE_SPEED);
    }
  }

  public void runIndexer2Forward() {
    indexer2Motor.set(IndexerConstants.INDEXER_2_SPEED);
  }

  public void runIndexer1Reverse() {
    indexer1Motor.set(IndexerConstants.INDEXER_1_REVERSE_SPEED);
  }

  public void runIndexer2Reverse() {
    indexer2Motor.set(IndexerConstants.INDEXER_2_REVERSE_SPEED);
  }

  @Override
  public void periodic() {
    boolean index2Broken = isIndex2Broken();

    // -------------------------
    // LED example based on beam break 2
    // -------------------------
    if (index2Broken) {
      ledChange = true;
      LEDConstants.IDLE = false;
      LEDSubsystem.setLEDColor(
          new RGBWColor(LEDConstants.ORANGE[0], LEDConstants.ORANGE[1], LEDConstants.ORANGE[2], 0),
          false);
    } else if (ledChange) {
      ledChange = false;
      LEDConstants.IDLE = true;
    }

    // -------------------------
    // Motor 1 logic (white rollers)
    // -------------------------
    if (reverse1Enabled) {
      indexer1Motor.set(IndexerConstants.INDEXER_1_REVERSE_SPEED);
    } else if (indexingEnabled || Shooting) {
      indexer1Motor.set(IndexerConstants.INDEXER_1_SPEED);
    } else {
      indexer1Motor.set(0);
    }
    // -------------------------
    // Motor 2 logic (orange compliance wheels)
    // -------------------------
    if (reverse2Enabled) {
      indexer2Motor.set(IndexerConstants.INDEXER_2_REVERSE_SPEED);
    } else if (Shooting) {
      // During shooting, always run motor 2 forward
      indexer2Motor.set(IndexerConstants.INDEXER_2_SPEED);
    } else if (indexingEnabled && !index2Broken) {
      // During normal indexing, stop motor 2 once beam break 2 sees fuel
      indexer2Motor.set(IndexerConstants.INDEXER_2_SPEED);
    } else {
      indexer2Motor.set(0);
    }

    SmartDashboard.putBoolean("Indexer 2 Broken", index2Broken);
    SmartDashboard.putBoolean("Indexing Enabled", indexingEnabled);
    SmartDashboard.putBoolean("Shooting Enabled", Shooting);
    SmartDashboard.putBoolean("Reverse 1 Enabled", reverse1Enabled);
    SmartDashboard.putBoolean("Reverse 2 Enabled", reverse2Enabled);
  }
}