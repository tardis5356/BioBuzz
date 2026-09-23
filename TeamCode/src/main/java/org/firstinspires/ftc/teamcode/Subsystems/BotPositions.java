package org.firstinspires.ftc.teamcode.Subsystems;

import com.pedropathing.math.Pose;

import java.util.Map;

public final class BotPositions {

    // === ROBOT GEOMETRY ===
    public static final class Turret {
        public static double
                RADIANS_PER_TICK = 0,
                TICKS_PER_DEGREE = 0,
                OFFSET_X = 0, OFFSET_Y = 0,
                KP = 0, KI = 0, KD = 0, KS = 0,
                TOLERANCE_DEGREE = 0,
                MAX_ROTATION_DEG = 200;
    }

    public static final class Intake {

    }

    public static final class Shooter {
        public static double
                KP = 0, KI = 0, KD = 0, KS = 0;

        public static final Map<org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset, Double> HOOD_PRESETS = Map.of(
                org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset.CLOSE, 0.7,
                org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset.MID,   0.44,
                org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset.FAR,   0.33
        );

        public static final Map<org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset, Double> FLYWHEEL_PRESETS = Map.of(
                org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset.CLOSE, 850.0,
                org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset.MID,   925.0,
                org.firstinspires.ftc.teamcode.Subsystems.Shooter.ShooterPreset.FAR,   1150.0
        );

    }

    // === FIELD POSITIONS ===
    public static final class Hive {
        public static final Pose BLUE_AUDIENCE = new Pose(84, 54, 0);
        public static final Pose BLUE_SCORING  = new Pose(84, 87, 0);
        public static final Pose RED_SCORING   = new Pose(58, 87, 0);
        public static final Pose RED_AUDIENCE  = new Pose(58, 54, 0);
    }


}
