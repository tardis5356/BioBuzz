package org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class BBBShooter extends SubsystemBase {
    DcMotorSimple mF;
    DcMotorEx mBR;
    public static double flyWheelSpeed /* = 2400 */;
    double currentVelocity;

    boolean bangBangActive;

    public BBBShooter(HardwareMap hardwareMap) {
        mBR = hardwareMap.get(DcMotorEx.class, "mBR");
        mF = hardwareMap.get(DcMotorSimple.class, "mF");
        mF.setDirection(DcMotorSimple.Direction.REVERSE);
        flyWheelSpeed = 1100;
        currentVelocity = mBR.getVelocity();
        bangBangActive = true;
    }

    @Override
    public void periodic(){
        currentVelocity = mBR.getVelocity();

if (bangBangActive){
    mF.setPower(currentVelocity < flyWheelSpeed ? 1.0 : 0.0);
}else {
    mF.setPower(0);
}

    }




    public void on() {
       bangBangActive = true;
    }



    public void stop() {
    bangBangActive = false;
    }
}
