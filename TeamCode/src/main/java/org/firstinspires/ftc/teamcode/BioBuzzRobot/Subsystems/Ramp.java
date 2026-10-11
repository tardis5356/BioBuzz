package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Ramp extends SubsystemBase {

    Servo sR;

    public enum Ramp_State {FRONT, BACK, RETRACTED}

    public static Ramp_State rampState = Ramp_State.RETRACTED;

    public Ramp(HardwareMap hardwareMap) {
        sR = hardwareMap.get(Servo.class, "sR");
        retract();
    }


    @Override
    public void periodic() {

    }

    public void deploy() {
        sR.setPosition(BotPositions.Ramp.FRONT);
        rampState = Ramp_State.FRONT;
    }


    public void retract() {
        sR.setPosition(BotPositions.Ramp.RETRACT);
        rampState = Ramp_State.RETRACTED;
    }

}
