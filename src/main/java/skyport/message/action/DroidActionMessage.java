package skyport.message.action;

import java.util.List;

import skyport.game.Direction;

public class DroidActionMessage extends ActionMessage {
    private List<Direction> sequence;
    
    public List<Direction> getPath() {
        return sequence;
    }

    public void setPath(List<Direction> sequence) {
       this.sequence = sequence;
    }
}
