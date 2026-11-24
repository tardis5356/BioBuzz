package org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class BBBTransfer extends SubsystemBase {
    CRServo sI;
    private static AnalogInput saI;

    boolean windmillOn;

   public boolean autoWindmill;

    double defaultWindmillPosition = 0.13;

    double windmillTolerance;

    public BBBTransfer  (HardwareMap hardwareMap){
        sI = hardwareMap.get(CRServo.class, "sI");
        saI = hardwareMap.get(AnalogInput.class, "saI");

    }

    @Override
    public void periodic(){
        if (!windmillOn){
            if (!autoWindmill){
                if (Math.abs(getCurrentPosition() -  defaultWindmillPosition)> .1){
                    sI.setPower(.5);
                } else {
                    sI.setPower(0);
                }
            } else sI.setPower(0);

        } else {
            sI.setPower(-1);
        }

    }

    public double getCurrentPosition(){
        return saI.getVoltage()/saI.getMaxVoltage();
    }




    public void on() {
        windmillOn = true;
    }

    public void stop() {
        windmillOn = false;

    }

    public void reverse(){
        sI.setPower(1);
    }
}

