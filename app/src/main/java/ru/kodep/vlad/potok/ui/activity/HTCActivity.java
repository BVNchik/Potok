package ru.kodep.vlad.potok.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ru.kodep.vlad.potok.R;

public class HTCActivity extends AppCompatActivity {
    TextView tvNamePerson, tvNumberPerson, tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htc);
        tvNamePerson = findViewById(R.id.tvNamePerson);
        tvNumberPerson = findViewById(R.id.tvNumberPerson);
        tvTitle = findViewById(R.id.tvTitle);
    }


}
