package doodle.doodleme.doodleme.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import doodle.doodleme.doodleme.CustomData.ResultData;
import doodle.doodleme.doodleme.R;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    ArrayList<ResultData> list;
    Context context;

    public ResultAdapter(Context context, ArrayList<ResultData> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_item,
                viewGroup, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder resultViewHolder, int i) {

        ResultData resultData = list.get(i);

        resultViewHolder.imageViewDoodle.setImageBitmap(stringToBitmap(resultData.getUserDrawing()));
        resultViewHolder.textViewDoodleName.setText(resultData.getDoodleName());

        String doodlePrediction = resultData.getCouldGuess();
        if(doodlePrediction.equals(String.valueOf(true))){
            resultViewHolder.imageViewDoodlePredictionStatus.setImageResource(R.drawable.ic_correct);
        } else{
            resultViewHolder.imageViewDoodlePredictionStatus.setImageResource(R.drawable.ic_wrong);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder{

        TextView textViewDoodleName;
        ImageView imageViewDoodle;
        ImageView imageViewDoodlePredictionStatus;


        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDoodleName = itemView.findViewById(R.id.txvDoodleName);
            imageViewDoodle = itemView.findViewById(R.id.imvDoodle);
            imageViewDoodlePredictionStatus = itemView.findViewById(R.id.imvDoodlePredictionStatus);

        }
    }

    public Bitmap stringToBitmap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}
