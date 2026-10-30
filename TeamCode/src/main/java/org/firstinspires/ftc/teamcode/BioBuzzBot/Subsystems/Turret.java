package org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems;


import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Map;


public class Turret extends SubsystemBase {

    public static Servo sT1, sT2;
    private final Servo[] turretServos = {sT1};//, sT2};
    private static AnalogInput saT1;

    public static int manualOffsetDegrees = 0;

    public static double desiredTicks;
    private static double targetPosition;

    GlobalVariables.AColor aColor;
    static boolean scoring;

    public boolean TurretTrackingDisabled = false;


    public Turret(HardwareMap hardwareMap) {
        manualOffsetDegrees = 0;

        sT1 = hardwareMap.get(Servo.class, "sT1");
        //sT2 = hardwareMap.get(Servo.class, "sT2");

        saT1 = hardwareMap.get(AnalogInput.class, "saT1");


    }

    // === BASIC CONTROLS ===

    public static double getCurrentPosition() {
        return saT1.getVoltage()/saT1.getMaxVoltage();
    }


    public static double getErrorDEG() {
        return getTurretThetaDEG()-getTargetDEG();
    }

    public static double getTargetPosition() {
        return targetPosition;
    }

    public static double getTargetRAD() {
        return targetPosition * BotPositions.Turret.RADIANS_PER_SERVO_POSITION;
    }

    public static double getTargetDEG() {
        return Math.toRadians(getTargetRAD());
    }

    public static void setTargetPosition(double position) {
        targetPosition = position;
    }

    @Override
    public void periodic() {
        setTurretServoPosition(getTargetPosition());
    }

    public void setTurretServoPosition(double position) {
//        for (Servo s : turretServos) {
            sT1.setPosition(position);
//        }
    }

    public void disableTurretTracking() {
        TurretTrackingDisabled = true;
    }

    public void enableTurretTracking() {
        TurretTrackingDisabled = false;
    }

    public static double getTurretThetaRAD() {
        return (getCurrentPosition() * BotPositions.Turret.RADIANS_PER_SERVO_POSITION);
    }

    public static double getTurretThetaDEG() {
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

         scoring = follower.pose().y() > 70.75;

        targetHive = (aColor == GlobalVariables.AColor.BLUE)
                ? (scoring ? GlobalVariables.TargetHive.BLUE_SCORING : GlobalVariables.TargetHive.BLUE_AUDIENCE)
                : (scoring ? GlobalVariables.TargetHive.RED_SCORING : GlobalVariables.TargetHive.RED_AUDIENCE);

        hivePose = HIVE_POSES.get(targetHive);
        double goalX = hivePose.x();
        double goalY = hivePose.y();


        // === GET ROBOT POSE FROM PEDRO LOCALIZER ===
        double robotX = follower.pose().x();    // Pedro stores raw doubles
        double robotY = follower.pose().y();
        double robotHeadingRad = follower.pose().heading();

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
        double desiredTurretOnBotAngleRAD = (desiredFieldTurretAngleRAD - robotHeadingRad + Math.toRadians(manualOffsetDegrees)) % (2 * Math.PI);

        // === LIMIT TO MAX TURRET ANGLE (avoid cable wrap) ===
        if (desiredTurretOnBotAngleRAD > Math.toRadians(BotPositions.Turret.MAX_ROTATION_DEG)) {
            desiredTurretOnBotAngleRAD -= 2 * Math.PI;
        }

        // === CONVERT TO TICKS ===
        desiredTicks = (desiredTurretOnBotAngleRAD / BotPositions.Turret.RADIANS_PER_SERVO_POSITION) +.5;//+.5 to account for the 0 position being straight


        if (!TurretTrackingDisabled) {
            setTargetPosition(desiredTicks);
        } else {
           // setTargetPosition(.5);
        }


        // === UPDATE GLOBAL DISTANCE ===
        GlobalVariables.distanceFromTarget =
                Math.hypot(goalY - turretFieldY, goalX - turretFieldX);

        // === TELEMETRY ===
        telemetry.addData("targetHive", targetHive);
        telemetry.addData("Desired Turret On Field Theta:", Math.toDegrees(desiredFieldTurretAngleRAD));
        telemetry.addData("ServoPosition", sT1.getPosition());
        telemetry.addData("AnalogInputPosition", getCurrentPosition());

    }


}
