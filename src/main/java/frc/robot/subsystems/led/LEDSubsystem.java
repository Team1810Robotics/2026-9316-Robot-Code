package frc.robot.subsystems.led;

import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.FireAnimation;
import com.ctre.phoenix6.controls.LarsonAnimation;
import com.ctre.phoenix6.controls.RainbowAnimation;
import com.ctre.phoenix6.controls.RgbFadeAnimation;
import com.ctre.phoenix6.controls.SingleFadeAnimation;
import com.ctre.phoenix6.controls.SolidColor;
import com.ctre.phoenix6.controls.StrobeAnimation;
import com.ctre.phoenix6.controls.TwinkleAnimation;
import com.ctre.phoenix6.controls.TwinkleOffAnimation;
import com.ctre.phoenix6.hardware.CANdle;
import com.ctre.phoenix6.signals.AnimationDirectionValue;
import com.ctre.phoenix6.signals.LossOfSignalBehaviorValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import com.ctre.phoenix6.signals.StripTypeValue;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDSubsystem extends SubsystemBase {

  private CANdle m_candle;

  public void StartLEDSubsystem() {
    m_candle = new CANdle(LEDConstants.CANDLE_ID);
    configureCANdle();
  }

  public static RGBWColor LEDColor =
      new RGBWColor(255, 0, 0, 0); // This Defaults to red and can be changed through a command

  private void configureCANdle() {
    CANdleConfiguration config = new CANdleConfiguration();

    config.LED.BrightnessScalar = 1;
    config.LED.StripType = StripTypeValue.RGB; // TODO: Figure out whch strip type we have
    config.LED.LossOfSignalBehavior = LossOfSignalBehaviorValue.DisableLEDs;

    config.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Enabled;

    m_candle.getConfigurator().apply(config);
  }

  public void setSolidColor(int r, int g, int b) {
    m_candle.setControl(
        new SolidColor(8, LEDConstants.NUM_LEDS).withColor(new RGBWColor(255, 0, 0)));
  }

  public void initLEDColor() {
    setSolidColor(255, 20, 147);
  }

  // private static final RGBWColor kViolet = RGBWColor.fromHSV(Degrees.of(270), 0.9, 0.8);
  // private static final RGBWColor kRed = RGBWColor.fromHex("#D9000000").orElseThrow();

  // public static RGBWColor LEDColor = new RGBWColor(255, 0, 0, 0); //This Defaults to red and can
  // be changed through a command

  private enum AnimationType {
    None,
    ColorFlow,
    Fire,
    Larson,
    Rainbow,
    RgbFade,
    SingleFade,
    Strobe,
    Twinkle,
    TwinkleOff,
  }

  public static String strLEDColor = "";
  public static String strLEDAnimation = "";

  private static final int kSlot0StartIdx = 8;
  private static final int kSlot0EndIdx = 37;

  private static final int kSlot1StartIdx = 38;
  private static final int kSlot1EndIdx = 67;

  private AnimationType m_anim0State = AnimationType.None;
  private AnimationType m_anim1State = AnimationType.None;

  private int ColorCycle = 0;

  private final SendableChooser<AnimationType> m_anim0Chooser =
      new SendableChooser<AnimationType>();
  private final SendableChooser<AnimationType> m_anim1Chooser =
      new SendableChooser<AnimationType>();

  @Override
  public void periodic() {
    /* if the selection for slot 0 changes, change animations */
    final var anim0Selection = m_anim0Chooser.getSelected();
    if (m_anim0State != anim0Selection) {
      m_anim0State = anim0Selection;

      switch (m_anim0State) {
        default:
          strLEDAnimation = m_anim0State.toString();
        case ColorFlow:
          m_candle.setControl(
              new ColorFlowAnimation(kSlot0StartIdx, kSlot0EndIdx).withSlot(0).withColor(LEDColor));
          break;
        case Rainbow:
          m_candle.setControl(new RainbowAnimation(kSlot0StartIdx, kSlot0EndIdx).withSlot(0));
          break;
        case Twinkle:
          m_candle.setControl(
              new TwinkleAnimation(kSlot0StartIdx, kSlot0EndIdx).withSlot(0).withColor(LEDColor));
          break;
        case TwinkleOff:
          m_candle.setControl(
              new TwinkleOffAnimation(kSlot0StartIdx, kSlot0EndIdx)
                  .withSlot(0)
                  .withColor(LEDColor));
          break;
        case Fire:
          m_candle.setControl(new FireAnimation(kSlot0StartIdx, kSlot0EndIdx).withSlot(0));
          break;
      }
    }

    /* if the selection for slot 1 changes, change animations */
    final var anim1Selection = m_anim1Chooser.getSelected();
    if (m_anim1State != anim1Selection) {
      m_anim1State = anim1Selection;

      switch (m_anim1State) {
        default:
          strLEDAnimation = m_anim1State.toString();
        case Larson:
          m_candle.setControl(
              new LarsonAnimation(kSlot1StartIdx, kSlot1EndIdx).withSlot(1).withColor(LEDColor));
          break;
        case RgbFade:
          m_candle.setControl(new RgbFadeAnimation(kSlot1StartIdx, kSlot1EndIdx).withSlot(1));
          break;
        case SingleFade:
          m_candle.setControl(
              new SingleFadeAnimation(kSlot1StartIdx, kSlot1EndIdx)
                  .withSlot(1)
                  .withColor(LEDColor));
          break;
        case Strobe:
          m_candle.setControl(
              new StrobeAnimation(kSlot1StartIdx, kSlot1EndIdx).withSlot(1).withColor(LEDColor));
          break;
        case Fire:
          /* direction can be reversed by either the Direction parameter or switching start and end */
          m_candle.setControl(
              new FireAnimation(kSlot1StartIdx, kSlot1EndIdx)
                  .withSlot(1)
                  .withDirection(AnimationDirectionValue.Backward)
                  .withCooling(0.4)
                  .withSparking(0.5));
          break;
      }
    }
  }

  public void setLEDColor(RGBWColor color, boolean Cycle) {
    if (Cycle == true) {
      ColorCycle += 1;
      if (ColorCycle > 7) {
        ColorCycle = 1;
      }

      if (ColorCycle == 1) {
        color =
            new RGBWColor(
                LEDConstants.RED[0],
                LEDConstants.RED[1],
                LEDConstants.RED[2],
                LEDConstants.RED[3]); // Red
      } else if (ColorCycle == 2) {
        color = new RGBWColor(255, 255, 255, 0); // Yellow
      } else if (ColorCycle == 3) {
        color = new RGBWColor(255, 255, 255, 255); // White
      } else if (ColorCycle == 4) {
        color = new RGBWColor(255, 255, 128, 0); // Orange
      } else if (ColorCycle == 5) {
        color = new RGBWColor(255, 0, 255, 0); // Green
      } else if (ColorCycle == 6) {
        color = new RGBWColor(0, 0, 255, 255); // Blue
      } else if (ColorCycle == 7) {
        color = new RGBWColor(255, 0, 255, 255); // Purple
      }
    }
    LEDColor = color;
  }

  public void StopLEDSubsystem() {
    setLEDColor(new RGBWColor(0, 0, 0, 0), false);
  }

  public String getLEDStats() {
    return LEDSubsystem.strLEDAnimation + ", " + LEDSubsystem.LEDColor;
  }

  public class setLEDColor {}
}
