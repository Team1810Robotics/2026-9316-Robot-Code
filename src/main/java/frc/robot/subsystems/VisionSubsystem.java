package frc.robot.subsystems;

import frc.robot.LimelightHelpers;
import frc.robot.LimelightHelpers.PoseEstimate;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import frc.robot.Constants.HoodConstants;
// DriverStation not used in this subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase;
// removed unused/invalid wildcard import
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vision subsystem adapted to use your LimelightHelpers library and to write
 * the camera->robot transform into the Limelight on init if configured.
 *
 * Important: LimelightHelpers.setCameraPose_RobotSpace expects:
 *   (forward, side, up, roll, pitch, yaw)
 * where translations are in meters, rotations are in degrees.
 */
public class VisionSubsystem extends SubsystemBase {
    private static final Logger LOG = Logger.getLogger(VisionSubsystem.class.getName());

    // Limelight identifier ("" for default 'limelight' table, or the hostname if using multiple)
    private final String limelightName = "";

    // If true, the subsystem WILL write the camera pose into the Limelight during construction.
    // Set to false if you prefer to manually configure the Limelight's camera pose via the web UI.
    private static final boolean APPLY_CAMERA_POSE_TO_LIMELIGHT_ON_INIT = true;

    // Camera->robot transform values (meters and degrees). Replace these with your measured offsets.
    // forward = positive forward from robot origin to camera
    // side = positive left from robot origin to cameraa
    // up = positive up from robot origin to camera
    // roll/pitch/yaw in degrees describing camera orientation relative to robot frame
    private static final double CAMERA_FORWARD_METERS = 0.20; // example: 20 cm forward
    private static final double CAMERA_SIDE_METERS    = 0.0;   // example: centered
    private static final double CAMERA_UP_METERS      = 0.45;  // example: 45 cm above robot origin

    private static final double CAMERA_ROLL_DEG  = 0.0;
    private static final double CAMERA_PITCH_DEG = 0.0;
    private static final double CAMERA_YAW_DEG   = 0.0;

    // Optional Transform3d (kept for robot-side usage). Not required for writing to Limelight.
    // Initialized at runtime via setCameraToRobotTransform if needed.
    @SuppressWarnings("unused")
    private Transform3d cameraToRobot;

    // Cached outputs
    private static double hoodDistance;
    private static double shooterSpeed;

    public VisionSubsystem() {
        if (APPLY_CAMERA_POSE_TO_LIMELIGHT_ON_INIT) {
            writeCameraPoseToLimelight();
        }
    }

    /**
     * Writes the configured camera pose (camera -> robot) into the Limelight's NetworkTables
     * so the Limelight can publish robot-centered botpose entries correctly.
     *
     * This method calls LimelightHelpers.setCameraPose_RobotSpace(limelightName, forward, side, up, roll, pitch, yaw).
     *
     * Units:
     *  - forward, side, up: meters
     *  - roll, pitch, yaw: degrees
     *
     * After calling this you can verify the values in NTViewer under the entry "camerapose_robotspace"
     * or via the Limelight web UI.
     */
    public void writeCameraPoseToLimelight() {
        try {
            LimelightHelpers.setCameraPose_RobotSpace(
                    limelightName,
                    CAMERA_FORWARD_METERS,
                    CAMERA_SIDE_METERS,
                    CAMERA_UP_METERS,
                    CAMERA_ROLL_DEG,
                    CAMERA_PITCH_DEG,
                    CAMERA_YAW_DEG
            );
            // Optionally also set robot orientation baseline for MegaTag2 if you use it; omitted by default.
            LOG.info(String.format("Wrote camera pose to Limelight '%s': f=%.3fm s=%.3fm u=%.3fm r=%.2f p=%.2f y=%.2f",
                    limelightName, CAMERA_FORWARD_METERS, CAMERA_SIDE_METERS, CAMERA_UP_METERS,
                    CAMERA_ROLL_DEG, CAMERA_PITCH_DEG, CAMERA_YAW_DEG));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to write camera pose to Limelight: " + e.getMessage(), e);
        }
    }

    /** Turns the Limelight LEDs on (via helpers) */
    public void limeLightOn() {
        LimelightHelpers.setLEDMode_ForceOn(limelightName);
    LOG.info("Limelight: LED ON (via helpers)");
    }

    /** Turns the Limelight LEDs off (via helpers) */
    public void limeLightOff() {
        LimelightHelpers.setLEDMode_ForceOff(limelightName);
    LOG.info("Limelight: LED OFF (via helpers)");
    }

    /** @return whether the limelight currently sees a valid target */
    public boolean targetValid() {
        return LimelightHelpers.getTV(limelightName);
    }

    /** @return X offset (degrees) reported by Limelight (tx) */
    public double targetXOffset() {
        return LimelightHelpers.getTX(limelightName);
    }

