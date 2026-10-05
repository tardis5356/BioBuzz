package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.AutoTrajectories.startPose;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.AutoTrajectories.startQuadrant;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables.inAuto;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Turret.targetHive;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Camera;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Ramp;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Transfer;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing.Constants;
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
    //Decision tree
    //AColor, StartPos, Intake Where?, Shoot Where?, Travel,

    public enum StartChoice{
        GARDEN,
        LOADING_ZONE_PARK,
        ALLIANCE_FLOWER
    }

    StartChoice startChoice;


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

     boolean audienceStartPos = false;

    private void handleInput() {
        // 1. Color Selection
        if (gamepad2.a) aColor = GlobalVariables.AColor.BLUE;
        if (gamepad2.b) aColor = GlobalVariables.AColor.RED;

        // 2. Position Selection
        if (gamepad2.dpad_up)    startChoice = StartChoice.GARDEN;
        if (gamepad2.dpad_right) startChoice = StartChoice.ALLIANCE_FLOWER;
        if (gamepad2.dpad_down)  startChoice = StartChoice.LOADING_ZONE_PARK;

//        // 3. Resolve Quadrant
//        startQuadrant = updateQuadrant(aColor, audienceStartPos);
    }

//    private AutoTrajectories.StartQuadrant updateQuadrant(GlobalVariables.AColor color, boolean scoring) {
//        if (color == GlobalVariables.AColor.BLUE) {
//            return scoring ? AutoTrajectories.StartQuadrant.BLUE_SCORING
//                    : AutoTrajectories.StartQuadrant.BLUE_AUDIENCE;
//        } else {
//            return scoring ? AutoTrajectories.StartQuadrant.RED_SCORING
//                    : AutoTrajectories.StartQuadrant.RED_AUDIENCE;
//        }
//    }

    private void printEASITelemetry() {
        telemetry.addData("StartingQuadrant", startQuadrant);
        telemetry.addData("AllianceColor", aColor);
        telemetry.addLine("IsWorking");
        telemetry.update();
    }

    @Override
    public void start() {

        AutoTrajectories.generateTrajectories(follower, startChoice);
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
                ramp
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

