// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.



package frc.robot;
import com.ctre.phoenix6.signals.RGBWColor;
public class Constants {
    
    
    public class IntakeConstants {
        public static int INTAKE_MOTOR = 14;

        public enum Mode {
            ON,
            OFF,
            STOP
        }
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
  }

  public static final class IndexerConstants {
    public static final int INDEXER_MOTOR_ID = 0; // Example CAN ID for the indexer motor
    public static final double INDEXER_SPEED = 0.5; // Speed at which to run the indexer
    public static final int INDEXER_BEAM_BREAK_SENSOR_PORT = 0; // Digital Input port for the beam break sensor
  }
                                                 
}



