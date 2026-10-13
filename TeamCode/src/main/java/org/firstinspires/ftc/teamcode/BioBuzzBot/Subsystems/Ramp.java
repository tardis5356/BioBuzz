package org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Ramp extends SubsystemBase {

    Servo sR;

    public Ramp(HardwareMap hardwareMap) {
        sR = hardwareMap.get(Servo.class, "sR");
        retract();
    }


    @Override
    public void periodic() {

    }

    public void deploy() {
        sR.setPosition(BotPositions.Ramp.FRONT);
    }


    public void retract() {
        sR.setPosition(BotPositions.Ramp.RETRACT);
    }

}
