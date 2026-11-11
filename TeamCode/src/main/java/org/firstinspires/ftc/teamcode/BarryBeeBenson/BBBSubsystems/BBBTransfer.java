package org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class BBBTransfer extends SubsystemBase {
    CRServo sI;

    public BBBTransfer  (HardwareMap hardwareMap){
        sI = hardwareMap.get(CRServo.class, "sI");


    }



    public void on() {
        sI.setPower(0.8);
    }

    public void stop() {
        sI.setPower(0);}

    public void reverse(){
        sI.setPower(-.8);
    }
}

