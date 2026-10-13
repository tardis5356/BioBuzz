package org.firstinspires.ftc.teamcode.BioBuzzBot.Auto;

import static org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.AutoTrajectories.startPose;
import static org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables.inAuto;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Camera;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Ramp;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Transfer;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing.Constants;
@Autonomous(name = "Decode Auto"
       // , preselectTeleOp = "BioBuzz Teleop"
)
public class BioBuzzAuto extends OpMode {


    private Follower follower;
    private Turret turret;
    private Intake intake;
    private Shooter shooter;
    private Transfer transfer;
    private Camera camera;
    private Ramp ramp;

    private GlobalVariables.AColor aColor;

    public enum StartChoice {
        GARDEN(0),
        LOADING_ZONE_PARK(1),
        ALLIANCE_FLOWER(2);

        private static final StartChoice[] VALUES = values();
        private final int start;

        StartChoice(int start) {
            this.start = start;
        }

        public int getChoice() {
            return start;
        }

        public static StartChoice fromStartChoice(int start) {
            if (start >= 0 && start < VALUES.length) {
                return VALUES[start];
            }
            return GARDEN; // Default fallback
        }
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
        inAuto = true;
        CommandScheduler.getInstance().reset();
        follower = Constants.create(hardwareMap);


//        turret = new Turret(hardwareMap);
//        intake = new Intake(hardwareMap);
//        shooter = new Shooter(hardwareMap);
//        transfer = new Transfer(hardwareMap);
//   //     camera = new Camera(hardwareMap);
//        ramp = new Ramp(hardwareMap);

    }

    @Override
    public void init_loop() {

        handleInput();
        printEASITelemetry();

    }

    private void handleInput() {
        // 1. Color Selection
        if (gamepad2.a) aColor = GlobalVariables.AColor.BLUE;
        if (gamepad2.b) aColor = GlobalVariables.AColor.RED;

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

        AutoTrajectories.generateTrajectories(follower, startChoice, transitChoice);
        startPose = AutoTrajectories.loadingZoneStart;
        follower.setPose(startPose);
        follower.update();
        SequentialCommandGroup auto = AutoGenerator.buildAuto(
                follower,
                intake,
                turret,
                shooter,
                transfer,
                camera,
                ramp,
                startChoice,
                transitChoice
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

        AutoTrajectories.savedPose = follower.pose();


        telemetry.update();
    }

}

