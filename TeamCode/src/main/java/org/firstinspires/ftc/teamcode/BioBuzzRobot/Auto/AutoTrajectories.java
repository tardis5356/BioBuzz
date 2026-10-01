package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.curves.Line;
import com.pedropathing.paths.curves.bezier.BezierCurve;
import com.pedropathing.paths.interpolator.Interpolator;

import org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems.GlobalVariables;

import java.lang.reflect.Method;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoTrajectories {



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


    //PASTE FROM HERE
    private static final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose gardenStart = poseFactory.of(78.9113, 133.1298, 90);
    private final Pose gardenStartToAllianceFlower = poseFactory.of(132.0193, 99.2815, -15);
    private final Pose gardenStartToAllianceFlowerSegment2Start = poseFactory.of(132.0193, 99.2815, -32.5114);
    private final Pose gardenStartToAllianceFlowerSegment2End = poseFactory.of(132.0193, 99.2815, -15);
    private final Pose allianceFlowerToGarden = poseFactory.of(132.3843, 130.2057, -90.6763);
    private final Pose gardenToLZFlower = poseFactory.of(89.3014, 10.2419, 106.7055);
    private final Pose gardenToLZFlowerControl1 = poseFactory.of(67.84, 136.4643, 0);
    private final Pose gardenToLZFlowerControl2 = poseFactory.of(80.6032, 38.6628, 0);
    private final Pose lZFlowerToLZ = poseFactory.of(121.3117, 29.8846, -148.4652);

    public Path gardenStartToAllianceFlower() {
        return line(gardenStart, gardenStartToAllianceFlower).heading(Interpolator.piecewise().until(0.8433, Interpolator.tangent).until(1, Interpolator.linear(gardenStartToAllianceFlowerSegment2Start, gardenStartToAllianceFlowerSegment2End)));
    }

    public Path allianceFlowerToGarden() {
        return line(gardenStartToAllianceFlower, allianceFlowerToGarden).reverseTangent();
    }

    public Path gardenToLZFlower() {
        return curve(allianceFlowerToGarden, gardenToLZFlowerControl1, gardenToLZFlowerControl2, gardenToLZFlower).reverseTangent();
    }

    public Path lZFlowerToLZ() {
        return line(gardenToLZFlower, lZFlowerToLZ).reverseTangent();
    }

    public Path reverse(Path originalPath) {

        // 1. Get start and end Poses directly from Path

        Pose start = new Pose(originalPath.curve.startPoint().x(), originalPath.curve.startPoint().y(), originalPath.get(0.0).heading());
        Pose end   = originalPath.endPose();

        // 2. Reverse geometry
        Path reversed;
        if (originalPath.curve instanceof Line) {
            reversed = line(end, start);
        } else if (originalPath.curve instanceof BezierCurve) {
            BezierCurve originalBezier = (BezierCurve) originalPath.curve;

            List<Vector2D> cps = new ArrayList<>(originalBezier.getControlPoints());
            Collections.reverse(cps);

            reversed = curve(cps.toArray(new Vector2D[0]));
        } else {
            throw new IllegalArgumentException("Unsupported curve type: " + originalPath.curve.getClass().getName());
        }

        // 3. Reverse heading interpolation if originalPath has a heading interpolator
        if (hasHeading(originalPath)) {
            reversed.heading((curve, t) -> originalPath.heading(1.0 - t));
        } else {
            reversed.reverseTangent();
        }

        return reversed;
    }

    public static boolean hasHeading(Path path) {
        if (path == null) return false;
        try {
            Method method = Path.class.getDeclaredMethod("hasHeading");
            method.setAccessible(true);
            Boolean result = (Boolean) method.invoke(path);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
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
