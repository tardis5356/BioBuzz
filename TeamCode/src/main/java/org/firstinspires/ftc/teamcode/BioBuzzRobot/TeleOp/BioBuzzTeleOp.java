package org.firstinspires.ftc.teamcode.BioBuzzRobot.TeleOp;

import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.AutoTrajectories.savedPose;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables.inAuto;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake.intake_State;
import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Ramp.rampState;


import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.IntakeToggleCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.LaunchSequenceCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Commands.RampToggleCommand;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Ramp;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Transfer;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing.Constants;

public class BioBuzzTeleOp extends CommandOpMode {
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

    double mFLPower, mFRPower, mBLPower, mBRPower;

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
        if (follower != null) {
            follower.setPose(savedPose);
            follower.update();
        }
        //INTAKE
        Trigger manualIntakeMode = new Trigger(() -> Intake.intakeMode == Intake.IntakeMode.MANUAL);

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.A))
                .and(manualIntakeMode)
                .whenActive(new IntakeToggleCommand(intake, Intake.Intake_State.IN));

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.B))
                .and(manualIntakeMode)
                .whenActive(new IntakeToggleCommand(intake, Intake.Intake_State.OUT));

        //RAMP
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.DPAD_UP))
                .whenActive(new RampToggleCommand(ramp, Ramp.Ramp_State.FRONT));

        new Trigger(() -> driver1.getButton(GamepadKeys.Button.DPAD_DOWN))
                .whenActive(new RampToggleCommand(ramp, Ramp.Ramp_State.BACK));

        //SHOOTER
        new Trigger(() -> (driver1.getButton(GamepadKeys.Button.X))).whenActive(
                new LaunchSequenceCommand(intake, transfer)
        );
        new Trigger(() -> driver2.getRightY() > .1)
                .whileActiveOnce(new InstantCommand(() -> shooter.speedOffset -= 25));

        new Trigger(() -> driver2.getRightY() < -.1)
                .whileActiveOnce(new InstantCommand(() -> shooter.speedOffset += 25));

        new Trigger(() -> driver2.getButton(GamepadKeys.Button.DPAD_UP))
                .whenInactive(() -> shooter.hoodOffset += .02);

        new Trigger(() -> driver2.getButton(GamepadKeys.Button.DPAD_DOWN))
                .whenInactive(() -> shooter.hoodOffset -= .02);

        //Camera

        //Turret
        new Trigger(() -> driver2.getButton(GamepadKeys.Button.BACK))
                .toggleWhenActive(
                        new InstantCommand(turret::disablePID),
                        new InstantCommand(() -> turret.enablePID())
                );

        new Trigger(() -> driver2.getButton(GamepadKeys.Button.RIGHT_BUMPER))
                .whenInactive(() -> Turret.manualOffset -= 200);

        new Trigger(() -> driver2.getButton(GamepadKeys.Button.LEFT_BUMPER))
                .whenInactive(() -> Turret.manualOffset += 200);
        //Drive
        new Trigger(() -> driver1.getButton(GamepadKeys.Button.RIGHT_BUMPER))
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

        telemetry.clearAll();
        telemetry.addLine("Ready To Go");
        telemetry.update();

    }

    public void run() {
        CommandScheduler.getInstance().run();
        driveRun();
        follower.update();
        turret.updateTurretTracking(follower, telemetry);
    }


    public void driveRun() {
        Rotation = -cubicScaling(gamepad1.left_trigger - gamepad1.right_trigger) * 0.75 * CURRENT_SPEED_MULTIPLIER;
        FB = cubicScaling(gamepad1.left_stick_y) * CURRENT_SPEED_MULTIPLIER;
        LR = cubicScaling(gamepad1.left_stick_x) * 1.2 * CURRENT_SPEED_MULTIPLIER;

       follower.manual(FB, LR, Rotation);


    }

    private double cubicScaling(float joystickValue) {
        //store 5% of the joystick value + 95% of the joystick value to the 3rd power
        double v = 0.05 * joystickValue + 0.95 * Math.pow(joystickValue, 3);
        if (joystickValue > 0.02)
            //if the joystick is positive, return positive .1 + the stored value
            return 0.1 + v;
        else if (joystickValue < -0.02)
            //if the joystick is negative, return -.1 plus the stored value
            return -0.1 + v;
            // there's a range where this won't do either, which is a good counter against stick drift (because you can never escape stick drift)
        else
            return 0;
    }

    private double driftLock(float stickValue) {
        if (stickValue > 0.02 || stickValue < -.02) {
            return stickValue;
        } else {
            return 0;
        }
    }
}
