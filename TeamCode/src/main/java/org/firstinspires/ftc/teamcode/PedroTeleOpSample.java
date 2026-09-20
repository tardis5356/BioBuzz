package org.firstinspires.ftc.teamcode;


import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.util.TelemetryData;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp
public class PedroTeleOpSample extends CommandOpMode {
    Follower follower;
    TelemetryData telemetryData = new TelemetryData(telemetry);

    @Override
    public void initialize() {
        follower = Constants.create(hardwareMap);
        super.reset();

        follower.setHeading(270);
    }

    @Override
    public void run() {
        super.run();

        /* Robot-Centric Drive
        follower.setTeleOpDrive(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, true);
        */

        // Field-Centric Drive

        follower.update();

        telemetryData.addData("X", follower.closestPose().x());
        telemetryData.addData("Y", follower.closestPose().y());
        telemetryData.addData("Heading", follower.closestPose().heading());
        telemetryData.update();
    }
}