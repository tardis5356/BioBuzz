package org.firstinspires.ftc.teamcode.TeleOp;

import static org.firstinspires.ftc.teamcode.Subsystems.Intake.intake_State;

import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;

public class BioBuzzTeleOp extends CommandOpMode {

    Intake intake;
GamepadEx driver1, driver2;

    @Override
    public void initialize(){
        intake = new Intake(hardwareMap);
        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);
        new Trigger(()->driver1.getButton(GamepadKeys.Button.A)&& intake_State!= Intake.Intake_State.IN).whenActive(
                new InstantCommand(intake::in)
        );
        new Trigger(()->driver1.getButton(GamepadKeys.Button.B) && intake_State != Intake.Intake_State.OUT).whenActive(
                new InstantCommand(intake::out)
        );
        new Trigger(()->(driver1.getButton(GamepadKeys.Button.A)|| driver1.getButton(GamepadKeys.Button.B))&& intake_State != Intake.Intake_State.STOP).whenActive(
                new InstantCommand(intake::stop)
        );
    }
    public void run(){



    }
}
