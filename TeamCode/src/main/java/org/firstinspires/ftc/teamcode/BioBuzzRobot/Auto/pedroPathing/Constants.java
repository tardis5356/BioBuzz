package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.pedroPathing;


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
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static MecanumConfig driveConfig = new MecanumConfig(
            c -> {
                c.frontLeftName.set("mFL");
                c.backLeftName.set("mBL");
                c.frontRightName.set("mFR");
                c.backRightName.set("mBR");

                c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
            }
    );
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-6.989);
        c.yPodOffset.set(-2.098);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.17036584794092324); // rough adjustment
                Controller secondaryTranslationalForward = Controller.proportional(0.085);//0.063 // fine adjustment
                Controller primaryTranslationalLateral = Controller.proportional(15);
                Controller secondaryTranslationalLateral = Controller.proportional(1);//6.0848

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward)); //primary will switch into secondary within 2.5 inches
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.01716256259187282)); // speeding up
                c.brake.set(Controller.proportionalFeedforward(0.014588178203091898)); // breaking

                c.headingFeedback.set(Controller.proportional(2));//3.52));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04088243380659385, 0.004027883077724302));

                c.linearBrakeCoefficients.set(Matrix.diag(0.057244757744923755, 0.043453649770849735));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0010896421432415454, 0.001660373401628275));

                c.maxAchievableForwardVelocity.set(61.46250501506093);
                c.maxAchievableStrafeVelocity.set(53.508304101599194);
                c.naturalForwardDeceleration.set(37.49811265763097);
                c.naturalStrafeDeceleration.set(60.979629199444126);
            }
    );
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, driveConfig),
                new Foresight(foresightConfig)
        );
    }
}



