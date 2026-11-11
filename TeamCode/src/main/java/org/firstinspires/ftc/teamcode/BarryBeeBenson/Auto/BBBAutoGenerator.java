package org.firstinspires.ftc.teamcode.BarryBeeBenson.Auto;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBIntake;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBShooter;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBTransfer;

import java.util.ArrayList;
import java.util.List;

import kotlin.time.Instant;

public class BBBAutoGenerator {

    public static SequentialCommandGroup buildAuto(
            Follower follower,
            BBBIntake bbbintake,
            BBBShooter bbbshooter,
            BBBTransfer bbbtransfer,
//            Wedge wedge,
            BBBAuto.StartChoice startChoice,
            BBBAuto.TransitChoice transitChoice
    ) {
        List<Command> seq = new ArrayList<>();
        if (startChoice == BBBAuto.StartChoice.LOADING_ZONE_PARK) {
            return new SequentialCommandGroup(
                    new FollowPathCommand(follower, BBBAutoTrajectories.loadingZoneStartToPark())
            );
        }

//        seq.add(new LaunchSequenceCommand(intake, transfer));
//        seq.add(new InstantCommand(intake::in));
        if (startChoice == BBBAuto.StartChoice.GARDEN) {
            seq.add(new ParallelCommandGroup(
                    new WaitCommand(5000),
                    new InstantCommand (() -> bbbintake.in()),
                    new InstantCommand(() -> bbbshooter.on()),
                    new InstantCommand(() -> bbbtransfer.on())
        )
            );
            seq.add(new InstantCommand(()->bbbtransfer.stop()));
            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.gardenStartToIntake()));
        }
//        seq.add(new InstantCommand(intake::stop));
        if (startChoice == BBBAuto.StartChoice.GARDEN) {
            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.gardenIntakeToScoringShoot()));
        }
////            seq.add(new InstantCommand(wedge::retract));
//        }
//        seq.add(new LaunchSequenceCommand(intake, transfer));
//        seq.add(new InstantCommand(wedge::deploy));
//        seq.add(new InstantCommand(intake::in));
//        new FollowPathCommand(follower, BBBAutoTrajectories.loadingZoneShootToLoadingZoneFlowerIntake());
//        seq.add(new LaunchSequenceCommand(intake, transfer));
        seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.scoringShootToGardenPark()));
//        seq.add(new InstantCommand(wedge::retract));


        return new SequentialCommandGroup(seq.toArray(new Command[0]));}

}
