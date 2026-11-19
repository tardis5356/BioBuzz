package org.firstinspires.ftc.teamcode.BarryBeeBenson;

import static org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBShooter.flyWheelSpeed;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;


import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBTransfer;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBIntake;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBShooter;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBWedge;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.Commands.TransferSequenceCommand;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.Commands.IntakeToggleCommand;


@TeleOp(name = "Bee_TeleOp")

    public class Bee_TeleOp extends LinearOpMode {

        //Set up hardware components and variables

        DcMotor mFL, mFR, mBL;
        DcMotorEx mBR;
        CRServo sIL, sIR, sI;
        Servo sW;
        DcMotorSimple mF, mI;
        BBBIntake bbbintake;
        BBBShooter bbbshooter;
        BBBTransfer bbbtransfer;
        BBBWedge bbbwedge;
        GamepadEx driver1, driver2;
        double currentVelocity;


        double mFLPower, mFRPower, mBLPower, mBRPower, FB, LR, Rotation;

        @Override
        public void runOpMode() {
            CommandScheduler.getInstance().reset();
            //Initialize and hardware map
            bbbintake = new BBBIntake(hardwareMap);
            bbbshooter = new BBBShooter(hardwareMap);
            bbbtransfer = new BBBTransfer(hardwareMap);
            bbbwedge = new BBBWedge(hardwareMap);

            mFL = hardwareMap.get(DcMotor.class, "mFL");
            mFR = hardwareMap.get(DcMotor.class, "mFR");
            mBL = hardwareMap.get(DcMotor.class, "mBL");
            mBR = hardwareMap.get(DcMotorEx.class, "mBR");
            mI = hardwareMap.get(DcMotorSimple.class, "mI");
            mF = hardwareMap.get(DcMotorSimple.class, "mF");
            sIL = hardwareMap.get(CRServo.class, "sIL");
            sIR = hardwareMap.get(CRServo.class, "sIR");
            sI = hardwareMap.get(CRServo.class, "sI");
            sW = hardwareMap.get(Servo.class, "sW");
            driver1 = new GamepadEx(gamepad1);
            driver2 = new GamepadEx(gamepad2);



            mBL.setDirection(DcMotorSimple.Direction.REVERSE);
            mFL.setDirection(DcMotorSimple.Direction.REVERSE);
            mF.setDirection(DcMotorSimple.Direction.REVERSE);
//SHOOTER
            new Trigger(() -> driver2.getButton(GamepadKeys.Button.X))
                    .toggleWhenActive(new InstantCommand(() -> bbbshooter.on()),new InstantCommand(() -> bbbshooter.stop()));

            new Trigger(() -> driver2.getRightY() > .1)
                .whileActiveOnce(new InstantCommand(() -> flyWheelSpeed -= 25));

        new Trigger(() -> driver2.getRightY() < -.1)
                .whileActiveOnce(new InstantCommand(() -> flyWheelSpeed += 25));

            //TRANSFER
            new Trigger(() -> driver2.getButton(GamepadKeys.Button.A))
                    .whenActive(new InstantCommand(() -> bbbtransfer.on()));
            new Trigger(() -> driver2.getButton(GamepadKeys.Button.B))
                    .whenActive(new InstantCommand(() -> bbbtransfer.stop()));
            new Trigger(() -> driver2.getButton(GamepadKeys.Button.Y))
                    .whenActive(new InstantCommand(() -> bbbtransfer.reverse()));
//            new Trigger(() -> driver2.getButton(GamepadKeys.Button.A))
//                    .whenActive(new TransferSequenceCommand(bbbtransfer));


            //INTAKE
            new Trigger(() -> driver1.getButton(GamepadKeys.Button.TRIANGLE))
                    .whenActive(new IntakeToggleCommand(bbbintake, BBBIntake.Intake_State.IN));

            new Trigger(() -> driver1.getButton(GamepadKeys.Button.CROSS))
                    .whenActive(new IntakeToggleCommand(bbbintake, BBBIntake.Intake_State.OUT));


            //WEDGE
//            new Trigger(() -> driver1.getButton(GamepadKeys.Button.LEFT_BUMPER))
//                    .whenInactive(new InstantCommand(() -> bbbwedge.up()))
//                    .whenActive(new InstantCommand(() -> bbbwedge.down()));
////



            waitForStart();

            while (opModeIsActive()) {

                CommandScheduler.getInstance().run();

                //Actual Logic Stuff

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

//            if (gamepad1.x) {
//                mF.setPower(flyWheelSpeed);
//            } else {
//                mF.setPower(0);
//            }
                currentVelocity = mBR.getVelocity();

                if (gamepad1.left_bumper){
                    bbbwedge.down();
                } else {
                    bbbwedge.up();
                }

                //SHOOTER


//                if (gamepad1.x || gamepad2.x) {
//                    mF.setPower(currentVelocity < flyWheelSpeed ? 1.0 : 0.0);
//                } else {
//                    mF.setPower(0);
//                }

//                if (gamepad1.a || gamepad2.a){
//                    sI.setPower(1);
//                } else {
//                    sI.setPower(0);
//                }
//                if (gamepad1.dpadUpWasPressed()) {
//                    flyWheelSpeed += 50;
//                }

//
//                if (gamepad1.dpadDownWasPressed()){
//                    flyWheelSpeed -= 50;
//                }

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
//
//
//                mI.setPower(-gamepad1.left_trigger);
//
//                sIL.setPower(-gamepad1.left_trigger);
//
//                sIR.setPower(gamepad1.left_trigger);
//
//
//                mI.setPower(gamepad1.right_trigger);
//
//                sIL.setPower(gamepad1.right_trigger);
//
//                sIR.setPower(-gamepad1.right_trigger);


                telemetry.addData("mFL get current position", "%7d",
                        mFL.getCurrentPosition());
                telemetry.addData("flyWheelSpeed", flyWheelSpeed);

                telemetry.addData("Servo pose",sW.getPosition());


                telemetry.update();
            }
        }

}
