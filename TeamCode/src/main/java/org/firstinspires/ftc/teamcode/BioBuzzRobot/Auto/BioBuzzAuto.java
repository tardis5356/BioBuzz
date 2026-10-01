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
    private GlobalVariables.AColor aColor;
    //Decision tree
    //AColor, StartPos, Intake Where?, Shoot Where?, Travel,

    //public static int[][] choices = new int[MAX_CYCLES][2];


    @Override
    public void init() {
        inAuto = true;
        CommandScheduler.getInstance().reset();
        follower = Constants.create(hardwareMap);


        turret = new Turret(hardwareMap);
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        transfer = new Transfer(hardwareMap);

    }

    @Override
    public void init_loop() {


        follower.setPose(startPose);
        handleInput();
        printEASITelemetry();

    }

     boolean audienceStartPos = false;

    private void handleInput() {
        // 1. Color Selection
        if (gamepad2.a) aColor = GlobalVariables.AColor.BLUE;
        if (gamepad2.b) aColor = GlobalVariables.AColor.RED;

        // 2. Position Selection
        if (gamepad2.dpad_up)   audienceStartPos = true;
        if (gamepad2.dpad_down) audienceStartPos = false;

        // 3. Resolve Quadrant
        startQuadrant = updateQuadrant(aColor, audienceStartPos);
    }

    private AutoTrajectories.StartQuadrant updateQuadrant(GlobalVariables.AColor color, boolean scoring) {
        if (color == GlobalVariables.AColor.BLUE) {
            return scoring ? AutoTrajectories.StartQuadrant.BLUE_SCORING
                    : AutoTrajectories.StartQuadrant.BLUE_AUDIENCE;
        } else {
            return scoring ? AutoTrajectories.StartQuadrant.RED_SCORING
                    : AutoTrajectories.StartQuadrant.RED_AUDIENCE;
        }
    }

    private void printEASITelemetry() {
        telemetry.addData("StartingQuadrant", startQuadrant);
        telemetry.addData("AllianceColor", aColor);
        telemetry.update();
    }

    @Override
    public void start() {

        AutoTrajectories.generateTrajectories(follower);
        SequentialCommandGroup auto = AutoGenerator.buildAuto(
                follower,
                intake,
                turret,
                shooter,
                transfer,
                camera
        );
        CommandScheduler.getInstance().schedule(
                auto
        );
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        follower.update();
        turret.updateTurretTracking(follower, telemetry);

        AutoTrajectories.savedPose = follower.pose();


        telemetry.update();
    }

}

