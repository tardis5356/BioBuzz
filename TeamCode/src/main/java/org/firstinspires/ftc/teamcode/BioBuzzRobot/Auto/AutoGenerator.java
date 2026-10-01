package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.GoToNearestBallCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.LaunchSequenceCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Camera;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Transfer;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Turret;

import java.util.ArrayList;
import java.util.List;

public class AutoGenerator {

    public static SequentialCommandGroup buildAuto(
            Follower follower,
            Intake intake,
            Turret turret,
            Shooter shooter,
            Transfer transfer,
            Camera camera
    ) {
        List<Command> seq = new ArrayList<>();
        seq.add(
                new LaunchSequenceCommand(intake, transfer)
        );



//        seq.add(new GoToNearestBallCommand(camera, follower));
        return new SequentialCommandGroup(seq.toArray(new Command[0]));
    }

}
