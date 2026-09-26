package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Intake extends SubsystemBase {

    public static Intake_State intake_State;

    public enum Intake_State { IN, STOP, OUT }

    public enum Mode {
        MANUAL,
        AUTO
    }
    private Mode mode = Mode.MANUAL;


    private DcMotorEx mI;
    private DigitalChannel[] intake_BeamBreaks;

    public static boolean autoIntake = false;
    private boolean overflowTimerRunning = false;

    private final ElapsedTime bbOverflowTimer = new ElapsedTime();
    private Intake_State intake_State = Intake_State.STOP;

    public Intake(HardwareMap hardwareMap) {

        mI = hardwareMap.get(DcMotorEx.class, "mI");
        mI.setDirection(DcMotorSimple.Direction.FORWARD);

        DigitalChannel bb1 = hardwareMap.get(DigitalChannel.class, "bb1");
        DigitalChannel bb2 = hardwareMap.get(DigitalChannel.class, "bb2");
        DigitalChannel bb3 = hardwareMap.get(DigitalChannel.class, "bb3");
        DigitalChannel bb4 = hardwareMap.get(DigitalChannel.class, "bb4");

        bb1.setMode(DigitalChannel.Mode.INPUT);
        bb2.setMode(DigitalChannel.Mode.INPUT);
        bb3.setMode(DigitalChannel.Mode.INPUT);
        bb4.setMode(DigitalChannel.Mode.INPUT);

        intake_BeamBreaks = new DigitalChannel[]{bb1, bb2, bb3, bb4};
    }

    @Override
    public void periodic() {

        // Count balls
        int bbBallCount = 0;
        for (DigitalChannel bb : intake_BeamBreaks) {
            if (bb.getState()) bbBallCount++;
        }

        // Overflow logic
        if (bbBallCount > 2) {
            if (!overflowTimerRunning) {
                overflowTimerRunning = true;
                bbOverflowTimer.reset();
            }
        } else {
            overflowTimerRunning = false;
            bbOverflowTimer.reset();
        }

        // Overflow action
        if (overflowTimerRunning && bbOverflowTimer.seconds() > 1.0) {
            enableAuto();
            out();
            sleep(300);
            disableAuto();
            stop();
        }
    }

    public void in() {
        mI.setPower(BotPositions.Intake.INTAKE_POWER);
        intake_State = Intake_State.IN;
    }

    public void out() {
        mI.setPower(BotPositions.Intake.OUTTAKE_POWER);
        intake_State = Intake_State.OUT;
    }

    public void stop() {
        mI.setPower(0);
        intake_State = Intake_State.STOP;
    }

    public boolean isAuto() {
        return mode == Mode.AUTO;
    }

    public void enableAuto() {
        mode = Mode.AUTO;
    }

    public void disableAuto() {
        mode = Mode.MANUAL;
    }
}

