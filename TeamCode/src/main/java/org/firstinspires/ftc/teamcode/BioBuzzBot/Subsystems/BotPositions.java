package org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems;

import com.pedropathing.math.Pose;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.Map;
import com.bylazar.configurables.annotations.Configurable;
@Configurable
public final class BotPositions {
    public static final class Turret {
        public static final double TOTAL_SERVO_ROTATION_DEGREES = 450; //516.3636;
        public static final double TOTAL_RADIANS = Math.toRadians(TOTAL_SERVO_ROTATION_DEGREES);
        public static final double RADIANS_PER_SERVO_POSITION = TOTAL_RADIANS;
        public static final double SERVO_POSITION_PER_DEGREE = 1.0 / TOTAL_SERVO_ROTATION_DEGREES;
        public static final double OFFSET_X = 0.0;
        public static final double OFFSET_Y = 0.0;
        public static final double MAX_ROTATION_DEG = 200;
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

        public static final Map<org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset, Double> HOOD_PRESETS = Map.of(
                org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset.CLOSE, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset.MID, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset.FAR, 0.0
        );

        public static final Map<org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset, Double> FLYWHEEL_PRESETS = Map.of(
                org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset.CLOSE, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset.MID, 0.0,
                org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter.ShooterPreset.FAR, 0.0
        );

    }

    public static final class Transfer {
        public static double OPEN = 0, CLOSE = 0;
    }

    public static final class Ramp {
        public static double
                FRONT = 1,
                BACK = 0,
                RETRACT = .5;
    }


    public static final class Camera {

        // === Physical Mounting Geometry ===
        public static final double Z_OFFSET = 5.87;          // lens height above floor
        public static final double PITCH_RADIAN = Math.toRadians(25);
        public static final double Y_OFFSET = 8.64;                // forward/back offset
        public static final double X_OFFSET = 0.0;                // left/right offset

        // === Optical Properties ===
        public static final double FOV_HORIZONTAL_RAD = Math.toRadians(85.28);
        public static final double FOCAL_LENGTH_PIXELS = 460;     // TODO Tune
        public static final double BALL_DIAMETER_INCHES = 2.5;

        // === Camera Resolution ===
        public static final int RES_WIDTH = 1280;
        public static final int RES_HEIGHT = 720;

        // === Color Detection (HSV) ===
        // Yellow ball detection ranges
        public static final Scalar HSV_LOWER_YELLOW = new Scalar(20, 100, 100);
        public static final Scalar HSV_UPPER_YELLOW = new Scalar(35, 255, 255);

        // === Morphological Filtering ===
        public static final Size MORPH_KERNEL_SIZE = new Size(5, 5);

        // === Hough Circle Detection ===
        public static final double HOUGH_DP = 1.2;
        public static final double HOUGH_MIN_DIST = 30;
        public static final double HOUGH_PARAM1 = 100;
        public static final double HOUGH_PARAM2 = 20;
        public static final int HOUGH_MIN_RADIUS = 10;
        public static final int HOUGH_MAX_RADIUS = 80;

        // === Tracking Parameters ===
        public static final double MAX_ASSOCIATION_DIST = 80.0;
        public static final int MAX_MISSED_FRAMES = 10;
    }

    public static final class Hive {
        public static final Pose BLUE_AUDIENCE = new Pose(84, 54, 0);
        public static final Pose BLUE_SCORING = new Pose(84, 87, 0);
        public static final Pose RED_SCORING = new Pose(58, 87, 0);
        public static final Pose RED_AUDIENCE = new Pose(58, 54, 0);
    }


}
