package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;


import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.BotPositions.Turret.SERVO_POSITION_PER_DEGREE;


import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Map;


public class Turret extends SubsystemBase {

    public static Servo sT1, sT2, sT3;
    private final Servo[] turretServos = {sT1, sT2, sT3};

    public static int manualOffset = 0;
    public static double turretError;

    public static int desiredTicks;
    private static double targetPositionTicks;


    public boolean PIDDisabled = false;


    public Turret(HardwareMap hardwareMap) {
        manualOffset = 0;
    }

    // === BASIC CONTROLS ===

    public static double getCurrentPosition() {
        return sT1.getPosition();
    }


    public static double getTargetPosition() {
        return targetPositionTicks;
    }

    public static void setTargetPosition(double position) {
        targetPositionTicks = position;
    }

    @Override
    public void periodic() {
        setTurretServoPower(getTargetPosition());
    }

    public void setTurretServoPower(double position) {
        for (Servo s : turretServos) {
            s.setPosition(position);
        }
    }

    public void disablePID() {
        PIDDisabled = true;
    }

    public void enablePID() {
        PIDDisabled = false;
    }

    public double getTurretThetaRAD() {
        return (getCurrentPosition() * BotPositions.Turret.RADIANS_PER_SERVO_POSITION);
    }

    public double getTurretThetaDEG() {
        return (Math.toDegrees(getTurretThetaRAD()));
    }


    public static GlobalVariables.TargetHive targetHive;

    private static final Map<GlobalVariables.TargetHive, Pose> HIVE_POSES = Map.of(
            GlobalVariables.TargetHive.BLUE_AUDIENCE, BotPositions.Hive.BLUE_AUDIENCE,
            GlobalVariables.TargetHive.BLUE_SCORING, BotPositions.Hive.BLUE_SCORING,
            GlobalVariables.TargetHive.RED_SCORING, BotPositions.Hive.RED_SCORING,
            GlobalVariables.TargetHive.RED_AUDIENCE, BotPositions.Hive.RED_AUDIENCE
    );


    Pose hivePose;

    public void updateTurretTracking(Follower follower, Telemetry telemetry) {

        hivePose = HIVE_POSES.get(targetHive);
        double goalX = hivePose.x();
        double goalY = hivePose.y();


        // === GET ROBOT POSE FROM PEDRO LOCALIZER ===
        double robotX = follower.closestPose().x();    // Pedro stores raw doubles
        double robotY = follower.closestPose().y();
        double robotHeadingRad = follower.closestPose().heading();

        // === COMPUTE TURRET WORLD POSITION ===
        double turretFieldX =
                robotX + Math.cos(robotHeadingRad) * BotPositions.Turret.OFFSET_X
                        - Math.sin(robotHeadingRad) * BotPositions.Turret.OFFSET_Y;

        double turretFieldY =
                robotY + Math.sin(robotHeadingRad) * BotPositions.Turret.OFFSET_X
                        + Math.cos(robotHeadingRad) * BotPositions.Turret.OFFSET_Y;

        // === DESIRED FIELD ANGLE TO TARGET ===
        double desiredFieldTurretAngleRAD =
                Math.atan2(goalY - turretFieldY, goalX - turretFieldX) + Math.PI;

        // === CONVERT FIELD ANGLE → ROBOT-RELATIVE TURRET ANGLE ===
        double desiredTurretOnBotAngleRAD = (desiredFieldTurretAngleRAD - robotHeadingRad) % (2 * Math.PI);

        // === LIMIT TO MAX TURRET ANGLE (avoid cable wrap) ===
        if (desiredTurretOnBotAngleRAD > Math.toRadians(BotPositions.Turret.MAX_ROTATION_DEG)) {
            desiredTurretOnBotAngleRAD -= 2 * Math.PI;
        }

        // === CONVERT TO TICKS ===
        desiredTicks = (int) Math.round(desiredTurretOnBotAngleRAD / BotPositions.Turret.RADIANS_PER_SERVO_POSITION)
                + manualOffset;

        setTargetPosition(desiredTicks);

        // === UPDATE GLOBAL DISTANCE ===
        GlobalVariables.distanceFromTarget =
                Math.hypot(goalY - turretFieldY, goalX - turretFieldX);

        // === TELEMETRY ===
        telemetry.addData("Desired Turret On Field Theta:", desiredFieldTurretAngleRAD);
        telemetry.addData("Turret Theta (deg) Current Error",
                "%.2f   %.2f",
                Math.toDegrees(getTurretThetaRAD()),
                (getCurrentPosition() - desiredTicks) / SERVO_POSITION_PER_DEGREE);

        telemetry.addData("TurretError",
                (desiredTicks - getCurrentPosition()) / SERVO_POSITION_PER_DEGREE);
    }


}
