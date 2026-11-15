package org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.BotPositions;
import org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems.Intake;

public class BBBIntake extends SubsystemBase {
    DcMotorSimple mI;
    CRServo sIL, sIR;

    public enum Intake_State {IN, STOP, OUT}



    public static BBBIntake.Intake_State intake_State;


    public BBBIntake(HardwareMap hardwareMap) {
        mI = hardwareMap.get(DcMotorSimple.class, "mI");

        sIL = hardwareMap.get(CRServo.class, "sIL");
        sIR = hardwareMap.get(CRServo.class, "sIR");

        intake_State = BBBIntake.Intake_State.STOP;
    }


    public void in() {
        mI.setPower(-1);
        sIL.setPower(-1);
        sIR.setPower(1);

        intake_State = BBBIntake.Intake_State.IN;
    }

    public void out() {
        mI.setPower(1);
        sIR.setPower(1);
        sIL.setPower(-1);

        intake_State = BBBIntake.Intake_State.OUT;
    }

    public void stop() {
        mI.setPower(0);
        sIL.setPower(0);
        sIR.setPower(0);

        intake_State = BBBIntake.Intake_State.STOP;
    }

}



