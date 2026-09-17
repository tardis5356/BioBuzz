package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;

import com.pedropathing.math.Pose;
import com.pedropathing.paths.curves.bezier.BezierCurve;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.util.TelemetryData;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class PedroAutoSample extends CommandOpMode {
    private Follower follower;
    TelemetryData telemetryData = new TelemetryData(telemetry);

    // Poses
    private final Pose startPose = new Pose(9, 111, Math.toRadians(-90));
    private final Pose scorePose = new Pose(16, 128, Math.toRadians(-45));
    private final Pose pickup1Pose = new Pose(30, 121, Math.toRadians(0));
    private final Pose pickup2Pose = new Pose(30, 131, Math.toRadians(0));
    private final Pose pickup3Pose = new Pose(45, 128, Math.toRadians(90));
    private final Pose parkPose = new Pose(68, 96, Math.toRadians(-90));

    // Path chains
    private PathChain scorePreload, grabPickup1, grabPickup2, grabPickup3;
    private PathChain scorePickup1, scorePickup2, scorePickup3, park;

    public void buildPaths() {
        scorePreload = follower.pathIndex()
                .addPath(new BezierCurve(startPose, scorePose))
                .setLinearHeadingInterpolation(startPose.heading(), scorePose.heading())
                .build();

        grabPickup1 = follower.pathIndex()
                .addPath(new BezierCurve(scorePose, pickup1Pose))
                .setLinearHeadingInterpolation(scorePose.heading(), pickup1Pose.heading())
                .build();

        scorePickup1 = follower.pathIndex()
                .addPath(new BezierCurve(pickup1Pose, scorePose))
                .setLinearHeadingInterpolation(pickup1Pose.heading(), scorePose.heading())
                .build();

        grabPickup2 = follower.pathIndex()
                .addPath(new BezierCurve(scorePose, pickup2Pose))
                .setLinearHeadingInterpolation(scorePose.heading(), pickup2Pose.heading())
                .build();

        scorePickup2 = follower.pathIndex()
                .addPath(new BezierCurve(pickup2Pose, scorePose))
                .setLinearHeadingInterpolation(pickup2Pose.heading(), scorePose.heading())
                .build();

        grabPickup3 = follower.pathIndex()
                .addPath(new BezierLine(scorePose, pickup3Pose))
                .setLinearHeadingInterpolation(scorePose.heading(), pickup3Pose.heading())
                .build();

        scorePickup3 = follower.pathIndex()
                .addPath(new BezierCurve(pickup3Pose, scorePose))
                .setLinearHeadingInterpolation(pickup3Pose.heading(), scorePose.heading())
                .build();

        park = follower.pathIndex()
                .addPath(new BezierCurve(
                        scorePose,
                        new Pose(68, 110), // Control point
                        parkPose)
                )
                .setLinearHeadingInterpolation(scorePose.heading(), parkPose.heading())
                .build();
    }

    // Mechanism commands - replace these with your actual subsystem commands
    private InstantCommand openOuttakeClaw() {
        return new InstantCommand(() -> {
            // Example: outtakeSubsystem.openClaw();
        });
    }

    private InstantCommand grabSample() {
        return new InstantCommand(() -> {
            // Example: intakeSubsystem.grabSample();
        });
    }

    private InstantCommand scoreSample() {
        return new InstantCommand(() -> {
            // Example: outtakeSubsystem.scoreSample();
        });
    }

    private InstantCommand level1Ascent() {
        return new InstantCommand(() -> {
            // Example: hangSubsystem.level1Ascent();
        });
    }

    @Override
    public void initialize() {
        super.reset();


        // Initialize follower
        follower = Constants.createFollower(hardwareMap);
        follower.setPose(startPose);
        buildPaths();


        // Schedule the autonomous sequence
        schedule(
                // Score preload
                new FollowPathCommand(follower, scorePreload),
                openOuttakeClaw(),
                new WaitCommand(1000), // Wait 1 second

                // First pickup cycle
                new FollowPathCommand(follower, grabPickup1).setGlobalMaxPower(0.5), // Sets globalMaxPower to 50% for all future paths
                // (unless a custom maxPower is given)
                grabSample(),
                new FollowPathCommand(follower, scorePickup1),
                scoreSample(),

                // Second pickup cycle
                new FollowPathCommand(follower, grabPickup2),
                grabSample(),
                new FollowPathCommand(follower, scorePickup2, 1.0), // Overrides maxPower to 100% for this path only
                scoreSample(),

                // Third pickup cycle
                new FollowPathCommand(follower, grabPickup3),
                grabSample(),
                new FollowPathCommand(follower, scorePickup3),
                scoreSample(),

                // Park
                new FollowPathCommand(follower, park, false), // park with holdEnd false
                level1Ascent()
        );
    }

    @Override
    public void run() {
        super.run();
        follower.update();

        telemetryData.addData("X", follower.setPose().getX());
        telemetryData.addData("Y", follower.setPose().getY());
        telemetryData.addData("Heading", follower.setPose().getHeading());
        telemetryData.update();
    }
}