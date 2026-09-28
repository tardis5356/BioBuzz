package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables;

public class AutoTrajectories {


    private static final PoseFactory poseFactory = PoseFactory.degrees();

    public static Pose startPose;
    public static Pose savedPose;
    GlobalVariables.AColor aColor;

    public enum StartQuadrant {
        RED_AUDIENCE,
        BLUE_AUDIENCE,
        RED_SCORING,
        BLUE_SCORING
    }

    static StartQuadrant startQuadrant;

    private final Pose start = poseFactory.of(78.9113, 133.1298, 90);
    private final Pose path1 = poseFactory.of(132.0193, 99.2815, -15);
    private final Pose path1Segment2Start = poseFactory.of(132.0193, 99.2815, -32.5114);
    private final Pose path1Segment2End = poseFactory.of(132.0193, 99.2815, -15);
    private final Pose point2 = poseFactory.of(84.0051, 103.6517, -5.2007);
    private final Pose point3 = poseFactory.of(132.1093, 132.7404, 75.8467);
    private final Pose point3Control1 = poseFactory.of(126.653, 110.2866, 0);
    private final Pose point4 = poseFactory.of(83.2357, 34.6343, 94.0991);
    private final Pose point4Control1 = poseFactory.of(71.2314, 134.2414, 0);
    private final Pose point4Control2 = poseFactory.of(82.7857, 41.5729, 0);
    private final Pose point5 = poseFactory.of(89.3229, 11.4857, 104.7329);

    public Path path1() {
        return line(start, path1).heading(Interpolator.piecewise().until(0.5, Interpolator.tangent).until(1, Interpolator.linear(path1Segment2Start, path1Segment2End)));
    }

    public Path path2() {
        return line(path1, point2).reverseTangent();
    }

    public Path path3() {
        return curve(point2, point3Control1, point3).tangent();
    }

    public Path path4() {
        return curve(point3, point4Control1, point4Control2, point4).reverseTangent();
    }

    public Path path5() {
        return line(point4, point5).reverseTangent();
    }

    public static void generateTrajectories(Follower follower) {

        switch (startQuadrant) {
            case RED_SCORING:
            case RED_AUDIENCE:
                poseFactory.mirrorY(70.75);
                poseFactory.mirrorX(70.75);
                break;
            case BLUE_SCORING:
                break;
            //the default is BLUE_AUDIENCE
            case BLUE_AUDIENCE:
                break;
        }



    }

}
