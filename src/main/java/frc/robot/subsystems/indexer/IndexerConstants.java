package frc.robot.subsystems.indexer;

public final class IndexerConstants {
  // Lower indexer motor: floor rollers moving fuel downhill toward shooter
  public static final int LOWER_INDEXER_MOTOR_ID = 13; 

  // Upper indexer motor: wheels feeding fuel into the flywheel
  public static final int UPPER_INDEXER_MOTOR_ID = 17; 

  // Beam break 1: detects whether fuel is present lower in the indexer path
  public static final int BEAM_BREAK_1_PORT = 3; 

  // Beam break 2: detects whether fuel is primed at the shooter
  public static final int BEAM_BREAK_2_PORT = 4; 

  // Normal forward indexing speeds
  public static final double LOWER_FORWARD_SPEED = -0.45;
  public static final double UPPER_FORWARD_SPEED = -0.45;

  // Reverse speeds for jam clearing
  public static final double LOWER_REVERSE_SPEED = 0.35;
  public static final double UPPER_REVERSE_SPEED = 0.35;


  public static final double SHOOT_FEED_SPEED = -0.60;
}