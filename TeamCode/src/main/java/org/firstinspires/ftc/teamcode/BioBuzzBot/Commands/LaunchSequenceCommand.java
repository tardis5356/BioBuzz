package org.firstinspires.ftc.teamcode.BioBuzzBot.Commands;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Transfer;

public class LaunchSequenceCommand extends SequentialCommandGroup {
    public LaunchSequenceCommand(Intake intake, Transfer transfer) {
        addCommands(
                //TODO Check the waits and sequence
                new SequentialCommandGroup(
                        new InstantCommand(transfer::openGate),
                        new InstantCommand(intake::stop),
                        new WaitCommand(200),
                        new InstantCommand(() -> Shooter.bangBangActive = true),
                        new InstantCommand(intake::in),
                        new WaitCommand(450),
                        new InstantCommand(transfer::closeGate),
                        new InstantCommand(intake::stop),
                        new InstantCommand(() -> Shooter.bangBangActive = false)
                )
        );
    }

}




