package org.firstinspires.ftc.teamcode.BarryBeeBenson.Auto;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import java.util.ArrayList;
import java.util.List;

public class BBBAutoGenerator {

    public static SequentialCommandGroup buildAuto(
            Follower follower
//            Intake intake,
//            Shooter shooter,
//            Transfer transfer,
//            BioBuzzAuto.StartChoice startChoice,
//            BioBuzzAuto.TransitChoice transitChoice
    ) {

        return new SequentialCommandGroup(
                new FollowPathCommand(follower, BBBAutoTrajectories.gardenStartToScoringShoot()),
                new FollowPathCommand(follower, BBBAutoTrajectories.ScoringShootPosToGardenIntake()));
    }


}
//        List<Command> seq = new ArrayList<>();
//        if (startChoice == BioBuzzAuto.StartChoice.LOADING_ZONE_PARK) {
//            return new SequentialCommandGroup(
//                    new FollowPathCommand(follower, BBBAutoTrajectories.loadingZoneStartToPark())
//            );
//        }
//
//      //  seq.add(new LaunchSequenceCommand(intake, transfer));
////        seq.add(new InstantCommand(intake::in));
//        if (startChoice == BioBuzzAuto.StartChoice.GARDEN) {
//            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.gardenStartToScoringShoot()));
//        } else if (startChoice == BioBuzzAuto.StartChoice.ALLIANCE_FLOWER) {
//            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.allianceFlowerStartToAllianceFlowerIntake()));
//        }
////        seq.add(new InstantCommand(intake::stop));
//        if (startChoice == BioBuzzAuto.StartChoice.GARDEN) {
//            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.ScoringShootPosToGardenIntake()));
//        } else if (startChoice == BioBuzzAuto.StartChoice.ALLIANCE_FLOWER) {
//            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.allianceFlowerIntakeToLoadingZoneShoot));
//        }
////        seq.add(new LaunchSequenceCommand(intake, transfer));
////        seq.add(new InstantCommand(intake::in));
////        !seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.loadingZoneShootToLoadingZoneFlowerIntake()));
////        seq.add(new LaunchSequenceCommand(intake, transfer));
////        !seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.loadingFlowerIntakeToParkPosition()));
//
//
//        return new SequentialCommandGroup(seq.toArray(new Command[0]));
