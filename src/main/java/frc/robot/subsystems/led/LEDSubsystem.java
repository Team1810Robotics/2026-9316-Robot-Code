package frc.robot.subsystems.led;

import com.ctre.phoenix6.configs.CANdleConfiguration;
import com.ctre.phoenix6.controls.ColorFlowAnimation;
import com.ctre.phoenix6.controls.EmptyAnimation;
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
import com.ctre.phoenix6.signals.LossOfSignalBehaviorValue;
import com.ctre.phoenix6.signals.RGBWColor;
import com.ctre.phoenix6.signals.StatusLedWhenActiveValue;
import com.ctre.phoenix6.signals.StripTypeValue;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// You can actually make up to 8 animation slots on this, but for the sake of simplicty, and because
// we dont need more, we are only using 1

public class LEDSubsystem extends SubsystemBase {

  private static CANdle m_candle;

  public LEDSubsystem() {
    m_candle = new CANdle(LEDConstants.CANDLE_ID);
    configureCANdle();
    // Slot 0 chooser
    m_anim0Chooser.setDefaultOption("None", AnimationType.None);
    m_anim0Chooser.addOption("ColorFlow", AnimationType.ColorFlow);
    m_anim0Chooser.addOption("Rainbow", AnimationType.Rainbow);
    m_anim0Chooser.addOption("Twinkle", AnimationType.Twinkle);
    m_anim0Chooser.addOption("TwinkleOff", AnimationType.TwinkleOff);
    m_anim0Chooser.addOption("Fire", AnimationType.Fire);
    m_anim0Chooser.setDefaultOption("None", AnimationType.None);
    m_anim0Chooser.addOption("Larson", AnimationType.Larson);
    m_anim0Chooser.addOption("RgbFade", AnimationType.RgbFade);
    m_anim0Chooser.addOption("SingleFade", AnimationType.SingleFade);
    m_anim0Chooser.addOption("Strobe", AnimationType.Strobe);
    m_anim0Chooser.addOption("Empty", AnimationType.None);
    // Publish to dashboard so it exists and can be selected
    edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putData("LED Anim Slot 0", m_anim0Chooser);
  }

  public static RGBWColor LEDColor =
      new RGBWColor(255, 0, 0, 0); // This Defaults to red and can be changed through a command

  private void configureCANdle() {
    CANdleConfiguration config = new CANdleConfiguration();

    config.LED.BrightnessScalar = frc.robot.subsystems.led.LEDConstants.LED_Brightness;
    config.LED.StripType =
        StripTypeValue.GRB; // I don't know who's bright idea ir was to make it GRB im sorry brotato
    config.LED.LossOfSignalBehavior = LossOfSignalBehaviorValue.DisableLEDs;

    config.CANdleFeatures.StatusLedWhenActive = StatusLedWhenActiveValue.Enabled;

    m_candle.setControl(new EmptyAnimation(0));

    m_candle.getConfigurator().apply(config);
  }

  public void setSolidColor(int[] color) {
    int r = color[0];
    int g = color[1];
    int b = color[2];

    RGBWColor rgbwColor = new RGBWColor(g, r, b);

    m_candle.setControl(new SolidColor(0, LEDConstants.NUM_LEDS).withColor(rgbwColor));
  }

  public void initLEDColor() {
    setSolidColor(new int[] {0, 255, 0});
  }

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

  public static String strLEDColor =
      ""; // Used for dashboard display and debugging, updated whenever the color is changed
  public static String strLEDAnimation =
      ""; // Used for dashboard display and debugging, updated whenever the animation is changed

  private static final int kSlot0StartIdx =
      0; // The starting index for the LEDs controlled by slot 0 (0-based index)
  private static final int kSlot0EndIdx =
      LEDConstants.NUM_LEDS; // The ending index for the LEDs controlled by slot 0 (0-based index)

  private AnimationType m_anim0State =
      AnimationType
          .None; // The current animation state for slot 0, used to prevent setting the same
  // animation multiple times and to track the current animation

  private static AnimationType animation =
      AnimationType
          .None; // The animation that is currently selected, used to set the animation in the
  // periodic method and to track the selected animation

