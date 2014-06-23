package skyport.message.graphics;

import java.util.Arrays;
import java.util.List;

import skyport.message.Message;

public class HighlightMessage extends Message {
    @SuppressWarnings("unused")
    private String coordinate;
    @SuppressWarnings("unused")
    private List<Integer> color;

    public HighlightMessage(String position, int r, int g, int b) {
        this.message = "highlight";
        this.coordinate = position;
        this.color = Arrays.asList(r, g, b);
    }
}
