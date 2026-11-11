package org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class BBBWedge extends SubsystemBase {
    Servo sW;

    public BBBWedge(HardwareMap hardwareMap) {
        sW = hardwareMap.get(Servo.class, "sW");
    }


    public void up() {
        sW.setPosition(1);
    }

    public void down() {
        sW.setPosition(0);
    }
}