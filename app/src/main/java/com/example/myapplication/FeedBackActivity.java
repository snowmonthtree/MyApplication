package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Controller.FeedBackController;
import com.example.myapplication.data.ViewSharer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FeedBackActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private FeedBackController feedBackController;
    private EditText editText;
    private Button button;
    private ViewSharer viewSharer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed_back);

        try {
            retrofit = RetrofitClient.getClient(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        viewSharer=(ViewSharer)getApplication();
        feedBackController=retrofit.create(FeedBackController.class);
        editText=findViewById(R.id.feedbackEditText);
        button=findViewById(R.id.submitButton);
        button.setOnClickListener(view -> submit());
    }
    private void submit(){
        Call<String> call=feedBackController.addFeedback(viewSharer.getUser().getUserId(),editText.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(FeedBackActivity.this, "1"+response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(FeedBackActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}