package org.firstinspires.ftc.teamcode.BarryBeeBenson.Auto;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBGlobalVariables;

public class BBBAutoTrajectories {


    public static Pose startPose;
    public static Pose savedPose;
    public static BBBGlobalVariables.AColor aColor;

    private static final PoseFactory poseFactory = PoseFactory.degrees();

//gardenStart-gardenIntake-park
    public static final Pose gardenStart = poseFactory.of(84.65, 130.9, 90);
    public static final Pose gardenIntakeControl1 = poseFactory.of(120.5, 117, 0);
    public static final Pose gardenIntake = poseFactory.of(131, 130, 90);
    public static final Pose gardenIntakeFinal = poseFactory.of(131, 132, 95);
    public static final Pose audienceFlower = poseFactory.of(93.5,7.5, 270);

    public static Path gardenStartToIntake() {
        return Paths.curve(gardenStart, gardenIntakeControl1, gardenIntake).linear(gardenStart, gardenIntake);
    }


    public static final Pose gardenParkPose = poseFactory.of(125, 41, 270);
    public static final Pose gardenParkControl2 = poseFactory.of(110, 98, 90);

    public static Path GardenIntakeToPark() {
        return Paths.curve(gardenIntakeFinal, gardenParkControl2, gardenParkPose).linear(gardenIntake, gardenParkPose);
    }
//loadingZoneStartToPark
    public static final Pose loadingZoneStart = poseFactory.of(132.5, 7.5, 270);
    public static final Pose parkPose = poseFactory.of(131.098, 32.3367, 270);

    public static Path loadingZoneStartToPark() {
        return Paths.line(loadingZoneStart, parkPose).constant(parkPose);
    }

//gardenStart-flowerintake-park
public static final Pose flowerIntakeMidpoint = poseFactory.of(109.9274, 95, 0);
    public static final Pose flowerIntakePose = poseFactory.of(134, 95, 0);


    public static Path gardenStartToFlowerIntakeMidpoint() {
        return Paths.line(gardenStart, flowerIntakeMidpoint).linear(gardenStart, flowerIntakeMidpoint);
    }

    public static Path flowerIntakeMidpointToFlowerIntakePose() {
        return Paths.line(flowerIntakeMidpoint, flowerIntakePose).linear(flowerIntakeMidpoint, flowerIntakePose);
    }

    public static Path flowerIntakePoseToFlowerIntakeMidpoint() {
        return Paths.line(flowerIntakePose, flowerIntakeMidpoint).linear(flowerIntakePose, flowerIntakeMidpoint);
    }
    public static Path flowerIntakeMidpointToPark() {
        return Paths.line(flowerIntakeMidpoint, parkPose).linear(flowerIntakeMidpoint, parkPose);
    }

        public static final Pose hiveMidpoint = poseFactory.of(81, 40, 90);
        public static final Pose intakeMidpointAudience = poseFactory.of(93.5, 33, 270);
        public static final Pose audienceFlowerIntake = poseFactory.of(93.5, 7.5, 270);

        public static Path gardenStartToHiveMidpoint() {
            return Paths.line(gardenStart, hiveMidpoint).linear(gardenStart, hiveMidpoint);
        }

        public static Path hiveMidpointToIntakeMidpointAudience() {
            return Paths.line(hiveMidpoint, intakeMidpointAudience).linear(hiveMidpoint, intakeMidpointAudience);
        }

        public static Path midpointIntakeToAudienceFlowerIntake() {
            return Paths.line(audienceFlowerIntake,audienceFlowerIntake).linear(audienceFlowerIntake, audienceFlowerIntake);
        }




//    public Path reverse(Path originalPath) {
//
//        // 1. Get start and end Poses directly from Path
//
//        Pose start = new Pose(originalPath.curve.startPoint().x(), originalPath.curve.startPoint().y(), originalPath.get(0.0).heading());
//        Pose end = originalPath.endPose();
//
//        // 2. Reverse geometry
//        Path reversed;
//        if (originalPath.curve instanceof Line) {
//            reversed = line(end, start);
//        } else if (originalPath.curve instanceof BezierCurve) {
//            BezierCurve originalBezier = (BezierCurve) originalPath.curve;
//            List<Vector2D> originalCps = originalBezier.getControlPoints();
//
//            // 1. Create list of Poses for the reversed curve
//            List<Pose> cpsPoses = new ArrayList<>();
//
//            // First element: start Pose of reversed path (original endPose)
//            cpsPoses.add(end);
//
//            // Middle elements: intermediate control points in reverse order
//            for (int i = originalCps.size() - 2; i >= 1; i--) {
//                Vector2D pt = originalCps.get(i);
//                cpsPoses.add(new Pose(pt.x(), pt.y(), 0));
//            }
//
//            // Last element: end Pose of reversed path (original startPose)
//            cpsPoses.add(start);
//
//            // 2. Create reversed Path using array of Poses
//            reversed = curve(cpsPoses.toArray(new Pose[0]));
//        } else {
//            throw new IllegalArgumentException("Unsupported curve type: " + originalPath.curve.getClass().getName());
//        }
//
//        // 3. Reverse heading interpolation if originalPath has a heading interpolator
//        if (hasHeading(originalPath)) {
//            reversed.heading((curve, t) -> originalPath.heading(1.0 - t));
//        } else {
//            reversed.reverseTangent();
//        }
//
//        return reversed;
//    }
//
//    public static boolean hasHeading(Path path) {
//        if (path == null) return false;
//        try {
//            Method method = Path.class.getDeclaredMethod("hasHeading");
//            method.setAccessible(true);
//            Boolean result = (Boolean) method.invoke(path);
//            return Boolean.TRUE.equals(result);
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public static Path gardenIntakeToLoadingZoneShoot;

