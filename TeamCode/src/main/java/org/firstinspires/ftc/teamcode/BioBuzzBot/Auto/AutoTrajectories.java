package org.firstinspires.ftc.teamcode.BioBuzzBot.Auto;

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

import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AutoTrajectories {


    public static Pose startPose;
    public static Pose savedPose;
    public static GlobalVariables.AColor aColor;

    private static final PoseFactory poseFactory = PoseFactory.degrees();


    public static final Pose loadingZoneStart = poseFactory.of(128.1, 8.82, 90);
    public static final Pose Park = poseFactory.of(133.2186, 38.0214, 90);

    public static Path loadingZoneStartToPark() {
        return line(loadingZoneStart, Park).constant(Park);
    }

    private static final Pose gardenStart = poseFactory.of(133.375, 125.11, 90);

    private static final Pose gardenIntake = poseFactory.of(133.375, 132.69, 90);

    public static Path gardenStartToGardenIntake() {
        return line(gardenStart, gardenIntake).constant(gardenIntake);
    }

    private static final Pose loadingZoneShoot = poseFactory.of(93.5, 24, -90);
    private static final Pose gardenIntakeToLoadingZoneShootControl1_Inside = poseFactory.of(78.6157, 130.1729, 0);
    private static final Pose gardenIntakeToLoadingZoneShootControl2_Inside = poseFactory.of(69.4229, 62.1071, 0);
    private static final Pose gardenIntakeToLoadingZoneShootControl1_OutsideTravel = poseFactory.of(84.2757, 124.5129, 0);
    private static final Pose gardenIntakeToLoadingZoneShootControl2_OutsideTravel = poseFactory.of(129.2571, 51.5957, 0);

    //TODO Check Outside park PLS
    private static final Pose gardenIntakeToLoadingZoneShootControl1_OutsidePark = poseFactory.of(99.8586, 100.5829, 0);
    private static final Pose gardenIntakeToLoadingZoneShootControl2_OutsidePark = poseFactory.of(160, 59.2771, 0);


    private static final Pose gardenIntakeToLoadingZoneShootSegment1Heading = poseFactory.of(93.5, 24, 270);
    private static final Pose gardenIntakeToLoadingZoneShootSegment3Start = poseFactory.of(93.5, 24, -77.055);
    private static final Pose gardenIntakeToLoadingZoneShootSegment3End = poseFactory.of(93.5, 24, 270);


    private static final Pose allianceFlowerStart = poseFactory.of(133.375, 109.889, 90);
    private static final Pose allianceFlowerIntake = poseFactory.of(128, 93.5, 180);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootControl1_OutsidePark = poseFactory.of(119.2643, 65.8143, 0);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootControl2_OutsidePark = poseFactory.of(140, 54.0214, 0);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootControl1_OutsideTravel = poseFactory.of(88.1343, 90.0715, 0);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootControl2_OutsideTravel = poseFactory.of(125.85, 37.0414, 0);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootControl1_Inside = poseFactory.of(70, 145, 0);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootControl2_Inside = poseFactory.of(74.5057, 35.02, 0);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootSegment1Heading = poseFactory.of(93.5, 24, 180);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootSegment3Start = poseFactory.of(93.5, 24, -141.2266);
    private static final Pose allianceFlowerIntakeToLoadingZoneShootSegment3End = poseFactory.of(93.5, 24, 270);

    public static Path allianceFlowerStartToAllianceFlowerIntake() {
        return line(allianceFlowerStart, allianceFlowerIntake).linear(allianceFlowerStart, allianceFlowerIntake);
    }

    private static final Pose parkPosition = poseFactory.of(127.6443, 25.2714, -158.7596);
    private static final Pose loadingZoneFlowerIntakeToParkPositionSegment1Heading = poseFactory.of(127.6443, 25.2714, 270);

    private static final Pose loadingZoneFlowerIntake = poseFactory.of(93.5, 12, -90);

    public static Path loadingZoneShootToLoadingZoneFlowerIntake() {
        return line(loadingZoneShoot, loadingZoneFlowerIntake).tangent();
    }


    public static Path loadingFlowerIntakeToParkPosition() {
        return line(loadingZoneFlowerIntake, parkPosition).heading(Interpolator.piecewise().until(0.2401, Interpolator.constant(loadingZoneFlowerIntakeToParkPositionSegment1Heading)).until(1, Interpolator.tangent.reverse()));
    }



    public Path reverse(Path originalPath) {

        // 1. Get start and end Poses directly from Path

        Pose start = new Pose(originalPath.curve.startPoint().x(), originalPath.curve.startPoint().y(), originalPath.get(0.0).heading());
        Pose end = originalPath.endPose();

        // 2. Reverse geometry
        Path reversed;
        if (originalPath.curve instanceof Line) {
            reversed = line(end, start);
        } else if (originalPath.curve instanceof BezierCurve) {
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

    public static Path gardenIntakeToLoadingZoneShoot;

    public static Path allianceFlowerIntakeToLoadingZoneShoot;

    public static void generateTrajectories(Follower follower, BioBuzzAuto.StartChoice startChoice, BioBuzzAuto.TransitChoice transitChoice) {

        if (aColor == GlobalVariables.AColor.RED) {
            poseFactory.mirrorY(70.75);
            poseFactory.mirrorX(70.75);
        }
        Pose gardenIntakeToLoadingZoneShootControl1 = null;
        Pose gardenIntakeToLoadingZoneShootControl2 = null;

        Pose allianceFlowerIntakeToLoadingZoneShootControl1 = null;
        Pose allianceFlowerIntakeToLoadingZoneShootControl2 = null;

        switch (transitChoice) {
            case INSIDE:
                gardenIntakeToLoadingZoneShootControl1 = gardenIntakeToLoadingZoneShootControl1_Inside;
                gardenIntakeToLoadingZoneShootControl2 = gardenIntakeToLoadingZoneShootControl2_Inside;

                allianceFlowerIntakeToLoadingZoneShootControl1 = allianceFlowerIntakeToLoadingZoneShootControl1_Inside;
                allianceFlowerIntakeToLoadingZoneShootControl2 = allianceFlowerIntakeToLoadingZoneShootControl2_Inside;
                break;
            case OUTSIDE_TRAVEL:
                gardenIntakeToLoadingZoneShootControl1 = gardenIntakeToLoadingZoneShootControl1_OutsideTravel;
                gardenIntakeToLoadingZoneShootControl2 = gardenIntakeToLoadingZoneShootControl2_OutsideTravel;

                allianceFlowerIntakeToLoadingZoneShootControl1 = allianceFlowerIntakeToLoadingZoneShootControl1_OutsideTravel;
                allianceFlowerIntakeToLoadingZoneShootControl2 = allianceFlowerIntakeToLoadingZoneShootControl2_OutsideTravel;
                break;
            case OUTSIDE_PARK:
                gardenIntakeToLoadingZoneShootControl1 = gardenIntakeToLoadingZoneShootControl1_OutsidePark;
                gardenIntakeToLoadingZoneShootControl2 = gardenIntakeToLoadingZoneShootControl2_OutsidePark;

                allianceFlowerIntakeToLoadingZoneShootControl1 = allianceFlowerIntakeToLoadingZoneShootControl1_OutsidePark;
                allianceFlowerIntakeToLoadingZoneShootControl2 = allianceFlowerIntakeToLoadingZoneShootControl2_OutsidePark;
                break;
        }
        gardenIntakeToLoadingZoneShoot =
                curve(gardenIntake, gardenIntakeToLoadingZoneShootControl1, gardenIntakeToLoadingZoneShootControl2, loadingZoneShoot)
                        .heading(
                                Interpolator.piecewise()
                                        .until(0.2, Interpolator.constant(gardenIntakeToLoadingZoneShootSegment1Heading))
                                        .until(0.8, Interpolator.tangent)
                                        .until(1, Interpolator.linear(gardenIntakeToLoadingZoneShootSegment3Start, gardenIntakeToLoadingZoneShootSegment3End))
                        );

        allianceFlowerIntakeToLoadingZoneShoot =
             curve(allianceFlowerIntake, allianceFlowerIntakeToLoadingZoneShootControl1, allianceFlowerIntakeToLoadingZoneShootControl2, loadingZoneShoot)
                     .heading(
                             Interpolator.piecewise()
                                     .until(0.2, Interpolator.constant(allianceFlowerIntakeToLoadingZoneShootSegment1Heading))
                                     .until(0.8, Interpolator.tangent)
                                     .until(1, Interpolator.linear(allianceFlowerIntakeToLoadingZoneShootSegment3Start, allianceFlowerIntakeToLoadingZoneShootSegment3End)));




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
          case ALLIANCE_FLOWER:
              startPose = allianceFlowerStart;
              follower.setPose(startPose);
              follower.update();
              break;

          default:
              throw new IllegalArgumentException("Invalid StartChoice: " + startChoice);
      }





    }

}
