package org.firstinspires.ftc.teamcode.BioBuzzBot.TeleOp;

import static org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables.inAuto;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter;

@TeleOp(name = "Shooter Test Teleop")
public class ShooterTestTeleOp extends CommandOpMode {

    Shooter shooter;
    GamepadEx driver1, driver2;




    @Override
    public void initialize() {
        inAuto = false;
        CommandScheduler.getInstance().reset();
        shooter = new Shooter(hardwareMap);
        shooter.spinning = false;
        shooter.bangBangActive = true;

        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);

        shooter.speedOffset = 1100;
        shooter.hoodOffset = 1;

        new Trigger(() -> driver1.getRightY() > .1)
                .whileActiveOnce(new InstantCommand(() -> shooter.speedOffset -= 25));

        new Trigger(() -> driver1.getRightY() < -.1)
                .whileActiveOnce(new InstantCommand(() -> shooter.speedOffset += 25));

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.DPAD_UP))
                .whenInactive(() -> shooter.hoodOffset += .02);

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.DPAD_DOWN))
                .whenInactive(() -> shooter.hoodOffset -= .02);

        new Trigger(() -> driver2.getButton(GamepadKeys.Button.BACK))
                .toggleWhenActive(
                        new InstantCommand(()-> shooter.spinning = true),
                        new InstantCommand(()-> shooter.spinning = false)
                );

        telemetry.clearAll();
        telemetry.addLine("Ready To Go");
        telemetry.update();

    }

    @Override
    public void run() {
        super.run();

        telemetry.addData("Shooter Speed", shooter.getFlyWheelSpeed());
        telemetry.addData("Shooter Target", shooter.getTargetFlyWheelSpeed());
        telemetry.addData("Hood Position", shooter.getHoodPosition());


        telemetry.update();
    }

}