  private static int ColorCycle =
      0; // Used to track the current color in the color cycle, cycles through the colors in this
  // order: Red, Orange, Yellow, Green, Blue, Purple, White
  private static int AnimationCycle =
      0; // Used to track the current animation in the animation cycle, cycles through the
  // animations in this order: ColorFlow, Rainbow, Twinkle, TwinkleOff, Fire, Larson,
  // RgbFade, SingleFade, Strobe, None

  private final SendableChooser<AnimationType> m_anim0Chooser =
      new SendableChooser<
          AnimationType>(); // The chooser for selecting the animation for slot 0, published to the
  // dashboard and used to select the animation through the dashboard

  private static AnimationType chosenAnim0 =
      AnimationType
          .None; // The animation that is currently selected for slot 0, used to set the animation

  // in the periodic method and to track the selected animation

  private boolean LEDCooldown = false; //Stops the LED idle from changing too often

  @Override
  public void
      periodic() { // This method is called once per scheduler run, used to update the LED animation
    // and color based on the current state and selections

    if (LEDConstants.IDLE && LEDCooldown == false) { // When the robot is idle, set the LEDs to a default color and animation
      setLEDColor( new RGBWColor(LEDConstants.PURPLE[0], LEDConstants.PURPLE[1], LEDConstants.PURPLE[2], 0),false); // Idle is PURPLE
      setLEDAnimation("ColorFlow", false); // Idle is ColorFlow
      LEDCooldown = true;
    } else {
      LEDCooldown = false;
    }

    /* if the selection for slot 0 changes, change animations */
    final var anim0Selection = chosenAnim0;
    if (m_anim0State != anim0Selection) {
      m_anim0State = anim0Selection;

      switch (m_anim0State) { // Set the animation based on the selection, default is empty
          // animation which just turns the LEDs off
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
        case Larson:
          m_candle.setControl(
              new LarsonAnimation(kSlot0StartIdx, kSlot0EndIdx).withSlot(0).withColor(LEDColor));
          break;
        case RgbFade:
          m_candle.setControl(new RgbFadeAnimation(kSlot0StartIdx, kSlot0EndIdx).withSlot(0));
          break;
        case SingleFade:
          m_candle.setControl(
              new SingleFadeAnimation(kSlot0StartIdx, kSlot0EndIdx)
                  .withSlot(0)
                  .withColor(LEDColor));
          break;
        case Strobe:
          m_candle.setControl(
              new StrobeAnimation(kSlot0StartIdx, kSlot0EndIdx).withSlot(0).withColor(LEDColor));
          break;
        case None:
          m_candle.setControl(new EmptyAnimation(0));
          break;
      }
    }
  }

  public static void setLEDColor(RGBWColor color, boolean Cycle) {
    if (Cycle) {
      ColorCycle += 1;
      if (ColorCycle > 7) {
        ColorCycle = 1;
      }

      if (ColorCycle
          == 1) { // Cycles through the colors in this order: Red, Orange, Yellow, Green, Blue,
        // Purple, White
        color =
            new RGBWColor(
                frc.robot.subsystems.led.LEDConstants.RED[0],
                frc.robot.subsystems.led.LEDConstants.RED[1],
                frc.robot.subsystems.led.LEDConstants.RED[2],
                0); // Red
      } else if (ColorCycle == 2) {
        color =
            new RGBWColor(
                frc.robot.subsystems.led.LEDConstants.ORANGE[0],
                frc.robot.subsystems.led.LEDConstants.ORANGE[1],
                frc.robot.subsystems.led.LEDConstants.ORANGE[2],
                0); // Orange
      } else if (ColorCycle == 3) {
        color =
            new RGBWColor(
                frc.robot.subsystems.led.LEDConstants.YELLOW[0],
                frc.robot.subsystems.led.LEDConstants.YELLOW[1],
                frc.robot.subsystems.led.LEDConstants.YELLOW[2],
                0); // Yellow
      } else if (ColorCycle == 4) {
        color =
            new RGBWColor(
                frc.robot.subsystems.led.LEDConstants.GREEN[0],
                frc.robot.subsystems.led.LEDConstants.GREEN[1],
                frc.robot.subsystems.led.LEDConstants.GREEN[2],
                0); // Green
      } else if (ColorCycle == 5) {
        color =
            new RGBWColor(
                frc.robot.subsystems.led.LEDConstants.BLUE[0],
                frc.robot.subsystems.led.LEDConstants.BLUE[1],
                frc.robot.subsystems.led.LEDConstants.BLUE[2],
                0); // Blue
      } else if (ColorCycle == 6) {
        color =
            new RGBWColor(
                frc.robot.subsystems.led.LEDConstants.PURPLE[0],
                frc.robot.subsystems.led.LEDConstants.PURPLE[1],
                frc.robot.subsystems.led.LEDConstants.PURPLE[2],
                0); // Purple
      } else if (ColorCycle == 7) {
        color =
            new RGBWColor(
                frc.robot.subsystems.led.LEDConstants.WHITE[0],
                frc.robot.subsystems.led.LEDConstants.WHITE[1],
                frc.robot.subsystems.led.LEDConstants.WHITE[2],
                0); // White
      }
    }
    strLEDColor = color.toString();
    LEDColor = color; // Set the LEDColor variable to the new color so that it can be used
    m_candle.setControl(new SolidColor(0, LEDConstants.NUM_LEDS).withColor(LEDColor));
  }

