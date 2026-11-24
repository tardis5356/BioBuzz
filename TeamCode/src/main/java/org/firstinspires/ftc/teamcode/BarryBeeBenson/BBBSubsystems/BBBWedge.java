package org.firstinspires.ftc.teamcode.BarryBeeBenson.BBBSubsystems;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class BBBWedge extends SubsystemBase {
    Servo sW;

    public BBBWedge(HardwareMap hardwareMap) {

        sW = hardwareMap.get(Servo.class, "sW");
        sW.setDirection(Servo.Direction.REVERSE);
    }


    public void up() {
        sW.setPosition(0.76);
    }

    public void block() {
        sW.setPosition(0.65);
    }

    public void down() {
        sW.setPosition(0.365);
    }
}