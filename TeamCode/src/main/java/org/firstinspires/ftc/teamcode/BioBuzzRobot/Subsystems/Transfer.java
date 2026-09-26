package org.firstinspires.ftc.teamcode.BioBuzzRobot.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Transfer extends SubsystemBase {

    private Servo sG;
    public boolean gate_Open = false;

    public Transfer(HardwareMap hardwareMap) {
        sG = hardwareMap.get(Servo.class, "sG");
        closeGate();
    }

    @Override
    public void periodic() {

    }

    public void openGate() {
        sG.setPosition(BotPositions.Transfer.OPEN);
        gate_Open = true;
    }

    public void closeGate() {
        sG.setPosition(BotPositions.Transfer.CLOSE);
        gate_Open = false;
    }
}
