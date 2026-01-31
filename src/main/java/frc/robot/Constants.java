package frc.robot;

/**
 * Project-wide constants.
 *
 * Note: this file intentionally keeps a small set of placeholders used by other
 * modules. Replace the placeholder values with measured constants from your
 * robot (for example, hood limits) as you validate on hardware.
 */
public class Constants {
	/**
	 * Hood-related constants. The FORWARD_HOOD_LIMIT value is a placeholder and
	 * should be replaced with your robot's actual forward hood limit (in the
	 * same units used by your hoodDistance calculation).
	 */
	public static class GyroAndIMUConstants {
		// Placeholder; set to a sensible value for your robot (e.g. -50.0)
		public static final double GYRO_YAW_OFFSET_DEGREES = 0.0;
	}
	public static class HoodConstants {
		// Placeholder; set to a sensible value for your robot (e.g. -50.0)
		public static final double FORWARD_HOOD_LIMIT = -1000.0;

	
	}
	public static class VisionConstants {
		// Public vision constants — set these to your measured camera offsets.
		public static final double CAMERA_FORWARD_METERS = 0.20; // 20 cm forward
		public static final double CAMERA_SIDE_METERS    = 0.0;  // centered
		public static final double CAMERA_UP_METERS      = 0.45; // 45 cm above robot origin

		public static final double CAMERA_ROLL_DEG  = 0.0;
		public static final double CAMERA_PITCH_DEG = 0.0;
		public static final double CAMERA_YAW_DEG   = 0.0;

		public static final int aprilTagNextToHumanPlayerBlueAllianceOutside = 29; // ID of the AprilTag on the right of the human player station
		public static final int aprilTagNextToHumanPlayerBlueAllianceInside  = 30; // ID of the AprilTag on the left of the human player station
		public static final int aprilTagNextToHumanPlayerRedAllianceOutside  = 13; // ID of the AprilTag on the right of the human player station
		public static final int aprilTagNextToHumanPlayerRedAllianceInside   = 14; // ID of the AprilTag on the left of the human player station
		public static final int aprilTagClimbBlueAllianceClosestToHumanPlayer= 31; // ID of the AprilTag on the climb structure
		public static final int aprilTagClimbBlueAllianceFurthestFromHumanPlayer = 32; // ID of the AprilTag on the climb structure
		public static final int aprilTagClimbRedAllianceClosestToHumanPlayer= 15; // ID of the AprilTag on the climb structure closest to human player
		public static final int aprilTagClimbRedAllianceFurthestFromHumanPlayer = 16; // ID of the AprilTag on the climb structure furthest from human player
		public static final int aprilTagScoringBlueAllianceClosestToHumanPlayer = 26; // ID of the AprilTag on the near scoring area
		public static final int aprilTagScoringBlueAllianceFurthestFromHumanPlayer = 25; // ID of the AprilTag on the near scoring area
		public static final int aprilTagScoringRedAllianceClosestToHumanPlayer = 10; // ID of the AprilTag on the far scoring area
		public static final int aprilTagScoringRedAllianceFurthestFromHumanPlayer = 9; // ID of the AprilTag on the far scoring area
		//add more as needed

		// april tag locations visual https://drive.google.com/file/d/1Urb7EcdkFHfVp7dLyGpgPSgWiyvLkA06/view?usp=sharing
	}
}