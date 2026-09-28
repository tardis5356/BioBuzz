package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.AutoTrajectories.startPose;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables.inAuto;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Turret.targetHive;

import com.pedropathing.follower.Follower;
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

public class BioBuzzAuto extends OpMode {


    private Follower follower;
    private Turret turret;
    private Intake intake;
    private Shooter shooter;
    private Transfer transfer;
    private Camera camera;
    private Ramp ramp;
    private GlobalVariables.AColor aColor;

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
    }

    @Override
    public void start() {

        AutoTrajectories.generateTrajectories(follower);
        SequentialCommandGroup auto = AutoGenerator.buildAuto(follower, intake, turret, shooter, transfer, camera, ramp);
        CommandScheduler.getInstance().schedule(
                auto
        );
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        follower.update();
        AutoTrajectories.savedPose = follower.pose();

        boolean scoring = follower.pose().y() > 72;
        targetHive = (aColor == GlobalVariables.AColor.BLUE)
                ? (scoring ? GlobalVariables.TargetHive.BLUE_SCORING : GlobalVariables.TargetHive.BLUE_AUDIENCE)
                : (scoring ? GlobalVariables.TargetHive.RED_SCORING  : GlobalVariables.TargetHive.RED_AUDIENCE);

        turret.updateTurretTracking(follower, telemetry);

    }

}

