package com.color.dictatethenfirebaseidunnowhatever;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.ViewHolder> {
    List<Sentence> allSentences;
    private Activity mActivity;

    public SentenceAdapter(List<Sentence> sentences, Activity activity) {
        allSentences = sentences;
        this.mActivity = activity;
    }

    private List<Sentence> getAllSentences(){
        return allSentences;
    }

    @NonNull
    @Override
    public SentenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View recipeView = inflater.inflate(R.layout.sentence_item_layout, viewGroup, false);
        return new SentenceAdapter.ViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceAdapter.ViewHolder viewHolder, int i) {
        final Sentence sentence = allSentences.get(i);
        viewHolder.sentenceText.setText(sentence.getSentence());


        viewHolder.backView.setBackgroundColor(sentence.getColor());

//        if(i%3==1 && i!=1){
//            viewHolder.emptyOneView.setVisibility(View.VISIBLE);
//        }else{
//            viewHolder.emptyOneView.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return allSentences.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView sentenceText;
        View backView;
        View emptyOneView;

        ViewHolder(View itemView) {
            super(itemView);
            sentenceText = itemView.findViewById(R.id.sentenceText);
            backView = itemView.findViewById(R.id.backView);
            emptyOneView = itemView.findViewById(R.id.emptyOneView);
        }
    }
}
