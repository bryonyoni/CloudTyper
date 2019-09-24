package com.color.dictatethenfirebaseidunnowhatever;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.typeEditText) EditText typeEditText;
    private final String CONVERSATIONS = "Conversation";
    private String oldText = "";

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

            }
        });
    }
}
