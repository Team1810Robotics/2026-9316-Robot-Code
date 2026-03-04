package frc.robot;

import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import dev.doglog.DogLog;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.CommandSwerveDrivetrain;
import frc.robot.subsystems.hood.HoodSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.vision.VisionSubsystem;

public class Autos {
  public enum AutoMode {
    SideScoreLeft;
  }

  public Autos(
      HoodSubsystem hood,
      IntakeSubsystem intake,
      VisionSubsystem vision,
      CommandSwerveDrivetrain drivetrain) {
    NamedCommands.registerCommand("SideScoreLeft", SideScoreLeft(drivetrain));
    DogLog.log("Auto/Status", "Named commands registered");
  }

  public static Command getAuto(String autoName) {
    DogLog.log("Auto/Selected", autoName);
    return new PathPlannerAuto(autoName);
  }

  public static Command SideScoreLeft(CommandSwerveDrivetrain drivetrain) {
    DogLog.log("Auto/Command", "SideScoreLeft");
    return drivetrain.runOnce(() -> {});
  }
}
//6741