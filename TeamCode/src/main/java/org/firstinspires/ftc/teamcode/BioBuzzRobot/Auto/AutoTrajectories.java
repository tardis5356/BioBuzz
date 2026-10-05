package org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import static org.firstinspires.ftc.teamcode.BioBuzzRobot.Auto.BioBuzzAuto.StartChoice.GARDEN;

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
import java.util.ArrayList;
import java.util.List;

public class AutoTrajectories {



    public static Pose startPose;
    public static Pose savedPose;
    public static GlobalVariables.AColor aColor;

    public enum StartQuadrant {
        RED_AUDIENCE,
        BLUE_AUDIENCE,
        RED_SCORING,
        BLUE_SCORING
    }
    static StartQuadrant startQuadrant;


    private static final PoseFactory poseFactory = PoseFactory.degrees();


    public static final Pose loadingZoneStart = poseFactory.of(128.1, 8.82, 90);
    public static final Pose Park = poseFactory.of(133.2186, 38.0214, 90);

    public static Path loadingZoneStartToPark() {
        return line(loadingZoneStart, Park).constant(Park);
    }

    public static final Pose gardenStart = poseFactory.of(109.3, 133.2, 90);
    public static final Pose gardenIntake = poseFactory.of(133, 133.2, 0);
    public static final Pose LZFlower = poseFactory.of(88.8971, 11.0505, 106.4954);
    public static final Pose gardenToLZFlowerInsideControl1 = poseFactory.of(64, 133.4, 0);
    public static final Pose gardenToLZFlowerInsideControl2 = poseFactory.of(80.6032, 38.6628, 0);
    public static final Pose gardenToLZFlowerOutsideControl1 = poseFactory.of(87.2457, 113.0157, 0);
    public static final Pose gardenToLZFlowerOutsideControl2 = poseFactory.of(122.6489, 26.13, 0);
    public static final Pose gardenToLZFlowerOutsideSegment2Start = poseFactory.of(88.8971, 11.0505, 80.5637);
    public static final Pose gardenToLZFlowerOutsideSegment2End = poseFactory.of(88.8971, 11.0505, 105);



    //Garden Start Paths
    public static Path gardenIntakeToLZFlowerInside() {
        return curve(gardenIntake, gardenToLZFlowerInsideControl1, gardenToLZFlowerInsideControl2, LZFlower).reverseTangent();
    }
    public static Path gardenStartToGardenIntake() {
        return line(gardenStart, gardenIntake).tangent();
    }
    public static Path gardenIntakeToLZFlowerOutside() {
        return curve(gardenIntake, gardenToLZFlowerOutsideControl1, gardenToLZFlowerOutsideControl2, LZFlower).heading(Interpolator.piecewise().until(0.77, Interpolator.tangent.reverse()).until(1, Interpolator.linear(gardenToLZFlowerOutsideSegment2Start, gardenToLZFlowerOutsideSegment2End)));
    }

    //Flower Start Paths



    public Path reverse(Path originalPath) {

        // 1. Get start and end Poses directly from Path

        Pose start = new Pose(originalPath.curve.startPoint().x(), originalPath.curve.startPoint().y(), originalPath.get(0.0).heading());
        Pose end   = originalPath.endPose();

        // 2. Reverse geometry
        Path reversed;
        if (originalPath.curve instanceof Line) {
            reversed = line(end, start);
        }else if (originalPath.curve instanceof BezierCurve) {
                BezierCurve originalBezier = (BezierCurve) originalPath.curve;
                List<Vector2D> originalCps = originalBezier.getControlPoints();

                // 1. Create list of Poses for the reversed curve
                List<Pose> cpsPoses = new ArrayList<>();

                // First element: start Pose of reversed path (original endPose)
                cpsPoses.add(end);

                // Middle elements: intermediate control points in reverse order
                for (int i = originalCps.size() - 2; i >= 1; i--) {
                    Vector2D pt = originalCps.get(i);
                    cpsPoses.add(new Pose(pt.x(), pt.y(), 0));
                }

                // Last element: end Pose of reversed path (original startPose)
                cpsPoses.add(start);

                // 2. Create reversed Path using array of Poses
                reversed = curve(cpsPoses.toArray(new Pose[0]));
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

    public static void generateTrajectories(Follower follower, BioBuzzAuto.StartChoice startChoice) {

      if (aColor == GlobalVariables.AColor.BLUE){
          poseFactory.mirrorY(70.75);
          poseFactory.mirrorX(70.75);
      }

      switch(startChoice) {
          case GARDEN:
              startPose = gardenStart;
              follower.setPose(startPose);
              follower.update();
              break;
          case LOADING_ZONE_PARK:
              startPose = loadingZoneStart;
              follower.setPose(startPose);
              follower.update();
              break;

          default:
              throw new IllegalArgumentException("Invalid StartChoice: " + startChoice);
              //          case ALLIANCE_FLOWER:
//              startPose = ;
//              follower.setPose(startPose);
//              follower.update();
//              break;

      }






    }

}
