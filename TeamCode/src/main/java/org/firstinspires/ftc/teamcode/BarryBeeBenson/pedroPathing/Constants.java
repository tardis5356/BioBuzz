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
        c.ticksPerUnit.set(OptionalDouble.of((1046.35)));
        c.xPodOffset.set(-5.25);
        c.yPodOffset.set(6.);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(102.2884148598254);
                Controller secondaryTranslationalForward = Controller.proportional(37.79283609392243);
                Controller primaryTranslationalLateral = Controller.proportional(0.629299830669022);
                Controller secondaryTranslationalLateral = Controller.proportional(0.23250947223103813);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.03256697382899324));
                c.brake.set(Controller.proportionalFeedforward(0.027681927754644255));

                c.headingFeedback.set(Controller.proportional(4.532872748177396));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.044902723274009515, 0.0036150397088357917));

                c.linearBrakeCoefficients.set(Matrix.diag(0.08059036158049407, 0.04770840354299343));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0016844094507368703, 0.0028663708278111296));

                c.maxAchievableForwardVelocity.set(31.82264051320099);
                c.maxAchievableStrafeVelocity.set(27.072354367286827);
                c.naturalForwardDeceleration.set(35.91723285911429);
                c.naturalStrafeDeceleration.set(35.219522981886314);
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



