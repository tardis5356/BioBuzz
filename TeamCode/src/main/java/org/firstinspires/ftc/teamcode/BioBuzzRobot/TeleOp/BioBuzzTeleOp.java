package org.firstinspires.ftc.teamcode.BioBuzzRobot.TeleOp;

import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables.inAuto;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake.intake_State;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.LaunchSequenceCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing.Constants;

public class BioBuzzTeleOp extends CommandOpMode {

    Intake intake;
    Turret turret;
    GamepadEx driver1, driver2;
    Follower follower;

    @Override
    public void initialize(){
        inAuto = false;
        CommandScheduler.getInstance().reset();

        intake = new Intake(hardwareMap);
        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);
        follower = Constants.create(hardwareMap);
        follower.update();
        //INTAKE
        new Trigger(()->driver1.getButton(GamepadKeys.Button.A)&& intake_State!= Intake.Intake_State.IN).whenActive(
                new InstantCommand(intake::in)
        );

        new Trigger(()->driver1.getButton(GamepadKeys.Button.B) && intake_State != Intake.Intake_State.OUT).whenActive(
                new InstantCommand(intake::out)
        );

        new Trigger(()->(driver1.getButton(GamepadKeys.Button.A)|| driver1.getButton(GamepadKeys.Button.B))&& intake_State != Intake.Intake_State.STOP).whenActive(
                new InstantCommand(intake::stop)
        );
        //SHOOTER
        new Trigger(()->(driver1.getButton(GamepadKeys.Button.X))).whenActive(
                new LaunchSequenceCommand(intake)
        );
        //Camera

    }
    public void run(){
        CommandScheduler.getInstance().run();
        follower.update();
        turret.updateTurretTracking(follower, telemetry);
    }
}
