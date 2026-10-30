package org.firstinspires.ftc.teamcode.BioBuzzBot.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Wedge extends SubsystemBase {

    Servo sW;

    public Wedge(HardwareMap hardwareMap) {
        sW = hardwareMap.get(Servo.class, "sW");
        retract();
    }


    @Override
    public void periodic() {

    }

    public void deploy() {
        sW.setPosition(BotPositions.Ramp.FRONT);
    }


    public void retract() {
        sW.setPosition(BotPositions.Ramp.RETRACT);
    }

}
