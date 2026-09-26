package org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Ramp;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Ramp.rampState;

public class RampToggleCommand extends CommandBase {

    private final Ramp ramp;
    private final Ramp.Ramp_State target;


    public RampToggleCommand(Ramp ramp, Ramp.Ramp_State target) {
        this.ramp = ramp;
        this.target = target;
    }

    @Override
    public void initialize() {

        Ramp.Ramp_State current = rampState;
        if (current == Ramp.Ramp_State.RETRACTED) {
            if (target == Ramp.Ramp_State.FRONT) ramp.deployFront();
            else ramp.deployBack();
        }else {
            ramp.retract();
        }

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
