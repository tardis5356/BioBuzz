package org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing.procedures.Tests;

public class Tuning {

    @Tuner
    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }


    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner((hardwareMap) -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig), (hardwareMap) -> new Mecanum(hardwareMap, Constants.driveConfig  ));
    }
    @Tuner
    public static Procedure tests() {
        return new Tests(hardwareMap -> new Mecanum(hardwareMap, Constants.driveConfig), (hardwareMap -> new PinpointLocalizer(hardwareMap, Constants.localizerConfig)), () -> new Foresight(Constants.foresightConfig));
    }
}