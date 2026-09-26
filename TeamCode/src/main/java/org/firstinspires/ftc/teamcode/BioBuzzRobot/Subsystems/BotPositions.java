package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;

import com.pedropathing.math.Pose;

import java.util.Map;

public final class BotPositions {

    // === ROBOT GEOMETRY ===
    public static final class Turret {
        public static double
                RADIANS_PER_SERVO_POSITION = 6.99877 / 100.0,
                SERVO_POSITION_PER_DEGREE = 100.0 / 401.0,
                OFFSET_X = 0.0, OFFSET_Y = 0.0,
                MAX_ROTATION_DEG = 400.0 / 2.0;
    }

    public static final class Intake {

        public static double
                INTAKE_POWER = 1,
                OUTTAKE_POWER = -1,
        EJECT_TIME_MS = 500;
    }

    public static final class Shooter {
        public static double
                KP = 0, KI = 0, KD = 0, KS = 0;

        public static final Map<org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset, Double> HOOD_PRESETS = Map.of(
                org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset.CLOSE, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset.MID, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset.FAR, 0.0
        );

        public static final Map<org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset, Double> FLYWHEEL_PRESETS = Map.of(
                org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset.CLOSE, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset.MID, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter.ShooterPreset.FAR, 0.0
        );

    }

    public static final class Storage {
        public static double OPEN = 0, CLOSE = 0;
    }

    // === FIELD POSITIONS ===
    public static final class Hive {
        public static final Pose BLUE_AUDIENCE = new Pose(84, 54, 0);
        public static final Pose BLUE_SCORING = new Pose(84, 87, 0);
        public static final Pose RED_SCORING = new Pose(58, 87, 0);
        public static final Pose RED_AUDIENCE = new Pose(58, 54, 0);
    }


}
