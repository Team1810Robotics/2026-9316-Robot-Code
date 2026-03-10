[![.github/workflows/main.yml](https://github.com/Team1810Robotics/2024-robot-code/actions/workflows/main.yml/badge.svg)](https://github.com/Team1810Robotics/2024-robot-code/actions/workflows/main.yml)

# 2026 CUBATRONICS Robot Code

## Team 9316 - CUBATRONICS - code for the 2026 competition season

### Bot Named Shockwave

---
### Controller Bindings

#### Left Joystick - from Enoch

| Button         | Purpose                          |
| -----          | -----                            |
| Left Stick     | Swerve Drive                     |
| Right Stick    | Swerve Rotate                    |
| L Trigger      |Intake wheels Reverse             |
| L Bumper       |Intake wheels spin                |
| R Trigger      |Start shooter sequence            | 
| R Bumper       |Reset feild orientation           |
| X              |Intake Down                       |
| A              |Auto align (and hood move maybe:) |
| B              |Intake Up                         |
| Y              |                                  |
| POV Up         | Manual hood move up              |
| POV Down       | Manual hood move down            |
| POV Left       |                                  |
| POV Right      |                                  |

#### Driver Controller (Xbox Controller 0 - White) - from recent branch

| Button         | Purpose                              |
| -----          | -----                                |
| Left Stick     | Translation (forward/back, strafe)  |
| Right Stick X  | Rotation                             |
| Right Bumper   | Reset field-centric heading          |
| X (hold)       | Vision lock - PID rotation onto Apriltag |
| B              | Deploy intake out (arm to OUT_POSITION) |
| A              | Retract intake in (arm to IN_POSITION) |
| Left Trigger (hold) | Intake IN + LEDs orange         |
| Right Trigger (hold) | Full shoot sequence (flywheel + indexer) |
| D-Pad Down     | Intake eject (reverse wheels)        |
| D-Pad Right    | Hood up                              |
| D-Pad Left     | Hood down                            |
| Back Button    | Slow mode (~35% speed) + LEDs blue   |
| Start Button   | Fast mode (full speed) + LEDs green  |
| Y (hold)       | Climb command (testing/sysid)        |



#### Manipulator Controller (Xbox Controller 1)

| Button         | Purpose                              |
| -----          | -----                                |
| Right Trigger (hold) | Climb up (Extend)               |
| Left Trigger (hold)  | Climb down (Retract)           |
| B              | Cycle LED animation                  |

---

#### CAN

| ID    | Mechanism      | Being Controlled            | Controller     |
| ----- | -----          | -----                       | -----          |
| 01    | Front Right    | Drive Motor                 | TalonFX        |
| 02    | Front Right    | Steer Motor                 | TalonFX        |
| 03    | Back Right     | Drive Motor                 | TalonFX        |
| 04    | Back Right     | Steer Motor                 | TalonFX        |
| 05    | Back Left      | Drive Motor                 | TalonFX        |
| 06    | Back Left      | Steer Motor                 | TalonFX        |
| 07    | Front Left     | Drive Motor                 | TalonFX        |
| 08    | Front Left     | Steer Motor                 | TalonFX        |
| 11    | Intake         | Roller Motor                | TalonFX        |
| 13    | Flywheel       | Left Motor                  | TalonFX        |
| 14    | Hood           | Hood Motor                  | TalonFX        |
| 15    | Flywheel       | Right Motor                 | TalonFX        |
| 20    | LEDs           | CANdle                      | CANdle         |
| 30    | IMU            | Pigeon 2                    | Pigeon2        |
| 31    | Front Right    | CANcoder                    | CANcoder       |
| 32    | Back Right     | CANcoder                    | CANcoder       |
| 33    | Back Left      | CANcoder                    | CANcoder       |
| 34    | Front Left     | CANcoder                    | CANcoder       |

#### CAN (Additional Mechanisms)

| ID    | Mechanism      | Being Controlled            | Controller     |
| ----- | -----          | -----                       | -----          |
| 03    | Climb          | Motor 2                     | TalonFX        |
| 04    | Climb          | Motor 1                     | TalonFX        |

#### Relay

| Port |  Mechanism | Being Controlled | Controller |
| -----| -----      | -----            | -----      |
| 00   |            |                  |            |
| 01   |            |                  |            |
| 02   |            |                  |            |
| 03   |            |                  |            |

#### PWM

| Port  | Mechanism | Being Controlled | Controller |
| ----- | -----     | -----            | -----      |
| 00    |           |                  |            |
| 01    |           |                  |            |
| 02    |           |                  |            |
| 03    |           |                  |            |
| 04    |           |                  |            |
| 05    |           |                  |            |
| 06    |           |                  |            |
| 07    |           |                  |            |
| 08    |           |                  |            |
| 09    |           |                  |            |

#### DIO

|       | Mechanism |
| ----- | -----     |
| 00    |           |
| 01    | Intake Proximity Sensor Left |
| 02    | Intake Proximity Sensor Right |
| 03    |           |
| 04    |           |
| 05    | Flywheel Beam Break |
| 06    |           |
| 07    |           |
| 08    |           |
| 09    |           |

#### Analog In

|           | Mechanism |
| -----     | -----     |
| 00        |           |
| 01        |           |
| 02        |           |
| 03        |           |

