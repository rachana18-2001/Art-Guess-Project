package doodle.doodleme.doodleme.Fragments;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import doodle.doodleme.doodleme.CNNModel.PredictionListener;
import doodle.doodleme.doodleme.CNNModel.Predictor;
import doodle.doodleme.doodleme.CustomData.LabelProbability;
import doodle.doodleme.doodleme.R;
import doodle.doodleme.doodleme.Utility.Constants;
import doodle.doodleme.doodleme.Utility.DoodleDrawingKeeper;
import doodle.doodleme.doodleme.Views.DrawModel;
import doodle.doodleme.doodleme.Views.DrawRenderer;
import doodle.doodleme.doodleme.Views.DrawingCanvas;

public class DoodleDrawingFragment extends Fragment implements View.OnTouchListener, PredictionListener {

    private DoodleDrawingKeeper doodleDrawingKeeper;

    private final int MILLIS_BETWEEN_PREDICTIONS = 500;
    private final float MIN_THRESHOLD_PROBABILITY_FOR_CORRECT_PREDICTION = (float) 0.40;
    private final float MIN_THRESHOLD_PROBABILITY = (float) 0.15;
    private final int NUMBER_OF_TOP_PREDICTIONS = 4;
    private final int TIME_PER_DOODLE_DRAWING = 30; /* in seconds */

    private TextToSpeech speechEngine;
    private String doodleName;
    private boolean stopPredicting;

    private DrawingCanvas drawingCanvas;
    private DrawModel drawModel;
    private PointF mTmpPoint = new PointF();
    private float mLastX;
    private float mLastY;

    private Predictor predictor;
    private Random random;
    private long nextPredictionTime = 0;

    private TextView textViewPredictions;
    private TextView textViewCountDownTimer;
    private TextView textViewDoodleName;

    private CountDownTimer countDownTimer;