    public static Path allianceFlowerIntakeToLoadingZoneShoot;

    public static void generateTrajectories(Follower follower , BBBAuto.StartChoice startChoice, BBBAuto.IntakeChoice intakeChoice) {

        if (aColor == BBBGlobalVariables.AColor.RED) {
            poseFactory.mirrorY(70.75);
            poseFactory.mirrorX(70.75);
        }
        Pose gardenIntakeToLoadingZoneShootControl1 = null;
        Pose gardenIntakeToLoadingZoneShootControl2 = null;

        Pose allianceFlowerIntakeToLoadingZoneShootControl1 = null;
        Pose allianceFlowerIntakeToLoadingZoneShootControl2 = null;

//        switch (transitChoice) {
//            case INSIDE:
//                gardenIntakeToLoadingZoneShootControl1 = gardenIntakeToLoadingZoneShootControl1_Inside;
//                gardenIntakeToLoadingZoneShootControl2 = gardenIntakeToLoadingZoneShootControl2_Inside;
//
//                allianceFlowerIntakeToLoadingZoneShootControl1 = allianceFlowerIntakeToLoadingZoneShootControl1_Inside;
//                allianceFlowerIntakeToLoadingZoneShootControl2 = allianceFlowerIntakeToLoadingZoneShootControl2_Inside;
//                break;
//            case OUTSIDE_TRAVEL:
//                gardenIntakeToLoadingZoneShootControl1 = gardenIntakeToLoadingZoneShootControl1_OutsideTravel;
//                gardenIntakeToLoadingZoneShootControl2 = gardenIntakeToLoadingZoneShootControl2_OutsideTravel;
//
//                allianceFlowerIntakeToLoadingZoneShootControl1 = allianceFlowerIntakeToLoadingZoneShootControl1_OutsideTravel;
//                allianceFlowerIntakeToLoadingZoneShootControl2 = allianceFlowerIntakeToLoadingZoneShootControl2_OutsideTravel;
//                break;
//            case OUTSIDE_PARK:
//                gardenIntakeToLoadingZoneShootControl1 = gardenIntakeToLoadingZoneShootControl1_OutsidePark;
//                gardenIntakeToLoadingZoneShootControl2 = gardenIntakeToLoadingZoneShootControl2_OutsidePark;
//
//                allianceFlowerIntakeToLoadingZoneShootControl1 = allianceFlowerIntakeToLoadingZoneShootControl1_OutsidePark;
//                allianceFlowerIntakeToLoadingZoneShootControl2 = allianceFlowerIntakeToLoadingZoneShootControl2_OutsidePark;
//               break;
//        }
//        gardenIntakeToLoadingZoneShoot =
//                curve(gardenIntake, gardenIntakeToLoadingZoneShootControl1, gardenIntakeToLoadingZoneShootControl2, loadingZoneShoot)
//                        .heading(
//                                Interpolator.piecewise()
//                                        .until(0.2, Interpolator.constant(gardenIntakeToLoadingZoneShootSegment1Heading))
//                                        .until(0.8, Interpolator.tangent)
//                                        .until(1, Interpolator.linear(gardenIntakeToLoadingZoneShootSegment3Start, gardenIntakeToLoadingZoneShootSegment3End))
//                        );
//
//        allianceFlowerIntakeToLoadingZoneShoot =
//             curve(allianceFlowerIntake, allianceFlowerIntakeToLoadingZoneShootControl1, allianceFlowerIntakeToLoadingZoneShootControl2, loadingZoneShoot)
//                     .heading(
//                             Interpolator.piecewise()
//                                     .until(0.2, Interpolator.constant(allianceFlowerIntakeToLoadingZoneShootSegment1Heading))
//                                     .until(0.8, Interpolator.tangent)
//                                     .until(1, Interpolator.linear(allianceFlowerIntakeToLoadingZoneShootSegment3Start, allianceFlowerIntakeToLoadingZoneShootSegment3End)));
//



//      switch(startChoice) {
//          case GARDEN:
//              startPose = gardenStart;
//              follower.setPose(startPose);
//              follower.update();
//              break;
//          case LOADING_ZONE_PARK:
//              startPose = loadingZoneStart;
//              follower.setPose(startPose);
//              follower.update();
//              break;
//          case ALLIANCE_FLOWER:
//              startPose = allianceFlowerStart;
//              follower.setPose(startPose);
//              follower.update();
//              break;
//
//          default:
//              throw new IllegalArgumentException("Invalid StartChoice: " + startChoice);
//      }





    }

}
