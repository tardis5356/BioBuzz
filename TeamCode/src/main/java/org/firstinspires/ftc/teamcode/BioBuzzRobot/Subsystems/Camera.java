package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;


import com.qualcomm.robotcore.hardware.HardwareMap;
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
import java.util.List;

public class Camera extends SubsystemBase {

    private static final int IMG_HEIGHT = 720;
    private static final int IMG_WIDTH = 1280;
    // === HARDWARE AND PROCESSORS ===
    public VisionPortal visionPortal;
    public final WebcamName frontWebcam, backWebcam;

    PDController controller;
    public double p = .001, d = .0003;
    public double FB = 0, LR = 0, Rotation = 0.;

    //OpenCV stuff
    double cX = 0;
    double cY = 0;
    double width = 0;

    MatOfPoint lC;
    private static final int CAMERA_WIDTH = 1280; // width  of wanted camera resolution
    private static final int CAMERA_HEIGHT = 720; // height of wanted camera resolution

    // Calculate the distance using the formula
    public static final double objectWidthInRealWorldUnits = 2.5;  // Replace with the actual width of the object in real-world units (in inches)
    public static final double focalLength = (10 * 460) / objectWidthInRealWorldUnits;
    List<ColorBlobLocatorProcessor.Blob> blobs = new ArrayList<>();

    private CameraName switchableCamera;

    public enum ActiveCamera {
        BACK,
        FRONT
    }

    private ActiveCamera currentCamera = ActiveCamera.FRONT;

    public Camera(HardwareMap hardwareMap) {
        backWebcam = hardwareMap.get(WebcamName.class, "Webcam 2");
        frontWebcam = hardwareMap.get(WebcamName.class, "Webcam 1");


        // Start with the turret camera
        switchableCamera = ClassFactory.getInstance()
                .getCameraManager().nameForSwitchableCamera(frontWebcam, backWebcam);
        ;


        visionPortal = new VisionPortal.Builder()
                .setCamera(switchableCamera)
                //.addProcessor()
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .setCameraResolution(new android.util.Size(IMG_WIDTH, IMG_HEIGHT))
                // .setAutoStartStreamOnBuild(true)
                .build();


    }

    class YellowBlobDetectionPipeline extends OpenCvPipeline {
        @Override
        public Mat processFrame(Mat input) {
            // Preprocess the frame to detect yellow regions
            Mat yellowMask = preprocessFrame(input);

            // Find contours of the detected yellow regions
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(yellowMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Find the largest yellow contour (blob)
            MatOfPoint largestContour = findLargestContour(contours);
            lC = largestContour;

            if (largestContour != null) {
                // Draw a red outline around the largest detected object
                Imgproc.drawContours(input, contours, contours.indexOf(largestContour), new Scalar(255, 0, 0), 2);
                // Calculate the width of the bounding box
                width = calculateWidth(largestContour);

                // Display the width next to the label
                String widthLabel = "Width: " + (int) width + " pixels";
                Imgproc.putText(input, widthLabel, new Point(cX + 10, cY + 20), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
                //Display the Distance
                String distanceLabel = "Distance: " + String.format("%.2f", getDistance(width)) + " inches";
                Imgproc.putText(input, distanceLabel, new Point(cX + 10, cY + 60), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
                // Calculate the centroid of the largest contour
                Moments moments = Imgproc.moments(largestContour);
                cX = moments.get_m10() / moments.get_m00();
                cY = moments.get_m01() / moments.get_m00();

                // Draw a dot at the centroid
                String label = "(" + (int) cX + ", " + (int) cY + ")";
                Imgproc.putText(input, label, new Point(cX + 10, cY), Imgproc.FONT_HERSHEY_COMPLEX, 0.5, new Scalar(0, 255, 0), 2);
                Imgproc.circle(input, new Point(cX, cY), 5, new Scalar(0, 255, 0), -1);
                //Imgproc.putText(input,,new Point(cX+20, cY), Imgproc.FONT_HERSHEY_COMPLEX, 0.5,);

            }

            return input;

        }

//

        private Mat preprocessFrame(Mat frame) {
            Mat hsvFrame = new Mat();
            Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);

            Scalar lowerYellow = new Scalar(100, 100, 100);
            Scalar upperYellow = new Scalar(180, 230, 230);


            Mat yellowMask = new Mat();
            Core.inRange(hsvFrame, lowerYellow, upperYellow, yellowMask);

            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
            Imgproc.morphologyEx(yellowMask, yellowMask, Imgproc.MORPH_OPEN, kernel);
            Imgproc.morphologyEx(yellowMask, yellowMask, Imgproc.MORPH_CLOSE, kernel);

            return yellowMask;
        }

        public MatOfPoint findLargestContour(List<MatOfPoint> contours) {
            double maxArea = 300;
            MatOfPoint largestContour = null;

            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                    largestContour = contour;
                }
            }


            return largestContour;
        }

        private double calculateWidth(MatOfPoint contour) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            return boundingRect.width;
        }

        private double getDistance(double width) {
            double distance = (objectWidthInRealWorldUnits * focalLength) / width;
            return distance;
        }
    }
}
