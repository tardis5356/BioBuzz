package org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Camera;

public class GoToNearestBallCommand extends CommandBase {

    private final Camera camera;
    private final Follower follower;

    private Command pathCommand;

    public GoToNearestBallCommand(Camera camera, Follower follower) {
        this.camera = camera;
        this.follower = follower;
    }

    @Override
    public void initialize() {
        pathCommand = camera.goToNearestBall(follower);
        if (pathCommand != null) {
            pathCommand.schedule();
        }
    }

    @Override
    public boolean isFinished() {
        return pathCommand == null || follower.idle();
    }
}