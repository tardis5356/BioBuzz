package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.AutoTrajectories.startPose;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables.inAuto;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;
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
        SequentialCommandGroup auto = AutoGenerator.buildAuto(follower, intake, turret, shooter, transfer);
        CommandScheduler.getInstance().schedule(
                auto
        );
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        follower.update();
        AutoTrajectories.savedPose = follower.closestPose();
        turret.updateTurretTracking(follower, telemetry);
    }

}

