package com.color.dictatethenfirebaseidunnowhatever;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.typeEditText) EditText typeEditText;
    @Bind(R.id.enteredSentencesRecyclerView) RecyclerView enteredSentencesRecyclerView;
    @Bind(R.id.progressBarTimer) ProgressBar progressBarTimer;
    @Bind(R.id.endBtn) Button endBtn;
    @Bind(R.id.startBtn) Button startBtn;
    @Bind(R.id.clearBtn) ImageView clearBtn;
    @Bind(R.id.sendBtn) ImageView sendBtn;
    private final String CONVERSATIONS = "Conversation";
    public static final String ONNODE = "OnNode";
    private final int MAX_I = 7000;
    private final int TIME_INTERVAL = 10;
    private String currentWord = "";

    private InitTask IT = null;
    private int i = MAX_I;
    private boolean hasPausedTimer = false;
    private boolean cancelTimerEntirely = false;
    private boolean hasTimerStarted = false;

    private String id;
    private Sentence enteredSentence;
    private List<Sentence> allSentences = new ArrayList<>();
    private String oldWords = "";
    private int lastChar = 0;
    private boolean isClearingEditText = false;
    private boolean isSendingViaButton = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(CONVERSATIONS);
        final DatabaseReference pushRef = ref.push();
        id = pushRef.getKey();

        if(!typeEditText.getText().toString().trim().equals("")){
            clearBtn.setAlpha(1f);
        }else{
            clearBtn.setAlpha(0.4f);
        }

        typeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.e("MainActivity","beforeTextChanged");
//                Log.e("MainActivity","charSequence: "+charSequence.toString());
//                Log.e("MainActivity","int i: "+i);
//                Log.e("MainActivity","int i1: "+i1);
//                Log.e("MainActivity","int i2: "+i2);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("MainActivity","onTextChanged");
                Log.e("MainActivity","charSequence: "+charSequence.toString());
                Log.e("MainActivity","int i: "+i);
                Log.e("MainActivity","int i1: "+i1);
                Log.e("MainActivity","int i2: "+i2);

                if(!typeEditText.getText().toString().trim().equals("")){
                    clearBtn.setAlpha(1f);
                }else{
                    clearBtn.setAlpha(0.4f);
                }

                if(enteredSentence!=null) {
                    int numberOfErases = charSequence.subSequence(i,charSequence.length()).length()+i2;
                    Log.e("MainActivity", "number of erases: " + numberOfErases);
                    String changedWords = charSequence.subSequence(i, charSequence.length()).toString();
                    Log.e("MainActivity", "changed words: " + changedWords);
                }

                String word = charSequence.toString();
                Log.e("MainActivity","word: "+word);

                try{
                    enteredSentence= new Sentence(id,word);
                    enteredSentence.setBackSpace(i1);
                    enteredSentence.setOldSentence(oldWords);

                    currentWord = word;
                    oldWords = word;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("MainActivity","afterTextChanged: "+editable.toString());
                enteredSentence.setBackSpace(0);
                enteredSentence.setWordsToAdd("");
                if(enteredSentence!=null) {
                    for (int i = 0; i < enteredSentence.getOldSentence().length(); i++) {
                        if(editable.toString().length()>i && enteredSentence.getOldSentence().length()>i) {
                            if (editable.toString().charAt(i) != enteredSentence.getOldSentence().charAt(i)) {
                                int char2erase = editable.toString().substring(i).length() - 1;
                                String charr = editable.toString().substring(i);
                                enteredSentence.setBackSpace(char2erase);
                                enteredSentence.setWordsToAdd(charr);
                                Log.e("MainActivity", "number of erases to make: " + char2erase);
                                Log.e("MainActivity", "characters to write: " + charr);
                                break;
                            }
                        }
                    }
                }
                if(!isClearingEditText){
//                    pushRef.setValue(enteredSentence);
                }else{
                    isClearingEditText = false;
                }

                if (!currentWord.equals("")){
                    cancelTimerEntirely = true;
                    progressBarTimer.setProgress(140);
                    i = MAX_I;

                    hasTimerStarted = false;
                    startTimer();
                }else{
                    cancelTimerEntirely = true;
                    progressBarTimer.setProgress(140);
                    i = MAX_I;
                }
            }
        });


        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredSentence= new Sentence(id,"xxxxxxxx");
                enteredSentence.setBackSpace(0);
                enteredSentence.setOldSentence(oldWords);

                pushRef.setValue(enteredSentence);
                Toast.makeText(getApplicationContext(),"Listener Ended",Toast.LENGTH_SHORT).show();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!typeEditText.getText().toString().trim().equals("")) {
                    isClearingEditText = true;
                    typeEditText.setText("");
                }
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endBtn.performClick();
                enteredSentence= new Sentence(id,"xxxxxxxx");

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ONNODE);
                final DatabaseReference pushRef = ref.push();
                pushRef.setValue(enteredSentence);

                Toast.makeText(getApplicationContext(),"Listener Started",Toast.LENGTH_SHORT).show();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredSentence.setSentence(typeEditText.getText().toString().trim());
                enteredSentence.setOldSentence("");
                pushRef.setValue(enteredSentence);
                enteredSentence.setOldSentence(typeEditText.getText().toString().trim());
                setSentenceInRecyclerView();
            }
        });
    }

    private void startTimer(){
        if(!isSendingViaButton){
            if(!hasTimerStarted) {
                cancelTimerEntirely = false;
                IT = new InitTask();
                IT.execute();
            }
        }
    }

    protected class InitTask extends AsyncTask<Context, Integer, String> {
        // -- gets called just before thread begins
        @Override
        protected void onPreExecute() {
            hasTimerStarted = true;
            progressBarTimer.setProgress(140);
            i = MAX_I;
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
            if(!currentWord.equals("")) {
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
        boolean canSet = false;
        if(allSentences.size()!=0){
            Sentence lastS = allSentences.get(allSentences.size()-1);
            if(!enteredSentence.getSentence().equals(lastS.getSentence())){
                canSet = true;
            }
        }else canSet = true;

        if(!enteredSentence.getSentence().equals("") && canSet) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            enteredSentence.setColor(color);
            allSentences.add(enteredSentence);
//            Toast.makeText(getApplicationContext(), "added: " + enteredSentence.getSentence(), Toast.LENGTH_SHORT).show();
            SentenceAdapter senAdap = new SentenceAdapter(allSentences, MainActivity.this);
            enteredSentencesRecyclerView.setAdapter(senAdap);
            LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
            llm.setStackFromEnd(true);
            enteredSentencesRecyclerView.setLayoutManager(llm);

//            typeEditText.setText("");
            currentWord = "";

            progressBarTimer.setProgress(140);
            i = MAX_I;
        }
    }

    private void Log(String tag,String message){
        try{
            Log.d(tag,message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
