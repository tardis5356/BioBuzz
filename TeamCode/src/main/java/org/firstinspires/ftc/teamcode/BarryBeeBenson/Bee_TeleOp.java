package org.firstinspires.ftc.teamcode.BarryBeeBenson;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Bee_Tele")

public class Bee_TeleOp extends LinearOpMode {

    //Set up hardware components and variables

    DcMotor mFL, mFR, mBL, mBR;
    CRServo sIL, sIR, sI;
    DcMotorSimple mF, mI;


    double mFLPower, mFRPower, mBLPower, mBRPower, FB, LR, Rotation, flyWheelSpeed;

    @Override
    public void runOpMode() {
        //Initialize and hardware map
        mFL = hardwareMap.get(DcMotor.class, "mFL");
        mFR = hardwareMap.get(DcMotor.class, "mFR");
        mBL = hardwareMap.get(DcMotor.class, "mBL");
        mBR = hardwareMap.get(DcMotor.class, "mBR");
        mI = hardwareMap.get(DcMotorSimple.class, "mI");
        mF = hardwareMap.get(DcMotorSimple.class,"mF");
        sIL = hardwareMap.get(CRServo.class, "sIL");
        sIR = hardwareMap.get(CRServo.class, "sIR");
        sI = hardwareMap.get(CRServo.class, "sI");
        flyWheelSpeed = .5;

       mBR.setDirection(DcMotorSimple.Direction.REVERSE);
       mFL.setDirection(DcMotorSimple.Direction.REVERSE);
       mF.setDirection(DcMotorSimple.Direction.REVERSE);


        waitForStart();

        while (opModeIsActive()) {
            //Actual Logic Stuff

            FB = gamepad1.left_stick_y;
            LR = gamepad1.right_stick_x;
            Rotation = gamepad1.left_stick_x;

            mFLPower = FB + LR + Rotation;
            mFRPower = FB - LR - Rotation;
            mBLPower = FB - LR + Rotation;
            mBRPower = FB + LR - Rotation;

            mFL.setPower(mFLPower);
            mFR.setPower(mFRPower);
            mBL.setPower(mBLPower);
            mBR.setPower(mBRPower);

            if (gamepad1.x) {
                mF.setPower(flyWheelSpeed);
            } else {
                mF.setPower(0);
            }

//            if (gamepad1.x) {
//                mF.setPower(currentVelocity < flyWheelSpeed ? 1.0 : 0.0);
//            } else {
//                mF.setPower(0);
//            }

            if (gamepad1.a){
                sI.setPower(1);
            } else {
                sI.setPower(0);
            }
            if (gamepad1.dpadUpWasPressed()) {
                flyWheelSpeed += 0.05;
            }


            if (gamepad1.dpadDownWasPressed()){
                flyWheelSpeed -= 0.05;
            }
//
//            if (gamepad1.b) {
//                sIL.setPower(1);
//                sIR.setPower(1);
//            } else {
//                sIL.setPower(0);
//                sIR.setPower(0);
//            }



            mI.setPower(-gamepad1.left_trigger);

            sIL.setPower(-gamepad1.left_trigger);

            sIR.setPower(gamepad1.left_trigger);


            mI.setPower(gamepad1.right_trigger);

            sIL.setPower(gamepad1.right_trigger);

            sIR.setPower(-gamepad1.right_trigger);



            telemetry.addData("mFL get current position", "%7d",
                    mFL.getCurrentPosition());
            telemetry.addData("flyWheelSpeed", flyWheelSpeed);
            telemetry.update();
        }
    }
}
