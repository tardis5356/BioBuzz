package org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter;

public class LaunchSequenceCommand extends SequentialCommandGroup {
    public LaunchSequenceCommand(Intake intake) {
                addCommands(
                        //TODO Check the waits and sequence
                        new SequentialCommandGroup(
                                new InstantCommand(() -> Shooter.firing = true),
                                //new InstantCommand(storage::openGate),
                                new InstantCommand(intake::stop),
                                new WaitCommand(200),
                                new InstantCommand(() -> Shooter.bangBangActive = true),
                                new InstantCommand(intake::in),
                                new WaitCommand(450),
                               // new InstantCommand(storage::closeGate),
                                new InstantCommand(intake::stop),
                                new InstantCommand(() -> Shooter.bangBangActive = false),
                                new InstantCommand(() -> Shooter.firing = false)
                        )
                );
        }

    }




