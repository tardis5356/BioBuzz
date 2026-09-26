package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static com.pedropathing.api.Paths.curve;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

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

    public static void generateTrajectories(Follower follower) {

        switch (startQuadrant) {
            case RED_SCORING:
                poseFactory.mirrorX(70.75);
            case RED_AUDIENCE:
                poseFactory.mirrorY(70.75);
                break;

            case BLUE_SCORING:
                poseFactory.mirrorX(70.75);
                break;
            //the default is BLUE_AUDIENCE
            case BLUE_AUDIENCE:
                break;
        }


    }

}
