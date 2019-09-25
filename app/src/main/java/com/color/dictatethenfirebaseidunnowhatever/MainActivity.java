package com.color.dictatethenfirebaseidunnowhatever;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.typeEditText) EditText typeEditText;
    @Bind(R.id.enteredSentencesRecyclerView) RecyclerView enteredSentencesRecyclerView;
    @Bind(R.id.progressBarTimer) ProgressBar progressBarTimer;
    private final String CONVERSATIONS = "Conversation";
    private final int MAX_I = 7000;
    private final int TIME_INTERVAL = 10;
    private String oldText = "";

    private InitTask IT = null;
    private int i = MAX_I;
    private boolean hasPausedTimer = false;
    private boolean cancelTimerEntirely = false;
    private boolean hasTimerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(CONVERSATIONS);
        final DatabaseReference pushRef = ref.push();
        final String id = pushRef.getKey();

        typeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = charSequence.toString();
                try{
                    Sentence enteredSentence= new Sentence(id,word);
                    enteredSentence.setOldSentence(oldText);
                    pushRef.setValue(enteredSentence);
                    oldText = word;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!typeEditText.getText().toString().equals("")){
                    cancelTimerEntirely = true;
                    progressBarTimer.setProgress(140);
                    i = MAX_I;

                    hasTimerStarted = false;
                    startTimer();
                }else if(typeEditText.getText().toString().equals("")){
                    cancelTimerEntirely = true;
                    progressBarTimer.setProgress(140);
                    i = MAX_I;
                }
            }
        });

        if (!typeEditText.getText().toString().equals("") && hasTimerStarted){
            startTimer();
        }
    }

    private void startTimer(){
        cancelTimerEntirely = false;
        IT = new InitTask();
        IT.execute();
    }

    protected class InitTask extends AsyncTask<Context, Integer, String> {
        // -- gets called just before thread begins
        @Override
        protected void onPreExecute() {
            hasTimerStarted = true;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            while (i > 0 && !hasPausedTimer && !cancelTimerEntirely) {
                try {
                    Thread.sleep(TIME_INTERVAL);
                    i -= 50;
                    publishProgress(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "COMPLETE!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(!typeEditText.getText().toString().equals("")) {
                progressBarTimer.incrementProgressBy(-1);
            }else{
                cancelTimerEntirely = true;
                progressBarTimer.setProgress(140);
                i = MAX_I;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!hasPausedTimer && !cancelTimerEntirely){
                resetTimer();
                Log("Timer --- ", "Timer has finished");
            }
            cancelTimerEntirely = false;
            hasTimerStarted = false;
        }
    }

    private void resetTimer() {
        cancelTimerEntirely = true;
        new InitTask2().execute();
    }

    protected class InitTask2 extends AsyncTask<Context, Integer, String> {
        // -- gets called just before thread begins
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            int j = 0;
            while (j <= 140) {
                try {
                    Thread.sleep(TIME_INTERVAL);
                    j += 10;
                    publishProgress(j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "COMPLETE!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBarTimer.incrementProgressBy(10);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBarTimer.setProgress(140);
            i = MAX_I;
            hasTimerStarted = false;
            setSentenceInRecyclerView();
        }

    }

    private void setSentenceInRecyclerView() {

    }

    private void Log(String tag,String message){
        try{
            Log.d(tag,message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
