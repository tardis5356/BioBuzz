package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

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
            Transfer transfer
    ) {
        List<Command> seq = new ArrayList<>();

        return new SequentialCommandGroup(seq.toArray(new Command[0]));
    }

}
