package org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Intake extends SubsystemBase {

    public enum Intake_State { IN, STOP, OUT }
    public enum IntakeMode { MANUAL, AUTO }

    public static Intake_State intake_State = Intake_State.STOP;
    public static IntakeMode intakeMode = IntakeMode.MANUAL;

    private DcMotorEx mIF, mIB;
    private DcMotorEx[] intakeMotors;

    private DigitalChannel[] beamBreaks;
    private boolean[] latched;
    private long[] lastBroken;

    private static final long CLEAR_TIME_MS = 75;

    private final ElapsedTime bbOverflowTimer = new ElapsedTime();

    public Intake(HardwareMap hardwareMap) {

        mIF = hardwareMap.get(DcMotorEx.class, "mIF");
        mIB = hardwareMap.get(DcMotorEx.class, "mIB");

        mIF.setDirection(DcMotorSimple.Direction.REVERSE);
        mIB.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeMotors = new DcMotorEx[]{mIF, mIB};

        DigitalChannel bb1 = hardwareMap.get(DigitalChannel.class, "bb1");
        DigitalChannel bb2 = hardwareMap.get(DigitalChannel.class, "bb2");
        DigitalChannel bb3 = hardwareMap.get(DigitalChannel.class, "bb3");
        DigitalChannel bb4 = hardwareMap.get(DigitalChannel.class, "bb4");

        beamBreaks = new DigitalChannel[]{bb1, bb2, bb3, bb4};

        for (DigitalChannel bb : beamBreaks) {
            bb.setMode(DigitalChannel.Mode.INPUT);
        }

        latched = new boolean[beamBreaks.length];
        lastBroken = new long[beamBreaks.length];

        stop();
    }

    @Override
    public void periodic() {

        int bbBallCount = 0;
        for (int i = 0; i < beamBreaks.length; i++) {
            if (getBeamBreakState(i)) bbBallCount++;
        }

        if (bbBallCount <= 2) {
            bbOverflowTimer.reset();
        }

        if (intakeMode == IntakeMode.MANUAL && bbOverflowTimer.seconds() > 0.5) {
            enableAuto();
            out();
            sleep(BotPositions.Intake.EJECT_TIME_MS);
            bbOverflowTimer.reset();
            disableAuto();
            stop();
        }
    }

    private boolean getBeamBreakState(int index) {
        long now = System.currentTimeMillis();
        boolean broken = !beamBreaks[index].getState(); // false = broken

        if (broken) {
            latched[index] = true;
            lastBroken[index] = now;
        } else if (latched[index] && now - lastBroken[index] > CLEAR_TIME_MS) {
            latched[index] = false;
        }

        return latched[index];
    }

    public void setIntakePower(double power) {
        for (DcMotorEx m : intakeMotors) {
            m.setPower(power);
        }
    }

    public void in() {
        setIntakePower(BotPositions.Intake.INTAKE_POWER);
        intake_State = Intake_State.IN;
    }

    public void out() {
        setIntakePower(BotPositions.Intake.OUTTAKE_POWER);
        intake_State = Intake_State.OUT;
    }

    public void stop() {
        setIntakePower(0);
        intake_State = Intake_State.STOP;
    }

    public void enableAuto() {
        intakeMode = IntakeMode.AUTO;
    }

    public void disableAuto() {
        intakeMode = IntakeMode.MANUAL;
    }
}


