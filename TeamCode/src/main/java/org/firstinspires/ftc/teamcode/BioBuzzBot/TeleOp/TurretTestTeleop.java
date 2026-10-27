package org.firstinspires.ftc.teamcode.BioBuzzBot.TeleOp;

import static org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.AutoTrajectories.savedPose;
import static org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.BotPositions.Turret.TOTAL_SERVO_ROTATION_DEGREES;
import static org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables.inAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Turret;


@TeleOp(name="TurretTestTeleOp", group="Test")
public class TurretTestTeleop extends CommandOpMode {
    GlobalVariables.AColor aColor;
    Turret turret;

    GamepadEx driver1, driver2;
    Follower follower;
    double FB, LR, Rotation;
    double CURRENT_SPEED_MULTIPLIER = 1;
    double FAST_SPEED_MULTIPLIER = 1;
    double SLOW_SPEED_MULTIPLIER = .5;

    @Override
    public void initialize() {
        inAuto = false;
        CommandScheduler.getInstance().reset();

        turret = new Turret(hardwareMap);

        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);
turret.setTargetPosition(.5);

        follower = Constants.create(hardwareMap);

        turret.disableTurretTracking();
        follower.setPose(new Pose(70.75, 70.75, Math.toRadians(270)));

        follower.update();


        //Turret
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.BACK))
                .toggleWhenActive(
                        new InstantCommand(turret::enableTurretTracking),
                        new InstantCommand(turret::disableTurretTracking)
                );

        new Trigger(() -> turret.TurretTrackingDisabled && driver1.getButton(GamepadKeys.Button.SQUARE))
                .toggleWhenActive(
                        new InstantCommand(() -> Turret.setTargetPosition(0)),
                        new InstantCommand(() -> Turret.setTargetPosition(360/TOTAL_SERVO_ROTATION_DEGREES)) // want to rotate by 360
                );
//        new Trigger(() -> turret.TurretTrackingDisabled && driver1.getButton(GamepadKeys.Button.CIRCLE))
//                .whenActive(
//                        new InstantCommand(() -> Turret.setTargetPosition(.5))
//                );

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.RIGHT_BUMPER))
                .whenInactive(() -> Turret.manualOffsetDegrees -= 2);

        new Trigger(() -> driver2.getButton(GamepadKeys.Button.LEFT_BUMPER))
                .whenInactive(() -> Turret.manualOffsetDegrees += 2);
        //Misc.
        new Trigger(() -> gamepad2.ps)
                .toggleWhenActive(
                        new InstantCommand(() -> aColor = GlobalVariables.AColor.RED),
                        new InstantCommand(() -> aColor = GlobalVariables.AColor.BLUE)
                );


        telemetry.clearAll();
        telemetry.addLine("Ready To Go");
        telemetry.update();

    }

    @Override
    public void run() {
        super.run();

        BioBuzzTeleOp.driveRun(gamepad1, follower, CURRENT_SPEED_MULTIPLIER);
        follower.update();
        turret.updateTurretTracking(follower, telemetry);

        savedPose = null;

        telemetry.update();
    }
}
