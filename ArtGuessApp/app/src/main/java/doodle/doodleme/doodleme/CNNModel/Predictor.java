package doodle.doodleme.doodleme.CNNModel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

import doodle.doodleme.doodleme.CustomData.LabelProbability;
import doodle.doodleme.doodleme.Utility.ProbabilitySorter;

public class Predictor {

    private PredictionListener predictionListener;

    private Context context;
    private ArrayList<String> labels;
    private Interpreter tflite;

    private ArrayList<LabelProbability> topPredictions;

    /* minimum 30 black pixels are needed before making a prediction*/
    private final int MIN_DRAWN_PIXEL = 35; /* out of 784 pixels */

    // CONSTRUCTOR
    // initialize the model here
    public Predictor(String tensorflowLiteModelName, String labelFileName,Context context, PredictionListener predictionListener){
        this.context = context;
        this.predictionListener = predictionListener;
        // load the tflite model
        try{
            tflite = new Interpreter(loadModel(tensorflowLiteModelName));
        } catch (Exception e){
            e.printStackTrace();
        }
        // read the labels
        labels = readLabels(labelFileName);
    }

    // predict method --> returns the top n predictions
    public void predict(Bitmap rawBitmap, final int numOfPredictions){

        // array for storing result returned by tflite model
        // 2D array with size 1 x MODEL_OUTPUT_SIZE
        final float[][] predictions = new float[1][labels.size()];
        final ArrayList<LabelProbability> list = new ArrayList<>();
        topPredictions = new ArrayList<>();

        rawBitmap = Bitmap.createScaledBitmap(rawBitmap, 840,840,false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rawBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        // resize the image to 28 x 28 using Glide
        Glide.with(context)
                .asBitmap()
                .load(stream.toByteArray())
                .override(28,28)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {

                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        int[] pixels = new int[width * height];
                        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                        float[] modelInputPixel = new float[pixels.length];
                        int blackPixelCount = 0;
                        for(int i=0; i<pixels.length; i++){
                            // turning white pixels to 1.0
                            modelInputPixel[i] = (float)(0xff&pixels[i])/255;
                            // anything other than white is converted to 0 (black)
                            if(modelInputPixel[i] != 1.0){
                                modelInputPixel[i] = 0;
                                blackPixelCount++; // count black pixels
                            }
                        }

                        // if less than MIN_DRAWN_PIXEL number of pixels are drawn
                        // then it's pointless to predict something useful from it
                        if(blackPixelCount < MIN_DRAWN_PIXEL){
                            return;
                        }

                        // get output from tflite model
                        tflite.run(reshapeToFourDimension(modelInputPixel, 28), predictions);

                        for(int i=0; i<labels.size(); i++){
                            list.add(new LabelProbability(predictions[0][i], labels.get(i)));
                        }

                        // sorting labels on descending order of prediction values
                        Collections.sort(list, new ProbabilitySorter());

                        for(int i=0; i<numOfPredictions; i++){
                            topPredictions.add(list.get(i));
                        }

                        // invoke the predictionCallback method
                        predictionListener.predictionCallback(topPredictions);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    // well, numpy.reshape is pretty easy, but the below method is just
    // the implementation for converting a 1-dimensional array of size n to 1 x sqrt(n) x sqrt(n) x 1 dimensions
    // why you ask?
    // well, tensorflow takes a 4D array as input
    private float[][][][] reshapeToFourDimension(float[] arr,int size){
        float[][][][] newArr = new float[1][size][size][1];
        int k=0;
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                newArr[0][i][j][0] = arr[k];
                k++;
            }
        }
        return newArr;
    }

    // read the labels from the asset folder
    private ArrayList<String> readLabels(String fileName){

        try{
            BufferedReader abc = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while((line = abc.readLine()) != null) {
                lines.add(line);
            }
            abc.close();
            return lines;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // load the tflite model from asset folder
    private MappedByteBuffer loadModel(String fileName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(fileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

}
