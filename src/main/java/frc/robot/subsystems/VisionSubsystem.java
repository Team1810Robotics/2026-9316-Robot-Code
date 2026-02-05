package frc.robot.subsystems.vision;

import dev.doglog.DogLog;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import frc.robot.lib.LimelightHelpers;
import frc.robot.lib.LimelightHelpers.PoseEstimate;

public class Vision extends SubsystemBase {
    private String name;

    public Vision(String limelightName) {
        this.name = limelightName;
    }
    
    public void setRobotOrientation(double yaw) {
        // Keeping variables identical, but ensuring MegaTag2 gets the data it needs
        LimelightHelpers.SetRobotOrientation(name, yaw, 0, 0, 0, 0, 0);
    }

    public void setRobotOrientation() {
        setRobotOrientation(RobotContainer.getDrivetrain().getState().Pose.getRotation().getDegrees());
    }

    public void setIMUMode(int mode) {
        LimelightHelpers.SetIMUMode(name, mode);
    }

    public void resetIMUHeading(double yaw) {
        setIMUMode(1);
        setRobotOrientation(yaw);
    }

    @Override
    public void periodic() {
        // 1. UPDATE GYRO FIRST: Timing is everything. MegaTag2 uses the gyro data to 
        // calculate the pose. If we do this first, the Limelight has the freshest data.
        double currentYaw = RobotContainer.getDrivetrain().getState().Pose.getRotation().getDegrees();
        setRobotOrientation(currentYaw);

        // 2. FETCH POSE: Standard MegaTag2 retrieval
        PoseEstimate poseEstimate = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(name);
        // you want pose estimate cause its epic lowkey. 
        // 3. THE "BETTER" LOGIC: Validation
        // - Is the estimate null? (Limelight disconnected)
        // - Is the tag count 0? (No tags in sight)
        // - Is the robot moving too fast for a reliable vision update? (Optional safety)
        if (poseEstimate != null && poseEstimate.tagCount > 0) {
            
            // Only add the measurement if it's "real" data.
            // This prevents your robot from "teleporting" to 0,0 when it loses sight of a tag.
            RobotContainer.getDrivetrain().addVisionMeasurement(
                poseEstimate.pose, 
                poseEstimate.timestampSeconds
            );

            // Detailed logging for post-match analysis
            DogLog.log("Vision/" + name + "/Pose", poseEstimate.pose);
            DogLog.log("Vision/" + name + "/TagCount", poseEstimate.tagCount);
            DogLog.log("Vision/" + name + "/AvgTagDist", poseEstimate.avgTagDist);
        }
    }
}