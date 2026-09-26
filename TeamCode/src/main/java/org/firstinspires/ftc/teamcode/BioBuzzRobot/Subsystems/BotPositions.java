package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;

import com.pedropathing.math.Pose;

import java.util.Map;

public final class BotPositions {
    public static final class Turret {
        public static final double TOTAL_DEGREES = 401.710526316;
        public static final double TOTAL_RADIANS = Math.toRadians(TOTAL_DEGREES);
        public static final double RADIANS_PER_SERVO_POSITION = TOTAL_RADIANS;
        public static final double SERVO_POSITION_PER_DEGREE = 1.0 / TOTAL_DEGREES;
        public static final double OFFSET_X = 0.0;
        public static final double OFFSET_Y = 0.0;
        public static final double MAX_ROTATION_DEG = TOTAL_DEGREES / 2.0;
    }

    public static final class Intake {
        public static double
                INTAKE_POWER = 1,
                OUTTAKE_POWER = -1;
        public static long
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

    public static final class Transfer {
        public static double OPEN = 0, CLOSE = 0;
    }

    public static final class Ramp {
        public static double
                FRONT = 0,
                BACK = 0,
                RETRACT = 0;
    }

    public static final class Hive {
        public static final Pose BLUE_AUDIENCE = new Pose(84, 54, 0);
        public static final Pose BLUE_SCORING = new Pose(84, 87, 0);
        public static final Pose RED_SCORING = new Pose(58, 87, 0);
        public static final Pose RED_AUDIENCE = new Pose(58, 54, 0);
    }


}
