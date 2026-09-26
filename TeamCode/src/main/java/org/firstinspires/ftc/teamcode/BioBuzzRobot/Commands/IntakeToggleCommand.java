package org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;

public class IntakeToggleCommand extends CommandBase {

    private final Intake intake;
    private final Intake.Intake_State target;

    public IntakeToggleCommand(Intake intake, Intake.Intake_State target) {
        this.intake = intake;
        this.target = target;
    }

    @Override
    public void initialize() {

        Intake.Intake_State current = Intake.intake_State;
        if (current == Intake.Intake_State.STOP) {
            if (target == Intake.Intake_State.IN) intake.in();
            else intake.out();
        }else {
            intake.stop();
        }

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}