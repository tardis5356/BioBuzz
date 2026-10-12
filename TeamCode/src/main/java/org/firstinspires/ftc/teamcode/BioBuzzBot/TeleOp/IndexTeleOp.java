package org.firstinspires.ftc.teamcode.BioBuzzBot.TeleOp;

import static org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.AutoTrajectories.savedPose;
import static org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables.inAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Commands.IntakeToggleCommand;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Ramp;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Transfer;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Turret;

public class IndexTeleOp extends CommandOpMode {
    GlobalVariables.AColor aColor;
    Intake intake;
    Turret turret;
    Shooter shooter;
    Transfer transfer;
    Ramp ramp;

    GamepadEx driver1, driver2;
    Follower follower;
    double FB, LR, Rotation;
    double CURRENT_SPEED_MULTIPLIER;
    double FAST_SPEED_MULTIPLIER = 1;
    double SLOW_SPEED_MULTIPLIER = .5;

    @Override
    public void initialize() {
        inAuto = false;
        CommandScheduler.getInstance().reset();

        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        transfer = new Transfer(hardwareMap);
        turret = new Turret(hardwareMap);
        ramp = new Ramp(hardwareMap);

        CURRENT_SPEED_MULTIPLIER = FAST_SPEED_MULTIPLIER;
        shooter.spinning = true;

        driver1 = new GamepadEx(gamepad1);
        driver2 = new GamepadEx(gamepad2);


        follower = Constants.create(hardwareMap);

        turret.disableTurretTracking();
        follower.setPose(new Pose(70.75, 70.75, 270));
        Turret.setTargetPosition(0.5);
        follower.update();

        //INTAKE
        Trigger manualIntakeMode = new Trigger(() -> Intake.intakeMode == Intake.IntakeMode.MANUAL);

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.CROSS))
                .and(manualIntakeMode)
                .whenActive(new IntakeToggleCommand(intake, Intake.Intake_State.IN));

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.CIRCLE))
                .and(manualIntakeMode)
                .whenActive(new IntakeToggleCommand(intake, Intake.Intake_State.OUT));

        //GATE

        new Trigger(() -> (driver1.getButton(GamepadKeys.Button.TRIANGLE))).toggleWhenActive(
                new InstantCommand(transfer::openGate), new InstantCommand(transfer::closeGate)
        );

        //Turret
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.BACK))
                .toggleWhenActive(
                        new InstantCommand(turret::enableTurretTracking),
                        new InstantCommand(turret::disableTurretTracking)
                );

        new Trigger(() -> turret.TurretTrackingDisabled && driver1.getButton(GamepadKeys.Button.SQUARE))
                .toggleWhenActive(
                        new InstantCommand(() -> Turret.setTargetPosition(0)),
                        new InstantCommand(() -> Turret.setTargetPosition(.5))
                );

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.RIGHT_BUMPER))
                .whenInactive(() -> Turret.manualOffsetDegrees -= 2);

        new Trigger(() -> driver2.getButton(GamepadKeys.Button.LEFT_BUMPER))
                .whenInactive(() -> Turret.manualOffsetDegrees += 2);
        //Drive
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.RIGHT_STICK_BUTTON))
                .toggleWhenActive(
                        () -> CURRENT_SPEED_MULTIPLIER = SLOW_SPEED_MULTIPLIER,
                        () -> CURRENT_SPEED_MULTIPLIER = FAST_SPEED_MULTIPLIER
                );
        //Misc.
        new Trigger(() -> gamepad2.ps)
                .toggleWhenActive(
                        new InstantCommand(() -> aColor = GlobalVariables.AColor.RED),
                        new InstantCommand(() -> aColor = GlobalVariables.AColor.BLUE)
                );


        new Trigger(() -> driver1.getButton(GamepadKeys.Button.DPAD_UP))
                .toggleWhenActive(new InstantCommand(ramp::deploy), new InstantCommand(ramp::retract));


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
