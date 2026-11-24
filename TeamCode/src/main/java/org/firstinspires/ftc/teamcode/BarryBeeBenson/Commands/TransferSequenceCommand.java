package org.firstinspires.ftc.teamcode.BarryBeeBenson.Commands;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBShooter;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBTransfer;

public class TransferSequenceCommand extends SequentialCommandGroup {
        public TransferSequenceCommand(BBBTransfer bbbtransfer) {
            addCommands(
                    //TODO Check the waits and sequence
                    new SequentialCommandGroup(
                            new InstantCommand(()-> bbbtransfer.autoWindmill = true),
                            new ParallelCommandGroup(
                                    new WaitCommand(400),
                                    new InstantCommand(bbbtransfer::on)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(1200),
                                    new InstantCommand(bbbtransfer::stop)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(400),
                                    new InstantCommand(bbbtransfer::on)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(1200),
                                    new InstantCommand(bbbtransfer::stop)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(400),
                                    new InstantCommand(bbbtransfer::on)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(1200),
                                    new InstantCommand(bbbtransfer::stop)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(400),
                                    new InstantCommand(bbbtransfer::on)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(1200),

                                    new InstantCommand(bbbtransfer::stop)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(400),
                                    new InstantCommand(bbbtransfer::on)
                            ),
                            new ParallelCommandGroup(
                                    new WaitCommand(1200),
                                    new InstantCommand(bbbtransfer::stop)
                            ),
            new InstantCommand(()-> bbbtransfer.autoWindmill = false)

                    )
            );
        }

    }





