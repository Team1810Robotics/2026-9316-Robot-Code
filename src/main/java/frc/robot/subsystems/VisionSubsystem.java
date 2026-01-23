package frc.robot.subsystems;

import frc.robot.LimelightHelpers;
import frc.robot.LimelightHelpers.PoseEstimate;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants.HoodConstants;
import frc.robot.Constants.VisionConstants;
// DriverStation not used in this subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase;
// removed unused/invalid wildcard import
import java.util.Optional;
import java.util.Arrays;
import edu.wpi.first.math.geometry.Pose2d;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Vision subsystem adapted to use your LimelightHelpers library, auto-selecting
 * the appropriate alliance botpose entries (wpiblue / wpired) based on DriverStation.
 *
 * Includes a small runtime validator to log raw Limelight arrays and converted poses
 * for on-robot verification.
 */
public class VisionSubsystem extends SubsystemBase {
    private static final Logger LOG = Logger.getLogger(VisionSubsystem.class.getName());

    // Limelight identifier ("" for default 'limelight' table, or the hostname if using multiple)
    private final String limelightName = "";

    // If true, the subsystem WILL write the camera pose into the Limelight during construction.
    private static final boolean APPLY_CAMERA_POSE_TO_LIMELIGHT_ON_INIT = true;

    // Camera->robot transform values (meters and degrees). Replace these with your measured offsets.
   // 
    
