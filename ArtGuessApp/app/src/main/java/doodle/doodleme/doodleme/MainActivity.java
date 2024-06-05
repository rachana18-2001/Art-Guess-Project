package doodle.doodleme.doodleme;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import doodle.doodleme.doodleme.CustomData.ResultData;
import doodle.doodleme.doodleme.Fragments.DoodleDrawingFragment;
import doodle.doodleme.doodleme.Fragments.ResultFragment;
import doodle.doodleme.doodleme.Utility.Constants;
import doodle.doodleme.doodleme.Utility.DoodleDrawingKeeper;

public class MainActivity extends AppCompatActivity implements DoodleDrawingKeeper {

    TextView textViewCounter;
    TextView textViewDoodleName;

    private ArrayList<String> questions;//store questions
    private ArrayList<ResultData> resultData;//store results

    private int questionNumber;//current question number
    private String doodleName;//current doodle name

    private DoodleDrawingFragment fragment;
    private ResultFragment resultFragment;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the number of tasks passed down
        int totalNumberOfTasks = getIntent().getIntExtra(Constants.TOTAL_NUMBER_OF_TASKS, 5);

        resultData = new ArrayList<>();

        // randomly choose TOTAL_NUMBER_OF_TASKS samples
        ArrayList<String> labels = getLabels(Constants.MODEL_LABEL_FILE_NAME);
        Collections.shuffle(labels); // shuffle the list then pick desired number of categories
        questions = new ArrayList<>();
        for(int i = 0; i< totalNumberOfTasks; i++)
            questions.add(labels.get(i));

        // show the first doodle name
        textViewDoodleName = findViewById(R.id.txvDoodleName);
        questionNumber = 0;
        doodleName = questions.get(questionNumber);
        textViewDoodleName.setText(doodleName);

        textViewCounter = findViewById(R.id.txvCounter);
        textViewCounter.setText((questionNumber+1) + " / " + questions.size());


        Button buttonStart = findViewById(R.id.btnStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment = DoodleDrawingFragment.newInstance(doodleName, MainActivity.this);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top,
                        R.anim.enter_from_top, R.anim.exit_to_top);
                transaction.addToBackStack(null);
                transaction.add(R.id.fragmentContainer, fragment, "DoodleDrawingFragment").commit();


            }
        });

    }

    @Override
    public void keepResult(String doodleName, boolean couldGuess, Bitmap userDrawing) {
        // save the result
        resultData.add(new ResultData(doodleName, String.valueOf(couldGuess), bitmapToString(userDrawing)));

        questionNumber++; // increment the questionNumber by 1
        textViewCounter.setText((questionNumber+1) + " / " + questions.size());

        if(questionNumber < questions.size()){
            // if more questions are left, fetch the new one
            doodleName = questions.get(questionNumber);
            this.doodleName = doodleName;
            // update the view
            textViewDoodleName.setText(doodleName);
        } else{
            textViewDoodleName.setText("");
            // the game is finished, open result fragment
            resultFragment = ResultFragment.newInstance(resultData);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top,
                    R.anim.enter_from_top, R.anim.exit_to_top);
            transaction.addToBackStack(null);
            transaction.add(R.id.fragmentContainer, resultFragment, "ResultFragment").commit();
        }

    }

    public String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    // read the labels from the asset folder
    private ArrayList<String> getLabels(String fileName){

        try{
            BufferedReader abc = new BufferedReader(
                    new InputStreamReader(getAssets().open(fileName)));
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

    @Override
    public void onBackPressed() {
        if(fragment != null && fragment.isVisible())
            fragment.onBackPressed();
        else if(resultFragment != null && resultFragment.isVisible())
            resultFragment.onBackPressed();
        else {
            startActivity(new Intent(MainActivity.this, LandingActivity.class));
            MainActivity.this.finish();
        }
    }

    public static String join(String[] arr, String separator) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = arr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(separator);
            sbStr.append(arr[i]);
        }
        return sbStr.toString();
    }

}
