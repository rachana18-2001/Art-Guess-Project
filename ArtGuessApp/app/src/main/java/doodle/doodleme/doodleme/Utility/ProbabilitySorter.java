package doodle.doodleme.doodleme.Utility;

import java.util.Comparator;

import doodle.doodleme.doodleme.CustomData.LabelProbability;

public class ProbabilitySorter implements Comparator<LabelProbability> {

    @Override
    public int compare(LabelProbability o1, LabelProbability o2) {
        float change1 = o1.getProbability();
        float change2 = o2.getProbability();
        if (change1 < change2) return 1;
        if (change1 > change2) return -1;
        return 0;
    }
}
