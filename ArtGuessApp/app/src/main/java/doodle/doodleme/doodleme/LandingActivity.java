package doodle.doodleme.doodleme;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import doodle.doodleme.doodleme.Utility.Constants;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] numberOfTasks = { "3", "5", "7", "11"};//, "13", "17", "19", "23", "29" };

                AlertDialog.Builder builder = new AlertDialog.Builder(LandingActivity.this);
                builder.setTitle("Play with how many doodles?");
                builder.setItems(numberOfTasks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
                        intent.putExtra(Constants.TOTAL_NUMBER_OF_TASKS,
                                Integer.parseInt(numberOfTasks[which]));
                        startActivity(intent);
                        LandingActivity.this.finish();
                    }
                });
                builder.show();
            }
        });

       /* findViewById(R.id.btnAboutProject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, AboutProjectActivity.class));
            }
        });

        findViewById(R.id.btnGithub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewOnGithub = new Intent(Intent.ACTION_VIEW);
                viewOnGithub.setData(Uri.parse("https://github.com/jyotirmoy-paul/DoodleMe"));
                startActivity(viewOnGithub);
            }
        });*/


    }
}
