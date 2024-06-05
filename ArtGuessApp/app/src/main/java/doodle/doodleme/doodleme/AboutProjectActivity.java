package doodle.doodleme.doodleme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class AboutProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_project);

        String projectDescription = "\"Doodle Me\" project is CNN (Convolutional Neural Network) based deep learning model which can distinguish 123 different doodle categories.  \n" +
                "\n" +
                "Thanks to Google for providing 50M doodles of over 300 categories, this project just uses a fraction of those data to operate. The CNN architecture designed to train and predict doodles can be found in the Github Repo.\n" +
                "The CNN model present in this Android App is trained with more than 0.605M 28x28 unique images and tested against 0.132M 28x28 unique images, and can classify amongst 88 different doodle categories. More categories with a better model can be added in a future version.\n" +
                "The performance of the model is well tested, check out the Github Repo for a detailed explanation.\n" +
                "\n" +
                "The main challenge faced in this project was the conversion of the high-resolution doodle image (drawn by user) into a 28x28 image without losing valuable information. After lots of trial and error, finally, Glide is used to lower the image resolution, along with a bit image manipulation, check out the Github Repo for more info.\n" +
                "\n" +
                "The final v6-28-123 model running in this android app was trained for 15 epochs with 0.605 unique images while being tested against 0.132M unique images, the model achieved an accuracy of 68.29% (about 90K correct classification), which is well, enough to start playing the grand game of Pictionary with a machine.";

        ((TextView)findViewById(R.id.txvProjectDescription)).setText(projectDescription);

        String licenseAndCopyWriteStuff = "The Quick, Draw! Dataset has a collection of 50 Million drawings across 345 categories. This data is made available by Google, Inc. under the Creative Commons Attribution 4.0 International license. Click on this text to view the Github Repo of googlecreativelab.";

        TextView textViewLicense = findViewById(R.id.txvAttribution);
        textViewLicense.setText(licenseAndCopyWriteStuff);
        textViewLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewOnGithub = new Intent(Intent.ACTION_VIEW);
                viewOnGithub.setData(Uri.parse("https://github.com/googlecreativelab/quickdraw-dataset"));
                startActivity(viewOnGithub);
            }
        });

        (findViewById(R.id.txvLicense)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewOnGithub = new Intent(Intent.ACTION_VIEW);
                viewOnGithub.setData(Uri.parse("https://creativecommons.org/licenses/by/4.0"));
                startActivity(viewOnGithub);
            }
        });

    }
}
