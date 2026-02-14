// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.signals.RGBWColor;

public class Constants {
    // ========== MOTOR CAN IDS (Central location for all motors) ==========
    public static final class MotorIDs {
        // Intake motors
        public static final int INTAKE_MOTOR_LEFT = 11;      // Left side motor (inverted, facing outward)
        public static final int INTAKE_MOTOR_RIGHT = 12;     // Right side motor (inverted, facing outward)
        public static final int INTAKE_MOTOR_WHEELS = 13;    // Bottom wheel motor (spinning intake wheels)
        
        // Flywheel motors
        public static final int FLYWHEEL_MOTOR_LEFT = 1;
        public static final int FLYWHEEL_MOTOR_RIGHT = 2;
        
        // Climb motors
        public static final int CLIMB_MOTOR_1 = 3;
        public static final int CLIMB_MOTOR_2 = 4;
        
        // Indexer motors
        public static final int INDEXER_MOTOR = 0;
        
        // Hood motor
        public static final int HOOD_MOTOR = 14;
    }
    
    public static class GyroAndIMUConstants {
		// Placeholder; set to a sensible value for your robot (e.g. -50.0)
		public static final double GYRO_YAW_OFFSET_DEGREES = 0.0;
	}
	public static class HoodConstants {
		// Behavior limits
		public static final double FORWARD_HOOD_LIMIT = -1000.0;
		public static final double REVERSE_HOOD_LIMIT = 0.0;

		public enum Mode {
			ON,
			OFF,
			STOP
		}
	}
	public static class VisionConstants {
		// Public vision constants — set these to your measured camera offsets.
		public static final double CAMERA_FORWARD_METERS = 0.20; // 20 cm forward
		public static final double CAMERA_SIDE_METERS    = 0.0;  // centered
		public static final double CAMERA_UP_METERS      = 0.45; // 45 cm above robot origin

    public static final double CAMERA_ROLL_DEG = 0.0;
    public static final double CAMERA_PITCH_DEG = 0.0;
    public static final double CAMERA_YAW_DEG = 0.0;

    public static final int aprilTagNextToHumanPlayerBlueAllianceOutside =
        29; // ID of the AprilTag on the right of the human player station
    public static final int aprilTagNextToHumanPlayerBlueAllianceInside =
        30; // ID of the AprilTag on the left of the human player station
    public static final int aprilTagNextToHumanPlayerRedAllianceOutside =
        13; // ID of the AprilTag on the right of the human player station
    public static final int aprilTagNextToHumanPlayerRedAllianceInside =
        14; // ID of the AprilTag on the left of the human player station
    public static final int aprilTagClimbBlueAllianceClosestToHumanPlayer =
        31; // ID of the AprilTag on the climb structure
    public static final int aprilTagClimbBlueAllianceFurthestFromHumanPlayer =
        32; // ID of the AprilTag on the climb structure
    public static final int aprilTagClimbRedAllianceClosestToHumanPlayer =
        15; // ID of the AprilTag on the climb structure closest to human player
    public static final int aprilTagClimbRedAllianceFurthestFromHumanPlayer =
        16; // ID of the AprilTag on the climb structure furthest from human player
    public static final int aprilTagScoringBlueAllianceClosestToHumanPlayer =
        26; // ID of the AprilTag on the near scoring area
    public static final int aprilTagScoringBlueAllianceFurthestFromHumanPlayer =
        25; // ID of the AprilTag on the near scoring area
    public static final int aprilTagScoringRedAllianceClosestToHumanPlayer =
        10; // ID of the AprilTag on the far scoring area
    public static final int aprilTagScoringRedAllianceFurthestFromHumanPlayer =
        9; // ID of the AprilTag on the far scoring area
    // add more as needed

		// april tag locations visual https://drive.google.com/file/d/1Urb7EcdkFHfVp7dLyGpgPSgWiyvLkA06/view?usp=sharing
	}
    
    public static final class IntakeConstants {
        // Proximity sensors (object detection on sides)
        public static final int PROXIMITY_SENSOR_PORT_LEFT = 0;
        public static final int PROXIMITY_SENSOR_PORT_RIGHT = 1;
    }

  public static final class FlywheelConstants {
    // Flywheel configuration (motor IDs in MotorIDs class)
    public static final int BEAM_BREAK_PORT = 5;
  }

  public static final class ClimbConstants {
    // Climb configuration (motor IDs in MotorIDs class)
  }

  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static final class LEDConstants {
    public static final int[] RED = {255, 0, 0, 0}; // RGB values
    public static final int[] YELLOW = {255, 255, 0}; // RGB values
    public static final int[] WHITE = {255, 255, 255}; // RGB values
    public static final int[] ORANGE = {255, 128, 0}; // RGB values
    public static final int[] GREEN = {0, 255, 0}; // RGB values
    public static final RGBWColor BLUE = new RGBWColor(0, 0, 255, 0); // RGB values
    public static final int[] Purple = {255, 0, 255}; // RGB values
    public static final int CANDLE_ID = 20; // CANdle ID
    public static final int NUM_LEDS = 0; // TODO: Find
  }

  public static final class IndexerConstants {
    public static final double INDEXER_SPEED = 0.5; // Speed at which to run the indexer
    public static final int INDEXER_BEAM_BREAK_SENSOR_PORT = 0; // Digital Input port for the beam break sensor
  }
}
