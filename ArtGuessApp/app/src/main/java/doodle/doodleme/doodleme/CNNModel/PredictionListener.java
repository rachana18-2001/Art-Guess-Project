package doodle.doodleme.doodleme.CNNModel;

import java.util.ArrayList;

import doodle.doodleme.doodleme.CustomData.LabelProbability;

public interface PredictionListener {
    void predictionCallback(ArrayList<LabelProbability> topPredictions);
}