    /**
     * private static final double CAMERA_SIDE_METERS    = 0.0;   // example: centered
    private static final double CAMERA_UP_METERS      = 0.45;  // example: 45 cm above robot origin

    private static final double CAMERA_ROLL_DEG  = 0.0;
    private static final double CAMERA_PITCH_DEG = 0.0;
    private static final double CAMERA_YAW_DEG   = 0.0;
    private static final double CAMERA_FORWARD_METERS = 0.20; // example: 20 cm forward
     */
    // Optional Transform3d (kept for robot-side usage). Not required for writing to Limelight.
    private Transform3d cameraToRobot = new Transform3d(
        new Translation3d(VisionConstants.CAMERA_FORWARD_METERS, VisionConstants.CAMERA_SIDE_METERS, VisionConstants.CAMERA_UP_METERS),
        new Rotation3d(Math.toRadians(VisionConstants.CAMERA_ROLL_DEG), Math.toRadians(VisionConstants.CAMERA_PITCH_DEG), Math.toRadians(VisionConstants.CAMERA_YAW_DEG))
    );

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
     */
    public void writeCameraPoseToLimelight() {
        try {
                LimelightHelpers.setCameraPose_RobotSpace(
                    limelightName,
                    VisionConstants.CAMERA_FORWARD_METERS,
                    VisionConstants.CAMERA_SIDE_METERS,
                    VisionConstants.CAMERA_UP_METERS,
                    VisionConstants.CAMERA_ROLL_DEG,
                    VisionConstants.CAMERA_PITCH_DEG,
                    VisionConstants.CAMERA_YAW_DEG
            );
        LOG.info(String.format("Wrote camera pose to Limelight '%s': f=%.3fm s=%.3fm u=%.3fm r=%.2f p=%.2f y=%.2f",
            limelightName, VisionConstants.CAMERA_FORWARD_METERS, VisionConstants.CAMERA_SIDE_METERS, VisionConstants.CAMERA_UP_METERS,
            VisionConstants.CAMERA_ROLL_DEG, VisionConstants.CAMERA_PITCH_DEG, VisionConstants.CAMERA_YAW_DEG));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to write camera pose to Limelight", e);
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
     * Helper to decide which alliance botpose entry to use based on DriverStation state.
     * Defaults to BLUE if the alliance is invalid/unknown.
     */
    private Alliance getCurrentAllianceOrDefault() {
        try {
            // Use reflection to support both older WPILib (returns Alliance) and newer (Optional<Alliance>)
            java.lang.reflect.Method m = DriverStation.class.getMethod("getAlliance");
            Object res = m.invoke(null);
            if (res == null) {
                LOG.warning("DriverStation alliance is unavailable (null); defaulting to BLUE for vision pose entries.");
                return Alliance.Blue;
            }

            // Optional case
            if (res instanceof Optional) {
                Optional<?> maybe = (Optional<?>) res;
                if (!maybe.isPresent()) {
                    LOG.warning("DriverStation alliance Optional empty; defaulting to BLUE for vision pose entries.");
                    return Alliance.Blue;
                }
                Object val = maybe.get();
                if (val == null) {
                    LOG.warning("DriverStation alliance Optional contained null; defaulting to BLUE.");
                    return Alliance.Blue;
                }
                Alliance a = (Alliance) val;
                if (a == null || "Invalid".equalsIgnoreCase(a.name())) {
                    LOG.warning("DriverStation alliance is Invalid; defaulting to BLUE for vision pose entries.");
                    return Alliance.Blue;
                }
                return a;
            }

            // Direct Alliance return
            if (res instanceof Alliance) {
                Alliance a = (Alliance) res;
                if (a == null || "Invalid".equalsIgnoreCase(a.name())) {
                    LOG.warning("DriverStation alliance is Invalid; defaulting to BLUE for vision pose entries.");
                    return Alliance.Blue;
                }
                return a;
            }

            LOG.warning("Unexpected return type from DriverStation.getAlliance(); defaulting to BLUE.");
            return Alliance.Blue;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to read DriverStation alliance via reflection; defaulting to BLUE.", e);
            return Alliance.Blue;
        }
    }

    /**
     * Returns the alliance-specific botpose Pose3d (uses botpose_wpiblue or botpose_wpired).
     * If no valid target is present, returns Optional.empty().
     */
    public Optional<Pose3d> getRobotPose3d() {
        if (!LimelightHelpers.getTV(limelightName)) {
            return Optional.empty();
        }

        Alliance alliance = getCurrentAllianceOrDefault();
        Pose3d pose;
        if (alliance == Alliance.Blue) {
            pose = LimelightHelpers.getBotPose3d_wpiBlue(limelightName);
        } else { // Red
            pose = LimelightHelpers.getBotPose3d_wpiRed(limelightName);
        }

        return Optional.ofNullable(pose);
    }

    /**
     * Returns a PoseEstimate suitable for addVisionMeasurement() and automatically chooses
     * the correct botpose_wpiblue / botpose_wpired estimator based on DriverStation alliance.
     *
     * @return Optional<PoseEstimate> or empty if no valid estimate
     */
    public Optional<PoseEstimate> getPoseEstimateForOdometry() {
        Alliance alliance = getCurrentAllianceOrDefault();
        PoseEstimate est;
        if (alliance == Alliance.Blue) {
            est = LimelightHelpers.getBotPoseEstimate_wpiBlue(limelightName);
        } else {
            est = LimelightHelpers.getBotPoseEstimate_wpiRed(limelightName);
        }

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
                LOG.log(Level.SEVERE, "Exception computing targeting values", e);
            }
        }

        return new double[] {hoodDistance, shooterSpeed};
    }

    /** Allows runtime change of camera->robot transform used by robot-side code (does not auto-write to Limelight) */
    public void setCameraToRobotTransform(Transform3d transform, boolean writeToLimelight) {
        this.cameraToRobot = transform;
        LOG.info("Updated cameraToRobot transform.");
        if (writeToLimelight) {
            double forward = transform.getTranslation().getX();
            double side = transform.getTranslation().getY();
            double up = transform.getTranslation().getZ();
            Rotation3d r = transform.getRotation();
            double rollDeg = Math.toDegrees(r.getX());
            double pitchDeg = Math.toDegrees(r.getY());
            double yawDeg = Math.toDegrees(r.getZ());
            try {
                LimelightHelpers.setCameraPose_RobotSpace(limelightName, forward, side, up, rollDeg, pitchDeg, yawDeg);
                LOG.info(String.format("Wrote updated camera pose to Limelight '%s': f=%.3fm s=%.3fm u=%.3fm r=%.2f p=%.2f y=%.2f",
                        limelightName, forward, side, up, rollDeg, pitchDeg, yawDeg));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to write updated camera pose to Limelight", e);
            }
        }
    }

