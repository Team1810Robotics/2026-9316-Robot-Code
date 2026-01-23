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
		
	}
}