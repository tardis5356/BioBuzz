package org.firstinspires.ftc.teamcode.BarryBeeBenson.pedroPathing;


import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.BarryBeeBenson.pedroPathing.procedures.Tests;

import java.security.cert.PKIXRevocationChecker;
import java.util.OptionalDouble;

public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("mFL");
        c.frontRightName.set("mFR");
        c.backLeftName.set("mBL");
        c.backRightName.set("mBR");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.ticksPerUnit.set(OptionalDouble.of((1046.35/2)));
        c.xPodOffset.set(-5.25-1.3/2);
        c.yPodOffset.set(-6.+2.75/2+1.25/2+.25);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(3.5);
                Controller secondaryTranslationalForward = Controller.proportional(.8);
                Controller primaryTranslationalLateral = Controller.proportional(0.63);
                Controller secondaryTranslationalLateral = Controller.proportional(1.6);//.8

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.03186069652374693));
                c.brake.set(Controller.proportionalFeedforward(0.02708159204518489));

                c.headingFeedback.set(Controller.proportional(3.8237620810602317));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.046224317618899675, 0.0030736615432120757));

                c.linearBrakeCoefficients.set(Matrix.diag(0.07852974812101804, 0.06083182506944638));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0016077621187021685, 0.002439480191308048));

                c.maxAchievableForwardVelocity.set(32.56983345251392);
                c.maxAchievableStrafeVelocity.set(28.04196222375195);
                c.naturalForwardDeceleration.set(132.57642504210207);
                c.naturalStrafeDeceleration.set(34.08686396250734);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}



