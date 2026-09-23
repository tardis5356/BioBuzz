package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing;

import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing.procedures.Tests;

public class Tuning {
    @Tuner
    public static Procedure tests() {
        return new Tests(hardwareMap -> new Mecanum(hardwareMap, Constants.driveConfig), null, null);
    }
}