    /** Set Limelight pipeline via helper */
    public void setPipeline(int idx) {
        LimelightHelpers.setPipelineIndex(limelightName, idx);
    }

    /**
     * Small validator / diagnostic that logs:
     *  - current alliance
     *  - Limelight "tv"/t2d info
     *  - raw botpose arrays (botpose, botpose_wpiblue, botpose_wpired)
     *  - camera pose entry (camerapose_robotspace)
     *  - Limelight JSON dump (shortened)
     *  - converted Pose2d/Pose3d and PoseEstimate (if present)
     *
     * Call this once (or on demand) after deploying to verify that arrays and conversions are correct.
     */
    public void runValidationCheck() {
        try {
            Alliance alliance = getCurrentAllianceOrDefault();
            LOG.info("Vision Validator: current alliance = " + alliance.name());

            boolean tv = LimelightHelpers.getTV(limelightName);
            LOG.info("Vision Validator: Limelight tv (target valid) = " + tv);

            double[] botpose = LimelightHelpers.getBotPose(limelightName);
            double[] botposeBlue = LimelightHelpers.getBotPose_wpiBlue(limelightName);
            double[] botposeRed = LimelightHelpers.getBotPose_wpiRed(limelightName);
            Pose3d cameraPose3d = LimelightHelpers.getCameraPose3d_RobotSpace(limelightName);

            LOG.info("Vision Validator: raw botpose: " + Arrays.toString(botpose));
            LOG.info("Vision Validator: raw botpose_wpiblue: " + Arrays.toString(botposeBlue));
            LOG.info("Vision Validator: raw botpose_wpired: " + Arrays.toString(botposeRed));
            LOG.info("Vision Validator: raw camerapose_robotspace: " + cameraPose3d);

            // Log converted Pose3d/Pose2d using helpers
            Pose3d pose3d;
            Pose2d pose2d;
            if (alliance == Alliance.Blue) {
                pose3d = LimelightHelpers.getBotPose3d_wpiBlue(limelightName);
                pose2d = LimelightHelpers.getBotPose2d_wpiBlue(limelightName);
            } else {
                pose3d = LimelightHelpers.getBotPose3d_wpiRed(limelightName);
                pose2d = LimelightHelpers.getBotPose2d_wpiRed(limelightName);
            }

            LOG.info("Vision Validator: converted Pose3d = " + pose3d);
            LOG.info("Vision Validator: converted Pose2d = " + pose2d);

            // PoseEstimate (includes timestamp & latency) when available
            PoseEstimate est = (alliance == Alliance.Blue)
                    ? LimelightHelpers.getBotPoseEstimate_wpiBlue(limelightName)
                    : LimelightHelpers.getBotPoseEstimate_wpiRed(limelightName);

            if (est != null && LimelightHelpers.validPoseEstimate(est)) {
                LOG.info(String.format("Vision Validator: PoseEstimate: pose=%s ts=%.3f latency=%.3f tagCount=%d",
                        est.pose, est.timestampSeconds, est.latency, est.tagCount));
                LimelightHelpers.printPoseEstimate(est);
            } else {
                LOG.info("Vision Validator: PoseEstimate not available or invalid.");
            }

            // Small JSON preview — do not spam logs; only print first ~512 chars
            String json = LimelightHelpers.getJSONDump(limelightName);
            if (json != null && !json.isEmpty()) {
                String preview = json.length() > 512 ? json.substring(0, 512) + "..." : json;
                LOG.fine("Vision Validator: JSON dump preview: " + preview);
            } else {
                LOG.fine("Vision Validator: JSON dump empty.");
            }

            LOG.info("Vision Validator: Completed check. Verify that raw arrays match expected [x,y,z,roll,pitch,yaw] ordering (roll/pitch/yaw degrees) and translations are meters.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Vision Validator failed", e);
        }
    }
}