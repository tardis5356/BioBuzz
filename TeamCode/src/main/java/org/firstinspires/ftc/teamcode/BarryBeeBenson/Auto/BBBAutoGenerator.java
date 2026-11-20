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
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBWedge;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.Commands.TransferSequenceCommand;

import java.util.ArrayList;
import java.util.List;

public class BBBAutoGenerator {

    public static SequentialCommandGroup buildAuto(
            Follower follower,
            BBBIntake bbbintake,
            BBBShooter bbbshooter,
            BBBTransfer bbbtransfer,
            BBBWedge bbbwedge,
            BBBAuto.StartChoice startChoice,
            BBBAuto.IntakeChoice intakeChoice
    ) {
        List<Command> seq = new ArrayList<>();
        if (startChoice == BBBAuto.StartChoice.LOADING_ZONE_PARK) {
            return new SequentialCommandGroup(
                    new FollowPathCommand(follower, BBBAutoTrajectories.loadingZoneStartToPark())
            );
        }




        if (startChoice == BBBAuto.StartChoice.ALLIANCE_FLOWER){
            seq.add(new ParallelCommandGroup(
                            new WaitCommand(7000),
//                    new InstantCommand (() -> bbbintake.in()),
                            new InstantCommand(() -> bbbshooter.on()),
                            new TransferSequenceCommand(bbbtransfer)
                    )
            );
//            seq.add(new InstantCommand(()->bbbtransfer.stop()));

            seq.add(new InstantCommand(() -> bbbshooter.stop()));
            seq.add(new FollowPathCommand(follower,BBBAutoTrajectories.gardenStartToFlowerIntakeMidpoint()));
            seq.add(new InstantCommand(() -> bbbwedge.down()));
            seq.add(new ParallelCommandGroup(
                    new FollowPathCommand(follower,BBBAutoTrajectories.flowerIntakeMidpointToFlowerIntakePose()),
                    new InstantCommand(() -> bbbintake.in()),
                    new WaitCommand(6500)
        ));
            seq.add(new InstantCommand(()->bbbintake.stop()));
            seq.add(new FollowPathCommand(follower,BBBAutoTrajectories.flowerIntakePoseToFlowerIntakeMidpoint()));
            seq.add(new InstantCommand(()->bbbwedge.up()));
            seq.add(new FollowPathCommand(follower,BBBAutoTrajectories.flowerIntakeMidpointToPark()));
        }
//        seq.add(new LaunchSequenceCommand(intake, transfer));
//        seq.add(new InstantCommand(intake::in));




        if (startChoice == BBBAuto.StartChoice.GARDEN) {
            seq.add(new ParallelCommandGroup(
                    new WaitCommand(7000),
//                    new InstantCommand (() -> bbbintake.in()),
                    new InstantCommand(() -> bbbshooter.on()),
                    new TransferSequenceCommand(bbbtransfer)
        )
            );
//            seq.add(new InstantCommand(()->bbbtransfer.stop()));
            seq.add(new InstantCommand(() -> bbbshooter.stop()));
            seq.add(new ParallelCommandGroup(
                    new WaitCommand(5000),
                    new FollowPathCommand(follower, BBBAutoTrajectories.gardenStartToIntake()),
                    new InstantCommand(() -> bbbintake.in())
            ));
            seq.add(new InstantCommand(bbbintake::stop));

            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.GardenIntakeToPark()));
        }

        if (intakeChoice == BBBAuto.IntakeChoice.GARDEN_INTAKE){
            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.gardenStartToHiveMidpoint()));
            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.hiveMidpointToIntakeMidpointAudience()));
            seq.add(new FollowPathCommand(follower, BBBAutoTrajectories.midpointIntakeToAudienceFlowerIntake()));
// flower intake back to audience scoring pose
        }

        if (intakeChoice == BBBAuto.IntakeChoice.FLOWER_SCORING){

        }

        if (intakeChoice == BBBAuto.IntakeChoice.FLOWER_AUDIENCE){

        }




        return new SequentialCommandGroup(seq.toArray(new Command[0]));}

}
