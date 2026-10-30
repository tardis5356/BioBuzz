package org.firstinspires.ftc.teamcode.BioBuzzBot.Auto;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Commands.LaunchSequenceCommand;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Camera;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Wedge;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Transfer;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Turret;

import java.util.ArrayList;
import java.util.List;

public class AutoGenerator {

    public static SequentialCommandGroup buildAuto(
            Follower follower,
            Intake intake,
            Turret turret,
            Shooter shooter,
            Transfer transfer,
            Camera camera,
            Wedge wedge,
            BioBuzzAuto.StartChoice startChoice,
            BioBuzzAuto.TransitChoice transitChoice
    ) {
        List<Command> seq = new ArrayList<>();
        if (startChoice == BioBuzzAuto.StartChoice.LOADING_ZONE_PARK) {
            return new SequentialCommandGroup(
                    new FollowPathCommand(follower, AutoTrajectories.loadingZoneStartToPark())
            );
        }

        seq.add(new LaunchSequenceCommand(intake, transfer));
        seq.add(new InstantCommand(intake::in));
        if (startChoice == BioBuzzAuto.StartChoice.GARDEN) {
            new FollowPathCommand(follower, AutoTrajectories.gardenStartToGardenIntake());
        } else if (startChoice == BioBuzzAuto.StartChoice.ALLIANCE_FLOWER) {
            seq.add(new InstantCommand(wedge::deploy));
            new FollowPathCommand(follower, AutoTrajectories.allianceFlowerStartToAllianceFlowerIntake());
        }
        seq.add(new InstantCommand(intake::stop));
        if (startChoice == BioBuzzAuto.StartChoice.GARDEN) {
            new FollowPathCommand(follower, AutoTrajectories.gardenIntakeToLoadingZoneShoot);
        } else if (startChoice == BioBuzzAuto.StartChoice.ALLIANCE_FLOWER) {
            seq.add(new InstantCommand(wedge::retract));
            new FollowPathCommand(follower, AutoTrajectories.allianceFlowerIntakeToLoadingZoneShoot);
        }
        seq.add(new LaunchSequenceCommand(intake, transfer));
        seq.add(new InstantCommand(wedge::deploy));
        seq.add(new InstantCommand(intake::in));
        new FollowPathCommand(follower, AutoTrajectories.loadingZoneShootToLoadingZoneFlowerIntake());
        seq.add(new LaunchSequenceCommand(intake, transfer));
        new FollowPathCommand(follower, AutoTrajectories.loadingFlowerIntakeToParkPosition());
        seq.add(new InstantCommand(wedge::retract));


        return new SequentialCommandGroup(seq.toArray(new Command[0]));
    }

}
