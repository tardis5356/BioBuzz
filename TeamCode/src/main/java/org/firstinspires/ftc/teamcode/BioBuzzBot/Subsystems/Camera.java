package org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems;


import static com.pedropathing.api.Paths.*;

import static org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.AutoTrajectories.aColor;
import static org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.GlobalVariables.inAuto;
import static org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Turret.scoring;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.seattlesolvers.solverslib.geometry.Vector2d;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.SwitchableCamera;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;

public class Camera extends SubsystemBase {

    public enum ActiveCamera {FRONT, BACK}

    public ActiveCamera currentCamera = ActiveCamera.FRONT;

    // Robot-center-relative extrinsics (x right, y forward, z up, inches).
    // pitch -90 = looking horizontally forward; yaw 180 = looking backward.
    private static final Position FRONT_POS = new Position(DistanceUnit.INCH, 0, BotPositions.Camera.Y_OFFSET, BotPositions.Camera.Z_OFFSET, 0);

    //TODO Camera Orientation and measuremnets
    private static final YawPitchRollAngles FRONT_ORI =
            new YawPitchRollAngles(AngleUnit.DEGREES, 0, -90 - 25, -90, 0);   // 25 deg up-tilt, tune
    private static final Position BACK_POS = new Position(DistanceUnit.INCH, 0, -BotPositions.Camera.Y_OFFSET, BotPositions.Camera.Z_OFFSET, 0);
    private static final YawPitchRollAngles BACK_ORI =
            new YawPitchRollAngles(AngleUnit.DEGREES, 180, -90 - 25, -90, 0);

    /**
     * Everything that belongs to one physical camera.
     */
    private static class Cam {
        VisionPortal portal;
        AprilTagProcessor tags;
        YellowBallProcessor balls;
    }

    private final Cam front = new Cam();
    private final Cam back = new Cam();


    static final Set<Integer> RED_TAG_IDS = Set.of(
            30, 31, 32, 33,   // RED SCORING
            34, 35, 36, 37    // RED AUDIENCE
    );

    static final Set<Integer> BLUE_TAG_IDS = Set.of(
            38, 39, 40, 41,   // BLUE AUDIENCE
            42, 43, 44, 45    // BLUE SCORING
    );

    static final Set<Integer> SCORING_SIDE_TAG_IDS = Set.of(
            30, 31, 32, 33,   // RED SCORING
            42, 43, 44, 45    // BLUE SCORING
    );

    static final Set<Integer> AUDIENCE_SIDE_TAG_IDS = Set.of(
            34, 35, 36, 37,   // RED AUDIENCE
            38, 39, 40, 41    // BLUE AUDIENCE
    );


    private final List<TrackedBall> tracks = new ArrayList<>();
    private int nextId = 0;

    public Camera(HardwareMap hardwareMap) {
        // One live-view container per portal; both show on the Driver Station
        int[] ids = VisionPortal.makeMultiPortalView(2, VisionPortal.MultiPortalLayout.HORIZONTAL);

        build(front, hardwareMap.get(WebcamName.class, "Webcam 1"), ids[0], FRONT_POS, FRONT_ORI);
        build(back, hardwareMap.get(WebcamName.class, "Webcam 2"), ids[1], BACK_POS, BACK_ORI);

        if (inAuto) {
            resumeYellowBallProcessing();
            pauseAprilTagProcessing();
        } else {
            pauseYellowBallProcessing();
            resumeAprilTagProcessing();
        }
        // Start with only the front camera streaming
        back.portal.stopStreaming();
    }

    @Override
    public void periodic() {


    }

    private void build(Cam c, WebcamName name, int viewId,
                       Position pos, YawPitchRollAngles ori) {
        c.tags = new AprilTagProcessor.Builder()
                .setCameraPose(pos, ori)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.RADIANS)
                .build();
        c.balls = new YellowBallProcessor(c);

