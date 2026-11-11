package org.firstinspires.ftc.teamcode.BarryBeeBenson;

import static org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.AutoTrajectories.savedPose;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;



import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBTransfer;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBIntake;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBShooter;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBWedge;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.pedroPathing.Constants;


@TeleOp(name = "Bee_Tele")

public class Bee_Tele extends CommandOpMode {

    //Set up hardware components and variables

    BBBGlobalVariables.AColor aColor;
    BBBIntake bbbintake;
    BBBShooter bbbshooter;
    BBBTransfer bbbtransfer;
    BBBWedge bbbwedge;
    GamepadEx driver1, driver2;
    Follower follower;

    DcMotor mFL, mFR, mBL, mBR;

//    DcMotorSimple mI;

    double mFLPower, mFRPower, mBLPower, mBRPower, FB, LR, Rotation; //flyWheelSpeed = 2400;

    @Override
    public void initialize() {
//        inBBBAuto = false;
//        CommandScheduler.getInstance().reset();
        bbbintake = new BBBIntake(hardwareMap);
        bbbshooter = new BBBShooter(hardwareMap);
        bbbtransfer = new BBBTransfer(hardwareMap);
        mFL = hardwareMap.get(DcMotor.class, "mFL");
        mFR = hardwareMap.get(DcMotor.class, "mFR");
        mBL = hardwareMap.get(DcMotor.class, "mBL");
        mBR = hardwareMap.get(DcMotorEx.class, "mBR");

        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);

        follower = Constants.create(hardwareMap);
        if (savedPose != null) {
            follower.setPose(savedPose);
            follower.update();
        } else {
            follower.setPose(new Pose(70.75, 70.75, 270));
            follower.update();
        }



        //INTAKE
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.RIGHT_BUMPER))
                .whenActive(new InstantCommand(() -> bbbintake.in()))
                .whenInactive(new InstantCommand(() -> bbbintake.stop()));
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.RIGHT_BUMPER))
                .whenActive(new InstantCommand(() -> bbbintake.out()))
                .whenInactive(new InstantCommand(() -> bbbintake.stop()));

        //TRANSFER
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.A))
                .whenActive(new InstantCommand(() -> bbbtransfer.on()))
                .whenInactive(new InstantCommand(() -> bbbtransfer.stop()));
        //SHOOTER
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.A))
                .whenActive(new InstantCommand(() -> bbbshooter.on()))
                .whenInactive(new InstantCommand(() -> bbbshooter.stop()));

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.DPAD_UP))
                .whileActiveOnce(new InstantCommand(() -> bbbshooter.flyWheelSpeed += 50 ));

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.DPAD_DOWN))
                .whileActiveOnce(new InstantCommand(() -> bbbshooter.flyWheelSpeed -= 50 ));

        //WEDGE
        new Trigger(() -> driver2.getButton(GamepadKeys.Button.A))
                .whenActive(new InstantCommand(() -> bbbwedge.up()));
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.B))
                .whenActive(new InstantCommand(() -> bbbwedge.down()));




        telemetry.addData("mFL get current position", "%7d",
                mFL.getCurrentPosition());

        telemetry.addData("flyWheelSpeed", bbbshooter.flyWheelSpeed);

//        telemetry.addData("Servo pose",sW.getPosition());

        FB = -gamepad1.left_stick_y;
        LR = gamepad1.left_stick_x;
        Rotation = gamepad1.right_stick_x;

        mFLPower = FB + LR + Rotation;
        mFRPower = FB - LR - Rotation;
        mBLPower = FB - LR + Rotation;
        mBRPower = FB + LR - Rotation;

        mFL.setPower(mFLPower);
        mFR.setPower(mFRPower);
        mBL.setPower(mBLPower);
        mBR.setPower(mBRPower);

        telemetry.update();

    }




}




//            if (gamepad1.b){
//                sW.setPosition(1);
//            }else {
//                sW.setPosition(0);
//            }
//
//            if (gamepad2.b) {
//                mFL.setPower(1);
//
//            } else {
//                mFL.setPower(0);
//            }
//
//            if (gamepad2.a) {
//                mBL.setPower(1);
//
//            } else {
//                mBL.setPower(0);
//            }
//            if (gamepad2.x) {
//                mBR.setPower(1);
//
//            } else {
//                mBR.setPower(0);
//            }
//            if (gamepad2.y) {
//                mFR.setPower(1);
//
//            } else {
//                mFR.setPower(0);
//            }

