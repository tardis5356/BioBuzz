package org.firstinspires.ftc.teamcode.Subsystems;


import static org.firstinspires.ftc.teamcode.Subsystems.BotPositions.Shooter.FLYWHEEL_PRESETS;
import static org.firstinspires.ftc.teamcode.Subsystems.BotPositions.Shooter.HOOD_PRESETS;

import com.pedropathing.controllers.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;

import org.firstinspires.ftc.teamcode.Util.InterpolatingDoubleTreeMap;

import java.util.Map;

public class Shooter extends SubsystemBase {
    private final VoltageSensor voltageSensor;

    //declare and define controllers
    PIDController velPIDController = new PIDController(BotPositions.Shooter.KP, BotPositions.Shooter.KI, BotPositions.Shooter.KD);
    SimpleMotorFeedforward velFFController = new SimpleMotorFeedforward(BotPositions.Shooter.KS, 0);

    public DcMotorEx mSR, mSL;
    public Servo sH;
    public static boolean bangBangActive;
    InterpolatingDoubleTreeMap HoodRegression = new InterpolatingDoubleTreeMap();
    public InterpolatingDoubleTreeMap WheelRegression = new InterpolatingDoubleTreeMap();
    public double targetFlyWheelSpeed;
    public double hoodOffset;

    public int speedOffset;

    public boolean targeting;

    public boolean spinning;

    double distanceFromTarget;

    public boolean shooterLock = false;

    public enum ShooterPreset {
        CLOSE,
        MID,
        FAR
    }
    public ShooterPreset shooterPreset = ShooterPreset.MID;

    ElapsedTime time;

    public Shooter(HardwareMap hardwareMap) {
        time = new ElapsedTime();
        time.reset();


        mSR = hardwareMap.get(DcMotorEx.class, "mSR");
        mSL = hardwareMap.get(DcMotorEx.class, "mSL");

        mSR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        mSL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        sH = hardwareMap.get(Servo.class, "sH");
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        mSL.setDirection(DcMotorSimple.Direction.REVERSE);


        //Hood and wheel points/data
//        HoodRegression.put(167.7, .18);
//
//        WheelRegression.put(167.7, 1425.);

        targeting = true;
    }

    @Override
    public void periodic() {


        setVel(WheelRegression.get(distanceFromTarget));

        double hoodPos;
        double flywheelTarget;

// === HOOD CONTROL ===
        if (targeting) {
            if (shooterLock) {
                hoodPos = BotPositions.Shooter.HOOD_PRESETS.get(shooterPreset) + hoodOffset;
            } else {
                hoodPos = HoodRegression.get(distanceFromTarget) + hoodOffset;
            }
        } else {
            hoodPos = hoodOffset;
        }

        sH.setPosition(hoodPos);

// === FLYWHEEL CONTROL ===
        if (spinning) {

            if (shooterLock) {
                flywheelTarget = BotPositions.Shooter.FLYWHEEL_PRESETS.get(shooterPreset) + speedOffset;
            } else {
                flywheelTarget = targetFlyWheelSpeed + speedOffset;
            }

            double power = bangBangActive
                    ? calculateBangBangFlyWheelPower(flywheelTarget)
                    : calculatePIDFlyWheelPower(flywheelTarget);

            mSL.setPower(power);
            mSR.setPower(power);

        } else {
            mSL.setPower(0);
            mSR.setPower(0);
        }

    }
    public void setVel(double tps) {
        targetFlyWheelSpeed = tps;
    }
    public double calculatePIDFlyWheelPower(double tps) {
        return (velPIDController.calculate(getFlyWheelSpeed(), tps) + velFFController.calculate(tps)) * 12.5 / voltageSensor.getVoltage();
    }
    public double calculateBangBangFlyWheelPower(double tps) {
        if (getFlyWheelSpeed() >= tps) {
            return 0;
        } else return 1;
    }
    public double getFlyWheelSpeed() {
        return mSR.getVelocity();
    }

    public void setTargetDistance(double d) {
        distanceFromTarget = d;
    }

    public double getTargetFlyWheelSpeed() {
        return targetFlyWheelSpeed;
    }


}