  public static void setLEDAnimation(String animationString, boolean Cycle) {
    System.out.println(animationString);
    animation = null;
    if (Cycle) { // Cycles through the animations in this order: ColorFlow, Rainbow, Twinkle,
      // TwinkleOff, Fire, Larson, RgbFade, SingleFade, Strobe, None
      AnimationCycle += 1;
      if (AnimationCycle > 10) {
        AnimationCycle = 1;
      }
      if (AnimationCycle == 1) {
        animation = AnimationType.ColorFlow;
      } else if (AnimationCycle == 2) {
        animation = AnimationType.Rainbow;
      } else if (AnimationCycle == 3) {
        animation = AnimationType.Twinkle;
      } else if (AnimationCycle == 4) {
        animation = AnimationType.TwinkleOff;
      } else if (AnimationCycle == 5) {
        animation = AnimationType.Fire;
      } else if (AnimationCycle == 6) {
        animation = AnimationType.Larson;
      } else if (AnimationCycle == 7) {
        animation = AnimationType.RgbFade;
      } else if (AnimationCycle == 8) {
        animation = AnimationType.SingleFade;
      } else if (AnimationCycle == 9) {
        animation = AnimationType.Strobe;
      } else if (AnimationCycle == 10) {
        animation = AnimationType.None;
      }
    } else { // Allows you to set the animation based on a string, used for commands and dashboard
      // selection
      if (animationString.equals("None")) {
        animation = AnimationType.None;
      } else if (animationString.equals("ColorFlow")) {
        animation = AnimationType.ColorFlow;
      } else if (animationString.equals("Rainbow")) {
        animation = AnimationType.Rainbow;
      } else if (animationString.equals("Twinkle")) {
        animation = AnimationType.Twinkle;
      } else if (animationString.equals("TwinkleOff")) {
        animation = AnimationType.TwinkleOff;
      } else if (animationString.equals("Fire")) {
        animation = AnimationType.Fire;
      } else if (animationString.equals("Larson")) {
        animation = AnimationType.Larson;
      } else if (animationString.equals("RgbFade")) {
        animation = AnimationType.RgbFade;
      } else if (animationString.equals("SingleFade")) {
        animation = AnimationType.SingleFade;
      } else if (animationString.equals("Strobe")) {
        animation = AnimationType.Strobe;
      }
    }

    if (animation != null) { // If the animation is valid, set it to the chosen animation
      chosenAnim0 = animation;
    } else { // If the animation is not valid, set it to None
      chosenAnim0 = AnimationType.None;
    }
  }

  public void
      StopLEDSubsystem() { // Turns off the LEDs, used for testing and when the robot is disabled
    setLEDColor(new RGBWColor(0, 0, 0, 0), false);
    setLEDAnimation("None", false);
  }

  public String
      getLEDStats() { // Returns the current LED animation and color as a string, used for debugging
    // and dashboard display
    System.out.println(
        "LED Animation: " + LEDSubsystem.strLEDAnimation + ", LED Color: " + LEDSubsystem.LEDColor);
    return "LED Animation: "
        + LEDSubsystem.strLEDAnimation
        + ", LED Color: "
        + LEDSubsystem.LEDColor;
  }
}

// Code made by Will Edwards(Freshman), help from Sam Bowling (Senior), Payton Gaultiey(Super Senior
// Mentor), Special thanks to: Jordan Shaw (Junior) for being Jordan.
