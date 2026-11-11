package org.firstinspires.ftc.teamcode.BarryBeeBenson.Auto;

//import static org.firstinspires.ftc.teamcode.BarryBeeBenson.Auto.BBBAutoTrajectories.startPose;
import static org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBGlobalVariables.inBBBAuto;

import static org.firstinspires.ftc.teamcode.BarryBeeBenson.Auto.BBBAutoTrajectories.startPose;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBGlobalVariables;


//import org.firstinspires.ftc.teamcode.BarryBeeBenson.Subsystems.Intake;
//import org.firstinspires.ftc.teamcode.BarryBeeBenson.Subsystems.Wedge;
//import org.firstinspires.ftc.teamcode.BarryBeeBenson.Subsystems.Shooter;
//import org.firstinspires.ftc.teamcode.BarryBeeBenson.Subsystems.Transfer;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBIntake;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBShooter;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBTransfer;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.pedroPathing.Constants;
@Autonomous(name = "BBB Auto"
        // , preselectTeleOp = "BioBuzz Teleop"
)
public class BBBAuto extends OpMode {


    private Follower follower;
    private BBBIntake bbbintake;
    private BBBShooter bbbshooter;
    private BBBTransfer bbbtransfer;
//    private Wedge wedge;

    private BBBGlobalVariables.AColor aColor;

    public enum StartChoice {
        GARDEN,
        LOADING_ZONE_PARK,
        ALLIANCE_FLOWER;

//        private static final StartChoice[] VALUES = values();
//        private final int start;
//
//        StartChoice(int start) {
//            this.start = start;
//        }
//
//        public int getChoice() {
//            return start;
//        }
//
//        public static StartChoice fromStartChoice(int start) {
//            if (start >= 0 && start < VALUES.length) {
//                return VALUES[start];
//            }
//            return GARDEN; // Default fallback
//        }
    }

    public enum TransitChoice{
        INSIDE,
        OUTSIDE_TRAVEL,
        OUTSIDE_PARK
    }

    StartChoice startChoice;
    TransitChoice transitChoice;


    @Override
    public void init() {
        inBBBAuto = true;
        CommandScheduler.getInstance().reset();
        follower = Constants.create(hardwareMap);


        bbbintake = new BBBIntake(hardwareMap);
        bbbshooter = new BBBShooter(hardwareMap);
        bbbtransfer = new BBBTransfer(hardwareMap);
//        wedge = new Wedge(hardwareMap);

    }

    @Override
    public void init_loop() {

        handleInput();
        printEASITelemetry();

    }

    private void handleInput() {
        // 1. Color Selection
        if (gamepad2.a) aColor = BBBGlobalVariables.AColor.BLUE;
        if (gamepad2.b) aColor = BBBGlobalVariables.AColor.RED;

        // 2. Position Selection
        if (gamepad2.dpad_up)    startChoice = StartChoice.GARDEN;
        if (gamepad2.dpad_right) startChoice = StartChoice.ALLIANCE_FLOWER;
        if (gamepad2.dpad_down)  startChoice = StartChoice.LOADING_ZONE_PARK;

        // 3. Transit Selection
        if (gamepad1.dpad_up)    transitChoice = TransitChoice.INSIDE;
        if (gamepad1.dpad_right) transitChoice = TransitChoice.OUTSIDE_TRAVEL;
        if (gamepad1.dpad_down)  transitChoice = TransitChoice.OUTSIDE_PARK;
    }



    private void printEASITelemetry() {
        telemetry.addData("AllianceColor", aColor);
        telemetry.addData("TransitChoice", transitChoice);
        telemetry.addData("StartChoice", startChoice);
        telemetry.update();
    }

    @Override
    public void start() {

        BBBAutoTrajectories.generateTrajectories(follower,startChoice, transitChoice);
        startPose = BBBAutoTrajectories.gardenStart;
        follower.setPose(startPose);
        follower.update();
        SequentialCommandGroup auto = BBBAutoGenerator.buildAuto(
                follower,
                bbbintake,
                bbbshooter,
                bbbtransfer,
                startChoice,
                transitChoice
//                wedge,
        );
        CommandScheduler.getInstance().schedule(
                auto
        );
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        follower.update();
        //turret.updateTurretTracking(follower, telemetry);

        BBBAutoTrajectories.savedPose = follower.pose();

        telemetry.addData("X pose", follower.localizer.pose().y());
        telemetry.addData("y pose", follower.localizer.pose().x());
        telemetry.addData("heading", follower.localizer.pose().heading());
        telemetry.update();
    }

}

