package doodle.doodleme.doodleme.CustomData;

import java.io.Serializable;


public class ResultData implements Serializable {

    String doodleName;
    String couldGuess;
    String userDrawing;

    public ResultData(String doodleName, String couldGuess, String userDrawing) {
        this.doodleName = doodleName;
        this.couldGuess = couldGuess;
        this.userDrawing = userDrawing;
    }

    public String getDoodleName() {
        return doodleName;
    }

    public String getCouldGuess() {
        return couldGuess;
    }

    public String getUserDrawing() {
        return userDrawing;
    }

    @Override
    public String toString() {
        return doodleName + ": " + couldGuess;
    }

}
