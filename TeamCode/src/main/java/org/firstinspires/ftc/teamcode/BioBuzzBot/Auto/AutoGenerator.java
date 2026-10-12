package org.firstinspires.ftc.teamcode.BioBuzzBot.Auto;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Commands.LaunchSequenceCommand;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Camera;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Ramp;
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
            Ramp ramp,
            BioBuzzAuto.StartChoice startChoice,
            BioBuzzAuto.TransitChoice transitChoice
    ) {
        List<Command> seq = new ArrayList<>();
        if (startChoice == BioBuzzAuto.StartChoice.LOADING_ZONE_PARK) {
            return new SequentialCommandGroup(
                    //new FollowPathCommand(follower, AutoTrajectories.loadingZoneStartToPark())
            );
        }

        seq.add(new LaunchSequenceCommand(intake, transfer));
        seq.add(new InstantCommand(intake::in));
        if (startChoice == BioBuzzAuto.StartChoice.GARDEN) {
            // gardenStart to gardenIntake
        }else  if (startChoice == BioBuzzAuto.StartChoice.ALLIANCE_FLOWER){
            seq.add(new InstantCommand(ramp::deploy));
            //allianceFlowerStart to allianceFlowerIntake
        }
        seq.add(new InstantCommand(intake::stop));
        if (startChoice == BioBuzzAuto.StartChoice.GARDEN) {
            // gardenIntake to loadingZoneShootPosition
        }else  if (startChoice == BioBuzzAuto.StartChoice.ALLIANCE_FLOWER){
            seq.add(new InstantCommand(ramp::deploy));
            //allianceFlowerIntake to loadingZoneShootPosition
        }
        seq.add(new LaunchSequenceCommand(intake, transfer));
        seq.add(new InstantCommand(ramp::deploy));
        seq.add(new InstantCommand(intake::in));
        //loadingZoneShootPosition to loadingZoneFlowerIntake
        seq.add(new LaunchSequenceCommand(intake, transfer));
        //loadingZoneFlowerIntake to parkingPosition




        return new SequentialCommandGroup(seq.toArray(new Command[0]));
    }

}
