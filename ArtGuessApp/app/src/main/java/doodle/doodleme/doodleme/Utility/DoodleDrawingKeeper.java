package doodle.doodleme.doodleme.Utility;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface DoodleDrawingKeeper extends Serializable {
    void keepResult(String doodleName, boolean couldGuess, Bitmap userDrawing);
}