        c.portal = new VisionPortal.Builder()
                .setCamera(name)
                .setCameraResolution(new android.util.Size(
                        BotPositions.Camera.RES_WIDTH, BotPositions.Camera.RES_HEIGHT))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .setLiveViewContainerId(viewId)
                .addProcessors(c.tags, c.balls)
                .build();
    }

    private Cam active() {
        return currentCamera == ActiveCamera.FRONT ? front : back;
    }

    public void switchCamera(ActiveCamera cam) {
        if (cam == currentCamera) return;

        Cam old = active();
        old.portal.stopStreaming();           // frees USB bandwidth + CPU
        synchronized (tracks) {
            tracks.clear();
        }   // tracks are per-viewpoint
        currentCamera = cam;
        active().portal.resumeStreaming();
    }

    public VisionPortal.CameraState getCameraState() {
        return active().portal.getCameraState();
    }

    public void stop() {
        front.portal.close();
        back.portal.close();
    }

    public void pauseYellowBallProcessing() {
        active().portal.setProcessorEnabled(active().balls, false);
    }

    public void resumeYellowBallProcessing() {
        active().portal.setProcessorEnabled(active().balls, true);
    }

    public List<TrackedBall> getTracks() {
        synchronized (tracks) {
            return new ArrayList<>(tracks);
        }
    }

    // === APRILTAG LOCALIZATION ===

    /**
     * Robot pose from the active camera's tags, in Pedro coordinates,
     * or null if no usable tag is visible. Because each processor has its
     * own camera pose, robotPose already accounts for which camera saw the tag.
     */

    public void pauseAprilTagProcessing() {
        active().portal.setProcessorEnabled(active().tags, false);
    }

    public void resumeAprilTagProcessing() {
        active().portal.setProcessorEnabled(active().tags, true);
    }

    public Pose getAprilTagPose() {

        //TODO exclude apriltags is needed
        List<AprilTagDetection> detections = active().tags.getDetections();

        double aprilTagY = 0;
        double aprilTagX = 0;
        double aprilTagRAD = 0;

        int validTags = 0;
        for (AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagSingleDetection) {

                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                int id = singleDet.id;

                // -------------------------------
                // FILTER 1: Ignore opponent alliance tags
                // -------------------------------
                if (aColor == GlobalVariables.AColor.RED) {
                    if (!RED_TAG_IDS.contains(id)) continue;
                } else { // blue alliance
                    if (!BLUE_TAG_IDS.contains(id)) continue;
                }
                // -------------------------------
                // FILTER 2: Ignore tags not on your side (Audience vs Scoring)
                // -------------------------------
                if (!scoring) {
                    if (!AUDIENCE_SIDE_TAG_IDS.contains(id)) continue;
                } else { // scoring side
                    if (!SCORING_SIDE_TAG_IDS.contains(id)) continue;
                }

                // -------------------------------
                // If tag passed both filters, include it
                // -------------------------------
                aprilTagY += singleDet.robotPose.getPosition().y;
                aprilTagX += singleDet.robotPose.getPosition().x;
                aprilTagRAD += singleDet.robotPose.getOrientation().getYaw(AngleUnit.RADIANS);
                validTags++;
            }
        }


        if (validTags == 0) {
            return null;
        }

        double finalY = aprilTagY / validTags;
        double finalX = aprilTagX / validTags;
        double finalRAD = aprilTagRAD / validTags;

        return new Pose(
                finalY + 72,
                72 - finalX,
                finalRAD - Math.PI / 2);
    }

    /**
     * Call each loop; snaps the follower to the tag pose when one is visible.
     */
    public boolean correctFollower(Follower follower) {
        Pose p = getAprilTagPose();
        if (p == null) return false;
        follower.setPose(p);
        return true;
    }

    // === DATA CLASSES ===

    public static class DetectedBall {
        public double x, y, radius;

        public DetectedBall(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    public static class TrackedBall {
        public int id;
        public double x, y, radius;
        public int framesSinceSeen;

        public TrackedBall(int id, double x, double y, double radius) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public double getDistanceInches() {
            return (BotPositions.Camera.BALL_DIAMETER_INCHES *
                    BotPositions.Camera.FOCAL_LENGTH_PIXELS) / (2.0 * radius);
        }
    }

    // === TRACKING === (unchanged)

    private void updateTracks(List<DetectedBall> detections) {
        synchronized (tracks) {
            boolean[] updated = new boolean[tracks.size()];
            for (DetectedBall det : detections) {
                int bestIndex = -1;
                double bestDist = Double.MAX_VALUE;
                for (int i = 0; i < tracks.size(); i++) {
                    TrackedBall t = tracks.get(i);
                    double dist = Math.hypot(t.x - det.x, t.y - det.y);
                    if (dist < bestDist && dist < BotPositions.Camera.MAX_ASSOCIATION_DIST) {
                        bestDist = dist;
                        bestIndex = i;
                    }
                }
                if (bestIndex != -1) {
                    TrackedBall t = tracks.get(bestIndex);
                    t.x = det.x;
                    t.y = det.y;
                    t.radius = det.radius;
                    t.framesSinceSeen = 0;
                    updated[bestIndex] = true;
                } else {
                    tracks.add(new TrackedBall(nextId++, det.x, det.y, det.radius));
                }
            }
            for (int i = 0; i < updated.length; i++)
                if (!updated[i]) tracks.get(i).framesSinceSeen++;
            tracks.removeIf(t -> t.framesSinceSeen > BotPositions.Camera.MAX_MISSED_FRAMES);
        }
    }

    // === VISION PROCESSOR ===

    public class YellowBallProcessor implements VisionProcessor {
        private final Cam owner;
        private final Mat hsv = new Mat(), mask = new Mat(), gray = new Mat(), circles = new Mat();
        private Mat kernel;
        private Rect roi;

        YellowBallProcessor(Cam owner) {
            this.owner = owner;
        }

        @Override
        public void init(int width, int height, CameraCalibration calibration) {
            roi = new Rect(0, height / 2, width, height / 2);
            kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                    BotPositions.Camera.MORPH_KERNEL_SIZE);
        }

        @Override
        public Object processFrame(Mat frame, long captureTimeNanos) {
            Mat cropped = frame.submat(roi);

            Imgproc.cvtColor(cropped, hsv, Imgproc.COLOR_RGB2HSV);
            Core.inRange(hsv, BotPositions.Camera.HSV_LOWER_YELLOW,
                    BotPositions.Camera.HSV_UPPER_YELLOW, mask);
            Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
            Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

            Imgproc.cvtColor(cropped, gray, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.blur(gray, gray, new Size(5, 5));
            Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT,
                    BotPositions.Camera.HOUGH_DP, BotPositions.Camera.HOUGH_MIN_DIST,
                    BotPositions.Camera.HOUGH_PARAM1, BotPositions.Camera.HOUGH_PARAM2,
                    BotPositions.Camera.HOUGH_MIN_RADIUS, BotPositions.Camera.HOUGH_MAX_RADIUS);

            List<DetectedBall> detections = new ArrayList<>();
            for (int i = 0; i < circles.cols(); i++) {
                double[] c = circles.get(0, i);
                if (c == null) continue;
                int cx = (int) c[0], cy = (int) c[1];
                if (cx < 0 || cy < 0 || cx >= mask.cols() || cy >= mask.rows()) continue;
                if (mask.get(cy, cx)[0] < 128) continue;
                detections.add(new DetectedBall(c[0], c[1] + roi.y, c[2]));
            }
            cropped.release();

            // Only the active camera feeds the shared track list
            if (owner == active()) updateTracks(detections);
            return detections;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onDrawFrame(Canvas canvas, int w, int h, float scale, float density, Object ctx) {
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4 * density);
            for (DetectedBall d : (List<DetectedBall>) ctx)
                canvas.drawCircle((float) (d.x * scale), (float) (d.y * scale),
                        (float) (d.radius * scale), paint);
        }
    }

    // === FIELD POSITION / PATH ===
    public Vector2d getBallFieldPosition(
            Follower follower,
            TrackedBall ball,
            boolean usingFrontCamera
    ) {
        double FOV = BotPositions.Camera.FOV_HORIZONTAL_RAD;

        double xNorm = (ball.x - BotPositions.Camera.RES_WIDTH / 2.0)
                / (BotPositions.Camera.RES_WIDTH / 2.0);

        double theta = xNorm * (FOV / 2.0);

        double d = ball.getDistanceInches();
        double horizontalDistance = d * Math.cos(BotPositions.Camera.PITCH_RADIAN);

        double xr = horizontalDistance * Math.sin(theta);
        double yr = horizontalDistance * Math.cos(theta);

        yr += usingFrontCamera ? BotPositions.Camera.Y_OFFSET : -BotPositions.Camera.Y_OFFSET;
        xr += BotPositions.Camera.X_OFFSET;

        Pose pose = follower.pose();
        double robotX = pose.x();
        double robotY = pose.y();
        double robotHeading = pose.heading();

        double cameraHeading = usingFrontCamera
                ? robotHeading
                : robotHeading + Math.PI;

        double fieldX = robotX + (xr * Math.cos(cameraHeading) - yr * Math.sin(cameraHeading));
        double fieldY = robotY + (xr * Math.sin(cameraHeading) + yr * Math.cos(cameraHeading));

        return new Vector2d(fieldX, fieldY);
    }

    // === PATH GENERATION ===

    public FollowPathCommand goToNearestBall(Follower follower) {

        TrackedBall nearest = null;
        double bestDist = Double.MAX_VALUE;

        for (TrackedBall t : tracks) {
            double d = t.getDistanceInches();
            if (d < bestDist) {
                bestDist = d;
                nearest = t;
            }
        }

        Vector2d pos = getBallFieldPosition(
                follower,
                nearest,
                currentCamera.equals(ActiveCamera.FRONT)
        );

        Pose start = follower.pose();
        Pose ballPos = new Pose(pos.getX(), pos.getY(), 0);


        return new FollowPathCommand(follower, line(start, ballPos).tangent());
    }
}

