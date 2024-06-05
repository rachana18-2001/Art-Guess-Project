package doodle.doodleme.doodleme.CustomData;

public class LabelProbability {

    private float probability;
    private String labelName;

    public LabelProbability(float probability, String labelName){
        this.probability = probability;
        this.labelName = labelName;
    }

    public float getProbability() {
        return probability;
    }

    public String getLabelName(){
        return labelName;
    }

    @Override
    public String toString() {
        return labelName + ": " + probability;
    }
}
