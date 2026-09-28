package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;



import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PDController;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.geometry.Pose2d;
import com.seattlesolvers.solverslib.geometry.Vector2d;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;


import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class Camera extends SubsystemBase {

    private OpenCvCamera frontCam;
    private OpenCvCamera backCam;
    private OpenCvCamera activeCam;

    public enum ActiveCamera {
        FRONT,
        BACK
    }

    public ActiveCamera currentCamera = ActiveCamera.FRONT;

    private List<TrackedBall> tracks = new ArrayList<>();
    private int nextId = 0;

    private YellowBallPipeline pipeline = new YellowBallPipeline();

    public Camera(HardwareMap hardwareMap) {

        int viewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        frontCam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"), viewId);

        backCam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 2"), viewId);

        frontCam.setPipeline(pipeline);
        backCam.setPipeline(pipeline);

        activeCam = frontCam;
        openAndStart(activeCam);
    }

    private void openAndStart(OpenCvCamera cam) {
        cam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            public void onOpened() {
                cam.startStreaming(
                        BotPositions.Camera.RES_WIDTH,
                        BotPositions.Camera.RES_HEIGHT,
                        OpenCvCameraRotation.UPRIGHT
                );
            }

            @Override
            public void onError(int errorCode) {}
        });
    }

    public void switchCamera(ActiveCamera cam) {
        if (cam == currentCamera) return;

        activeCam.stopStreaming();

        activeCam = (cam == ActiveCamera.FRONT) ? frontCam : backCam;
        currentCamera = cam;

        openAndStart(activeCam);
    }

    public List<TrackedBall> getTracks() {
        return tracks;
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

    public class TrackedBall {
        public int id;
        public double x, y, radius;
        public int framesSinceSeen;

        public TrackedBall(int id, double x, double y, double radius) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.framesSinceSeen = 0;
        }

        public double getDistanceInches() {
            return (BotPositions.Camera.BALL_DIAMETER_INCHES *
                    BotPositions.Camera.FOCAL_LENGTH_PIXELS) / (2.0 * radius);
        }
    }

    // === TRACKING LOGIC ===

    private void updateTracks(List<DetectedBall> detections) {
        boolean[] updated = new boolean[tracks.size()];

        for (DetectedBall det : detections) {
            int bestIndex = -1;
            double bestDist = Double.MAX_VALUE;

            for (int i = 0; i < tracks.size(); i++) {
                TrackedBall t = tracks.get(i);
                double dx = t.x - det.x;
                double dy = t.y - det.y;
                double dist = Math.sqrt(dx * dx + dy * dy);

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

        for (int i = 0; i < tracks.size(); i++) {
            if (!updated[i]) {
                tracks.get(i).framesSinceSeen++;
            }
        }

        tracks.removeIf(t -> t.framesSinceSeen > BotPositions.Camera.MAX_MISSED_FRAMES);
    }

    // === PIPELINE ===

    public class YellowBallPipeline extends OpenCvPipeline {

        private Mat hsv = new Mat();
        private Mat mask = new Mat();
        private Mat gray = new Mat();
        private Mat circles = new Mat();

        @Override
        public Mat processFrame(Mat input) {

            // === ROI cropping ===
            Rect roi = new Rect(
                    0,
                    BotPositions.Camera.RES_HEIGHT / 2,
                    BotPositions.Camera.RES_WIDTH,
                    BotPositions.Camera.RES_HEIGHT / 2
            );
            Mat cropped = input.submat(roi);

            Imgproc.cvtColor(cropped, hsv, Imgproc.COLOR_BGR2HSV);

            Core.inRange(
                    hsv,
                    BotPositions.Camera.HSV_LOWER_YELLOW,
                    BotPositions.Camera.HSV_UPPER_YELLOW,
                    mask
            );

            Mat kernel = Imgproc.getStructuringElement(
                    Imgproc.MORPH_RECT,
                    BotPositions.Camera.MORPH_KERNEL_SIZE
            );

            Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
            Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

            Imgproc.cvtColor(cropped, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.blur(gray, gray, new Size(5, 5));

            circles = new Mat();
            Imgproc.HoughCircles(
                    gray,
                    circles,
                    Imgproc.HOUGH_GRADIENT,
                    BotPositions.Camera.HOUGH_DP,
                    BotPositions.Camera.HOUGH_MIN_DIST,
                    BotPositions.Camera.HOUGH_PARAM1,
                    BotPositions.Camera.HOUGH_PARAM2,
                    BotPositions.Camera.HOUGH_MIN_RADIUS,
                    BotPositions.Camera.HOUGH_MAX_RADIUS
            );

            List<DetectedBall> detections = new ArrayList<>();

            if (circles.cols() > 0) {
                for (int i = 0; i < circles.cols(); i++) {
                    double[] c = circles.get(0, i);
                    if (c == null) continue;

                    double cx = c[0];
                    double cy = c[1];
                    double r = c[2];

                    if (mask.get((int) cy, (int) cx)[0] < 128) continue;

                    // IMPORTANT: add ROI offset back to Y coordinate
                    detections.add(new DetectedBall(cx, cy + roi.y, r));
                }
            }

            updateTracks(detections);

            return input;
        }

    }

    // === FIELD POSITION CALCULATION ===

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
        Pose ballPos = new Pose(pos.getX(), pos.getY(), 0 );



        return new FollowPathCommand(follower, line(start, ballPos).tangent());
    }
}