    /** @return Y offset (degrees) reported by Limelight (ty) */
    public double targetYOffset() {
        return LimelightHelpers.getTY(limelightName);
    }

    /** @return target area (ta) */
    public double targetArea() {
        return LimelightHelpers.getTA(limelightName);
    }

    /** @return AprilTag / fiducial ID (tid) */
    public int getTargetID() {
        return (int) LimelightHelpers.getFiducialID(limelightName);
    }

    /**
     * Get the Limelight-provided 3D robot pose in WPILib Blue coordinate system (if available).
     * NOTE: since Limelight's botpose entries are already robot-centered when camera pose is set on the LL,
     * we do not apply cameraToRobot here. If you expect a camera-space pose, you would transform it by
     * the inverse of cameraToRobot.
     *
     * @return Optional<Pose3d> in field coordinates or empty if no valid pose
     */
    public Optional<Pose3d> getRobotPose3d() {
        if (!LimelightHelpers.getTV(limelightName)) {
            return Optional.empty();
        }
        Pose3d raw = LimelightHelpers.getBotPose3d_wpiBlue(limelightName);
        // If Limelight is publishing an empty/default Pose3d when invalid, consider checking botpose_tagcount via getLatestResults().
        return Optional.ofNullable(raw);
    }

    /**
     * Get a PoseEstimate object containing Pose2d, timestamp and latency suitable for addVisionMeasurement()
     * (uses botpose_wpiblue / getBotPoseEstimate_wpiBlue)
     *
     * @return Optional<PoseEstimate> or empty if none
     */
    public Optional<PoseEstimate> getPoseEstimateForOdometry() {
        PoseEstimate est = LimelightHelpers.getBotPoseEstimate_wpiBlue(limelightName);
        if (est == null || !LimelightHelpers.validPoseEstimate(est)) {
            return Optional.empty();
        }
        return Optional.of(est);
    }

    /**
     * getTargetingValues - hoodDistance[0] and shooterSpeed[1]
     * Mirrors original mapping but uses LimelightHelpers accessors.
     */
    public double[] getTargetingValues() {
        final double ty = targetYOffset();

        if (targetValid()) {
            try {
                if (ty <= -12) {
                    hoodDistance = -80.433 * Math.pow(ty, 2) - 2156.31 * ty - 14451.6;

                    if (hoodDistance > 0) {
                        hoodDistance = 0;
                    }

                    if (hoodDistance < HoodConstants.FORWARD_HOOD_LIMIT) {
                        hoodDistance = HoodConstants.FORWARD_HOOD_LIMIT;
                    }
                } else {
                    hoodDistance = 0;
                }

                if (ty > 1.5) {
                    shooterSpeed = 0.435;
                } else if (ty < 18.0 && ty > -4) {
                    shooterSpeed = 0.5;
                } else if (ty < -4 && ty > -8.5) {
                    shooterSpeed = 0.6;
                } else if (ty < -8.5 && ty > -12) {
                    shooterSpeed = 0.75;
                } else if (ty <= -12) {
                    shooterSpeed = 1.0;
                } else {
                    shooterSpeed = 0.5;
                    LOG.warning("Shooter Speed unmatched");
                }

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Exception in getTargetingValues", e);
            }
        }

        return new double[] {hoodDistance, shooterSpeed};
    }

    /** Allows runtime change of camera->robot transform used by robot-side code (does not auto-write to Limelight) */
    public void setCameraToRobotTransform(Transform3d transform, boolean writeToLimelight) {
        this.cameraToRobot = transform;
    LOG.info("Updated cameraToRobot transform.");
        if (writeToLimelight) {
            // Decompose transform into forward/side/up and roll/pitch/yaw (in degrees) for Limelight
            double forward = transform.getTranslation().getX();
            double side = transform.getTranslation().getY();
            double up = transform.getTranslation().getZ();
            // Rotation3d stores rotations about x (roll), y (pitch), z (yaw) in radians
            Rotation3d r = transform.getRotation();
            double rollDeg = Math.toDegrees(r.getX());
            double pitchDeg = Math.toDegrees(r.getY());
            double yawDeg = Math.toDegrees(r.getZ());
            try {
                LimelightHelpers.setCameraPose_RobotSpace(limelightName, forward, side, up, rollDeg, pitchDeg, yawDeg);
        LOG.info(String.format("Wrote updated camera pose to Limelight '%s': f=%.3fm s=%.3fm u=%.3fm r=%.2f p=%.2f y=%.2f",
            limelightName, forward, side, up, rollDeg, pitchDeg, yawDeg));
            } catch (Exception e) {
        LOG.log(Level.SEVERE, "Failed to write updated camera pose to Limelight: " + e.getMessage(), e);
            }
        }
    }

    /** Set Limelight pipeline via helper */
    public void setPipeline(int idx) {
        LimelightHelpers.setPipelineIndex(limelightName, idx);
    }
}