package org.firstinspires.ftc.teamcode.BioBuzzBot.Commands;

import com.pedropathing.follower.Follower;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;

public class PathCompletionMarkerCommand extends CommandBase {

   Command command;
   double completionMarker;
   Follower follower;

    public PathCompletionMarkerCommand(Command command,double completionMarker, Follower follower) {
     this.command = command;
     this.completionMarker = completionMarker;
     this.follower = follower;
    }

    @Override
    public void initialize() {
        if (follower.completion() > completionMarker) {
            command.schedule();
        }
    }


    @Override
    public boolean isFinished() {
        if (follower.completion() > completionMarker) {
            return true;
        }
        return false;
    }

}