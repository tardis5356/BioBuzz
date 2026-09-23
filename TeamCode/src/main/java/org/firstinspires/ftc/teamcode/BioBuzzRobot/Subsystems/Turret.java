package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;


import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables.inAuto;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake.mI;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.BotPositions.Turret.TICKS_PER_DEGREE;
import static java.lang.Math.signum;

import com.pedropathing.controllers.PIDController;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Map;


public class Turret extends SubsystemBase {

    public static CRServo sT1, sT2, sT3;
    private final CRServo[] turretServos = {sT1, sT2, sT3};
    private final VoltageSensor voltageSensor;
    public static int manualOffset = 0;
    public static double turretError;

    public static int desiredTicks;
    private static double targetPositionTicks;

    private final PIDController pidController;
    public boolean PIDDisabled = false;
    public static double turretOffset = 0;
    public DigitalChannel bbT;

    public GoBildaPinpointDriver driver;


    public Turret(HardwareMap hardwareMap) {
        if (inAuto) {
            mI.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        }
        bbT = hardwareMap.get(DigitalChannel.class, "bbT");
        mI.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        pidController = new PIDController(BotPositions.Turret.KP, BotPositions.Turret.KI, BotPositions.Turret.KD);
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        bbT.setMode(DigitalChannel.Mode.INPUT);


        //TODO for servo direction
        sT1.setDirection(DcMotorSimple.Direction.FORWARD);
        sT2.setDirection(DcMotorSimple.Direction.FORWARD);
        sT3.setDirection(DcMotorSimple.Direction.FORWARD);

        manualOffset = 0;
    }

    // === BASIC CONTROLS ===

    public static double getCurrentPosition() {
        return mI.getCurrentPosition() + turretOffset;
    }

    public static double getTargetPosition() {
        return targetPositionTicks;
    }

    public static void setTargetPosition(double position) {
        targetPositionTicks = position;
    }

    @Override
    public void periodic() {
//TODO Test
//        if (bbT.getState()) {
//            turretOffset += mI.getCurrentPosition();
//        }

        turretError = (Math.abs(getCurrentPosition() - getTargetPosition()) / BotPositions.Turret.TICKS_PER_DEGREE);

        double servoPower;
        if (!PIDDisabled) {

            if (Math.abs(getCurrentPosition() - getTargetPosition()) / BotPositions.Turret.TICKS_PER_DEGREE > BotPositions.Turret.TOLERANCE_DEGREE) {
                servoPower = pidController.calculate(getCurrentPosition(), targetPositionTicks);
            } else servoPower = 0;

        } else servoPower = 0;

        setTurretServoPower((servoPower + signum(servoPower) * BotPositions.Turret.KS) * (12 / voltageSensor.getVoltage()));
    }

    public void setTurretServoPower(double power) {
        for (CRServo s : turretServos) {
            s.setPower(power);
        }
    }

    public void disablePID() {
        PIDDisabled = true;
    }

    public void enablePID() {
        PIDDisabled = false;
    }

    public double getTurretThetaRAD() {
        return (getCurrentPosition() * BotPositions.Turret.RADIANS_PER_TICK);
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
        desiredTicks = (int) Math.round(desiredTurretOnBotAngleRAD / BotPositions.Turret.RADIANS_PER_TICK)
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
                (getCurrentPosition() - desiredTicks) / TICKS_PER_DEGREE);

        telemetry.addData("TurretError",
                (desiredTicks - getCurrentPosition()) / TICKS_PER_DEGREE);
    }


}
