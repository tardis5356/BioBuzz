package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.AutoTrajectories.gardenStartToGardenIntake;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.AutoTrajectories.startPose;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.LaunchSequenceCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.PathCompletionMarkerCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.RampToggleCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Camera;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Ramp;
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
            Camera camera,
            Ramp ramp
    ) {
        List<Command> seq = new ArrayList<>();
       // if (startPose == AutoTrajectories.loadingZoneStart) {
            return new SequentialCommandGroup(
                    new FollowPathCommand(follower, AutoTrajectories.loadingZoneStartToPark(), true)
            );
     //   }
//        seq.add(new LaunchSequenceCommand(intake, transfer));
//        if (startPose == AutoTrajectories.gardenStart){
//            seq.add(new InstantCommand(intake::in));
//            seq.add(
//                    new ParallelCommandGroup(
//                            new FollowPathCommand(follower, gardenStartToGardenIntake()),
//                            new PathCompletionMarkerCommand(
//                                    new SequentialCommandGroup(
//                                            new InstantCommand(intake::stop),
//                                            new InstantCommand(ramp::deployBack)
//                                    ),
//                                    0.5,
//                                    follower
//                            )
//                    )
//
//            );
//        }



       // return new SequentialCommandGroup(seq.toArray(new Command[0]));
    }

}
