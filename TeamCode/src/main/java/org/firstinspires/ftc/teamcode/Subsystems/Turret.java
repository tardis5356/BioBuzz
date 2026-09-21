package org.firstinspires.ftc.teamcode.Subsystems;


import static org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase.getCurrentGameTagLibrary;

import static java.lang.Math.signum;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;

import java.util.Map;


public class Turret extends SubsystemBase {

    public static Servo sT1;
    public Servo sT2;
    public Servo sT3;
    public static double turretOffset = 0;
    public static int manualOffset = 0;
    public static double turretError;

    public static int desiredPosition;
    private static double targetPositionTicks;


    public int desiredTagID;


    public GoBildaPinpointDriver driver;


    public Turret(HardwareMap hardwareMap) {

        driver = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        //TODO Check Calibration For Pinpoint
        driver.setEncoderResolution(1 / 0.00197, DistanceUnit.INCH);

        manualOffset = 0;
    }

    // === BASIC CONTROLS ===
    //TODO TBD
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

    }


    public double getTurretThetaRAD() {
        return (getCurrentPosition() * TurretValues.RADIANS_PER_SERVO_POS);
    }

    public double getTurretThetaDEG() {
        return (Math.toDegrees(getTurretThetaRAD()));
    }



    GlobalVariables.TargetHive targetHive;

    private static final Map<GlobalVariables.TargetHive, Pose> HIVE_POSES = Map.of(
            GlobalVariables.TargetHive.BLUE_AUDIENCE, new Pose(84, 54, 0),
            GlobalVariables.TargetHive.BLUE_SCORING, new Pose(84, 87, 0),
            GlobalVariables.TargetHive.RED_SCORING, new Pose(58, 87, 0),
            GlobalVariables.TargetHive.RED_AUDIENCE, new Pose(58, 54, 0)
    );


    Pose hivePose;
    public void updateTurretTracking(Follower follower, Telemetry telemetry) {

        hivePose =  HIVE_POSES.get(targetHive);
        double goalX = hivePose.x();
        double goalY = hivePose.y();


        // === GET ROBOT POSE FROM PEDRO LOCALIZER ===
        double robotX = follower.closestPose().x();    // Pedro stores raw doubles
        double robotY = follower.closestPose().y();
        double robotHeadingRad = follower.closestPose().heading();

        // === COMPUTE TURRET WORLD POSITION ===
        double turretFieldX =
                robotX + Math.cos(robotHeadingRad) * TurretValues.OFFSET_X
                        - Math.sin(robotHeadingRad) * TurretValues.OFFSET_Y;

        double turretFieldY =
                robotY + Math.sin(robotHeadingRad) * TurretValues.OFFSET_X
                        + Math.cos(robotHeadingRad) * TurretValues.OFFSET_Y;

        // === DESIRED FIELD ANGLE TO TARGET ===
        double desiredFieldTurretAngleRAD =
                Math.atan2(goalY - turretFieldY, goalX - turretFieldX) + Math.PI;

        // === CONVERT FIELD ANGLE → ROBOT-RELATIVE TURRET ANGLE ===
        double desiredTurretOnBotAngleRAD = (desiredFieldTurretAngleRAD - robotHeadingRad) % (2 * Math.PI);

        // === LIMIT TO MAX TURRET ANGLE (avoid cable wrap) ===
        if (desiredTurretOnBotAngleRAD > Math.toRadians(TurretValues.MAX_ROTATION_DEG)) {
            desiredTurretOnBotAngleRAD -= 2 * Math.PI;
        }

        // === CONVERT TO TICKS ===
        desiredPosition = (int) Math.round(desiredTurretOnBotAngleRAD / TurretValues.RADIANS_PER_SERVO_POS)
                + manualOffset;

        setTargetPosition(desiredPosition);

        // === UPDATE GLOBAL DISTANCE ===
        GlobalVariables.distanceFromTarget =
                Math.hypot(goalY - turretFieldY, goalX - turretFieldX);

        // === TELEMETRY ===
        telemetry.addData("Desired Turret On Field Theta:", desiredFieldTurretAngleRAD);
        telemetry.addData("Turret Theta (deg) Current Error",
                "%.2f   %.2f",
                Math.toDegrees(getTurretThetaRAD()),
                (getCurrentPosition() - desiredPosition) / TurretValues.SERVO_POS_PER_DEG);

        telemetry.addData("TurretError",
                (desiredPosition - getCurrentPosition()) / TurretValues.SERVO_POS_PER_DEG);
    }


}
