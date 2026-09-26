package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing;

import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing.procedures.Tests;

public class Tuning {
    @Tuner
    public static Procedure tests() {
        return new Tests(hardwareMap -> new Mecanum(hardwareMap, Constants.driveConfig), (hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig)), null);
    }

    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }
}
