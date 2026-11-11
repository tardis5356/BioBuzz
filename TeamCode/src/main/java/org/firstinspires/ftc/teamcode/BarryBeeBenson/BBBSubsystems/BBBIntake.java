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

    public BBBIntake (HardwareMap hardwareMap){
        mI = hardwareMap.get(DcMotorSimple.class, "mI");

        sIL = hardwareMap.get(CRServo.class, "sIL");
        sIR = hardwareMap.get(CRServo.class, "sIR");

    }



public void in() {
mI.setPower(-1);
sIL.setPower(-1);
sIR.setPower(1);
}

public void out() {
mI.setPower(1);
sIR.setPower(-1);
sIL.setPower(-1);
}
public void stop() {
  mI.setPower(0);
  sIL.setPower(0);
  sIR.setPower(0);
}

}



