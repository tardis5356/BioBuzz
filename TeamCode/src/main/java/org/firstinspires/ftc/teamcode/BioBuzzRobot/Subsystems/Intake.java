package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Intake extends SubsystemBase {

    public static Intake_State intake_State =  Intake_State.STOP;

    public enum Intake_State { IN, STOP, OUT }

    public enum IntakeMode {
        MANUAL,
        AUTO
    }
    public static IntakeMode intakeMode = IntakeMode.MANUAL;


    private DcMotorEx mI;
    private DigitalChannel[] intake_BeamBreaks;

    private final ElapsedTime bbOverflowTimer = new ElapsedTime();


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
        stop();
    }

    @Override
    public void periodic() {

        // Count balls
        int bbBallCount = 0;
        for (DigitalChannel bb : intake_BeamBreaks) {
            if (bb.getState()) bbBallCount++;
        }

        // Overflow logic
        if (bbBallCount <= 2) {
            bbOverflowTimer.reset();
        }

        // Overflow action
        if (intakeMode == IntakeMode.MANUAL && bbOverflowTimer.seconds() > 0.5) {
            enableAuto();
            out();
            sleep(BotPositions.Intake.EJECT_TIME_MS);
            bbOverflowTimer.reset();
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
    public void enableAuto() {
        intakeMode = IntakeMode.AUTO;
    }

    public void disableAuto() {
        intakeMode = IntakeMode.MANUAL;
    }
}

