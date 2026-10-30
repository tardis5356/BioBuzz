package org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems;

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

    private DigitalChannel[] intakeBeamBreaks;
    private DigitalChannel bbS;

    private boolean[] latched;
    private long[] lastBroken;

    private boolean sLatched = false;
    private long sLastBroken = 0;

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
        bbS = hardwareMap.get(DigitalChannel.class, "bbS");

        intakeBeamBreaks = new DigitalChannel[]{bb1, bb2, bb3, bb4};

        for (DigitalChannel bb : intakeBeamBreaks) {
            bb.setMode(DigitalChannel.Mode.INPUT);
        }
        bbS.setMode(DigitalChannel.Mode.INPUT);

        latched = new boolean[intakeBeamBreaks.length];
        lastBroken = new long[intakeBeamBreaks.length];

        stop();
    }

    @Override
    public void periodic() {

        // Count balls
        int bbBallCount = 0;
        for (int i = 0; i < intakeBeamBreaks.length; i++) {
            if (getIntakeBeamBreakState(i)) bbBallCount++;
        }

        // Reset overflow timer when safe
        if (bbBallCount <= 2) {
            bbOverflowTimer.reset();
        }

        // Overflow triggers auto mode
        if (intakeMode == IntakeMode.MANUAL && bbOverflowTimer.seconds() > 0.5) {
            enableAuto();
            out();
        }

        // AUTO MODE: continue outtaking until bbS clears
        if (intakeMode == IntakeMode.AUTO) {
            boolean sState = getShooterBeamBreakState();

            if (!sState) {  // bbS is clear
                stop();
                disableAuto();
            }
        }
    }

    // === Beam Break Latching ===

    private boolean getIntakeBeamBreakState(int index) {
        long now = System.currentTimeMillis();
        boolean broken = !intakeBeamBreaks[index].getState(); // false = broken

        if (broken) {
            latched[index] = true;
            lastBroken[index] = now;
        } else if (latched[index] && now - lastBroken[index] > CLEAR_TIME_MS) {
            latched[index] = false;
        }

        return latched[index];
    }

    private boolean getShooterBeamBreakState() {
        long now = System.currentTimeMillis();
        boolean broken = !bbS.getState(); // false = broken

        if (broken) {
            sLatched = true;
            sLastBroken = now;
        } else if (sLatched && now - sLastBroken > CLEAR_TIME_MS) {
            sLatched = false;
        }

        return sLatched;
    }

    // === Motor Control ===

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