    public static DoodleDrawingFragment newInstance(String doodleName, DoodleDrawingKeeper doodleDrawingKeeper) {
        DoodleDrawingFragment fragment = new DoodleDrawingFragment();
        Bundle args = new Bundle();
        args.putString(Constants.DOODLE_NAME, doodleName);
        args.putSerializable(Constants.DOODLE_RESULT_KEEPER_INTERFACE, doodleDrawingKeeper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doodleName = getArguments().getString(Constants.DOODLE_NAME);
            doodleDrawingKeeper = (DoodleDrawingKeeper) getArguments()
                    .getSerializable(Constants.DOODLE_RESULT_KEEPER_INTERFACE);
        }

        // instantiate the predictor
        predictor = new Predictor(Constants.TFLITE_MODEL_NAME,
                Constants.MODEL_LABEL_FILE_NAME, getContext(), this);

        // initialize the speech engine
        speechEngine = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = speechEngine.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(getContext(),
                                "Speech not supported", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // speech engine settings
        speechEngine.setSpeechRate(0.9f);
        speechEngine.setPitch(1f);

        // instantiate for random number generation
        random = new Random();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doodle_drawing, container, false);

        textViewPredictions = view.findViewById(R.id.txvPredictions);

        // method to initialize the canvas drawing and our cnn model
        initializeCanvas(view);

        // the button to clear out the canvas
        ImageView imageViewClearCanvas = view.findViewById(R.id.imvClear);
        imageViewClearCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawModel.clear();
                drawingCanvas.reset();
                drawingCanvas.invalidate();
                textViewPredictions.setText("");
            }
        });

        ImageView imageViewClose = view.findViewById(R.id.imvClose);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // well if user is quitting -- he could not draw the doodle
                doodleDrawingKeeper.keepResult(doodleName, false, drawingCanvas.getBitmap());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
                        .remove(DoodleDrawingFragment.this).commit();
                countDownTimer.cancel();

            }
        });

        textViewCountDownTimer = view.findViewById(R.id.txvCountDownTimer);

        // start a count down timer to maintain time during game play
        countDownTimer = new CountDownTimer(1000*TIME_PER_DOODLE_DRAWING + 500 /* bonus time */,1000 /* 1s */){
            @Override
            public void onTick(long millisUntilFinished) {
                textViewCountDownTimer.setText(String.valueOf(millisUntilFinished/1000 /* seconds until finished */));
            }

            @Override
            public void onFinish() {

                stopPredicting = true; // no further predictions will be made
                speechEngine.speak("Sorry! I couldn't guess that!", TextToSpeech.QUEUE_FLUSH, null);

                // well if time has run up --- the user could not have drawn the correct doodle
                doodleDrawingKeeper.keepResult(doodleName, false, drawingCanvas.getBitmap());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
                        .remove(DoodleDrawingFragment.this).commit();

            }
        }.start();

        textViewDoodleName = view.findViewById(R.id.txvDoodleName);
        textViewDoodleName.setText(doodleName);

        return view;
    }

    // after a prediction is made this method is invoked
    @Override
    public void predictionCallback(ArrayList<LabelProbability> topPredictions) {

        // only if the said doodle shows up in the top position with probability more than
        // MIN_THRESHOLD_PROBABILITY_FOR_CORRECT_PREDICTION then consider it a correct drawing
        for(int i=0; i<topPredictions.size(); i++){
            if(topPredictions.get(i).getLabelName().equals(doodleName) &&
                    topPredictions.get(i).getProbability() >= MIN_THRESHOLD_PROBABILITY_FOR_CORRECT_PREDICTION){
                countDownTimer.cancel();
                handleSuccess(doodleName);
                return;
            }
        }

        if(!stopPredicting)
            handleFailure(topPredictions);

    }

    // if the doodle is correctly drawn, this method is invoked
    private void handleSuccess(final String doodleName){
        stopPredicting = true; // the model won't predict anything now

        String[] precedingSpeech = {
                "Oh I know! It's ",
                "Gotcha, it's ",
                "I got it, it's ",
                "Oh I got it, it's ",
                "Cool! It's ",
                "Oh gotcha, it's "
        };

        String finalSpeech = precedingSpeech[random.nextInt(precedingSpeech.length)] + doodleName;

        speechEngine.speak(
                finalSpeech,
                TextToSpeech.QUEUE_FLUSH, null);

        textViewPredictions.setText(finalSpeech);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // user successfully guessed the doodle
                doodleDrawingKeeper.keepResult(doodleName, true, drawingCanvas.getBitmap());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
                        .remove(DoodleDrawingFragment.this).commit();
            }
        }, 2000); // wait for 2 second before quiting

    }

    // if the drawn doodle is not correct, this method is invoked
    private void handleFailure(ArrayList<LabelProbability> predictions){

        String[] precedingSpeech = {
                "Well, I see ",
                "I can see ",
                "I guess it's ",
                "I see "
        };

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<predictions.size(); i++){
            if(!predictions.get(i).getLabelName().equals(doodleName) && predictions.get(i).getProbability() > MIN_THRESHOLD_PROBABILITY){
                builder.append(predictions.get(i).getLabelName()).append(", or ");
            }
        }

        if(!builder.toString().isEmpty()){
            String finalSpeech = precedingSpeech[random.nextInt(precedingSpeech.length)] +
                    builder.substring(0, builder.length()-5);

            textViewPredictions.setText(finalSpeech);
            speechEngine.speak(finalSpeech, TextToSpeech.QUEUE_FLUSH, null);

        } else{

            String[] couldNotGuessDrawingSpeech = {
                    "I have no idea what you are drawing!",
                    "What is this? No clue!",
                    "I am paranoid by your drawing!"
            };

            String finalSpeech = couldNotGuessDrawingSpeech[random.nextInt(couldNotGuessDrawingSpeech.length)];

            textViewPredictions.setText(finalSpeech);
            speechEngine.speak(finalSpeech,
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeCanvas(View view){

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float screenDensity = metrics.density;
        /*
        *  y = 10 + (x - 2.0)/0.075
        *
        *  well i am doing this because,
        *  with my phone of 2.0 density, width 10f works best but
        *  on my other phone with density 2.75, width 20f works best
        *  thus to generalize the relation I'm assuming the relation had to be linear
        *
        * */
        DrawRenderer.setStrokeWidth(10 + ((screenDensity-(float)2.0)/(float)0.075));

        drawingCanvas = view.findViewById(R.id.drawingCanvas);

        drawModel = new DrawModel(metrics.widthPixels, (int)((double)metrics.heightPixels*0.70));

        drawingCanvas.setModel(drawModel);
        drawingCanvas.setOnTouchListener(DoodleDrawingFragment.this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            processTouchDown(event);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    private void processTouchDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        drawingCanvas.calcPos(mLastX, mLastY, mTmpPoint);
        float lastConvX = mTmpPoint.x;
        float lastConvY = mTmpPoint.y;
        drawModel.startLine(lastConvX, lastConvY);
    }

    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        drawingCanvas.calcPos(x, y, mTmpPoint);
        float newConvX = mTmpPoint.x;
        float newConvY = mTmpPoint.y;
        drawModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        drawingCanvas.invalidate();
    }

    private void processTouchUp() {
        drawModel.endLine();

        long currentTime = System.currentTimeMillis();

        if(!stopPredicting)
            if(currentTime > nextPredictionTime){
                // this allows the prediction to happen only after a certain time period
                nextPredictionTime = currentTime + MILLIS_BETWEEN_PREDICTIONS;
                predictor.predict(drawingCanvas.getBitmap(),
                        NUMBER_OF_TOP_PREDICTIONS /* number of top predictions to fetch*/ );
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        drawingCanvas.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        drawingCanvas.onPause();
    }

    public void onBackPressed(){
        // well if user is quitting -- he could not draw the doodle
        doodleDrawingKeeper.keepResult(doodleName, false, drawingCanvas.getBitmap());
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
                .remove(DoodleDrawingFragment.this).commit();
        countDownTimer.cancel();
    }

}
