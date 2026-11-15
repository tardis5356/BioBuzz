package org.firstinspires.ftc.teamcode.BarryBeeBenson.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems.BBBIntake;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Intake;

public class IntakeToggleCommand extends CommandBase {

    private final BBBIntake intake;
    private final BBBIntake.Intake_State target;

    public IntakeToggleCommand(BBBIntake intake, BBBIntake.Intake_State target) {
        this.intake = intake;
        this.target = target;
    }

    @Override
    public void initialize() {

        BBBIntake.Intake_State current = BBBIntake.intake_State;
        if (current != target) {
            if (target == BBBIntake.Intake_State.IN) intake.in();
            else intake.out();
        }else{
            intake.stop();
        }

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}