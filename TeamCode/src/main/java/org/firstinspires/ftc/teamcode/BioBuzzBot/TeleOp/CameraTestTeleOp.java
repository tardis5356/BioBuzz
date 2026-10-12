package org.firstinspires.ftc.teamcode.BioBuzzBot.TeleOp;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Auto.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Camera;

import java.util.List;

@TeleOp(name="Camera + Ball Tracking Test", group="Test")
public class CameraTestTeleOp extends LinearOpMode {

    private Follower follower;
    private Camera camera;

    @Override
    public void runOpMode() throws InterruptedException {

        follower = Constants.create(hardwareMap);
        camera = new Camera(hardwareMap);

        telemetry.addLine("Camera Test TeleOp Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // === SWITCH CAMERAS ===
            if (gamepad1.a) {
                camera.switchCamera(Camera.ActiveCamera.FRONT);
            }
            if (gamepad1.b) {
                camera.switchCamera(Camera.ActiveCamera.BACK);
            }

            // === PRINT TRACKED BALLS ===
            List<Camera.TrackedBall> tracks = camera.getTracks();

            telemetry.addData("Active Camera", camera.currentCamera);

            if (tracks.isEmpty()) {
                telemetry.addLine("No balls detected");
            } else {
                for (Camera.TrackedBall t : tracks) {
                    telemetry.addData("Ball ID " + t.id,
                            "px=(%.0f, %.0f) r=%.1f dist=%.1f in",
                            t.x, t.y, t.radius, t.getDistanceInches());
                }
            }

            // === PRINT FIELD POSITIONS ===
            if (!tracks.isEmpty()) {
                Camera.TrackedBall nearest = tracks.get(0);

                Vector2d pos = camera.getBallFieldPosition(
                        follower,
                        nearest,
                        camera.currentCamera == Camera.ActiveCamera.FRONT
                );

                telemetry.addData("Nearest Ball Field Pos",
                        "X=%.1f  Y=%.1f", pos.getX(), pos.getY());
            }

            // === DRIVE TO NEAREST BALL ===
            if (gamepad1.x) {
                CommandScheduler.getInstance().schedule(
                        camera.goToNearestBall(follower)
                );
            }

            // === MANUAL DRIVE ===
            double forward = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;
            follower.manual(forward, lateral, turn);
            follower.update();

            telemetry.update();
        }
    }
}
