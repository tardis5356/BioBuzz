package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Intake extends SubsystemBase {

    static DcMotorEx mI;

    public enum Intake_State {
        IN, STOP, OUT
    }

    public static Intake_State intake_State;

    public Intake(HardwareMap hardwareMap) {
        mI = hardwareMap.get(DcMotorEx.class, "mI");
        intake_State = intake_State.STOP;
    }

    @Override
    public void periodic() {

    }

    public void in() {
        mI.setPower(1);
        intake_State = intake_State.IN;
    }

    public void out() {
        mI.setPower(-1);
        intake_State = intake_State.OUT;

    }

    public void stop() {
        mI.setPower(0);
        intake_State = intake_State.STOP;

    }
}
