/*
    Assignment:   Homework 2
    File:         MainActivity.java
    Author:       Daniel Christopher Hirt
 */

package com.example.daniel.group6_hw02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GetQuestionsAsyncTask.IData
{
    ProgressDialog _ProgressDialog;
    private ImageView imageTrivia;
    private TextView lblTriviaLoadStatus;
    private Button btnStartTrivia;
    private Button btnExit;
    private Button btnRetry;
    public static final String TAG = "HW2";
    public static final String QUESTIONS_KEY = "QUESTIONS";
    private final String TRIVIA_URL = "http://dev.theappsdr.com/apis/trivia_json/index.php";
    private ArrayList<Question> questions = new ArrayList<Question>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupListeners();

        attemptConnection();
    }

    private void setupViews()
    {
        imageTrivia = (ImageView)findViewById(R.id.imageTrivia);
        lblTriviaLoadStatus = (TextView)findViewById(R.id.lblTriviaLoadStatus);
        btnStartTrivia = (Button)findViewById(R.id.btnStartTrivia);
        btnExit = (Button)findViewById(R.id.btnExit);
        btnRetry = (Button)findViewById(R.id.btnRetry);
    }

    private void setupListeners()
    {
        btnExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        btnStartTrivia.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent triviaIntent = new Intent(MainActivity.this, TriviaActivity.class);
                triviaIntent.putExtra(QUESTIONS_KEY, questions);

                startActivity(triviaIntent);
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                attemptConnection();
            }
        });
    }

    private void attemptConnection()
    {
        if(isConnectedOnline())
        {
            btnRetry.setVisibility(View.INVISIBLE);
            new GetQuestionsAsyncTask(this).execute(TRIVIA_URL);
        }
        else
        {
            lblTriviaLoadStatus.setText(getString(R.string.label_connection_error));
            btnRetry.setVisibility(View.VISIBLE);
        }
    }

    private boolean isConnectedOnline()
    {
        ConnectivityManager _ConnectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo _NetworkInfo = _ConnectivityManager.getActiveNetworkInfo();

        return (_NetworkInfo != null) && (_NetworkInfo.isConnected());
    }

    @Override
    public void setTriviaReady(boolean ready)
    {
        if(ready)
        {
            _ProgressDialog.dismiss();
            imageTrivia.setVisibility(View.VISIBLE);
            lblTriviaLoadStatus.setText(getString(R.string.label_trivia_ready));
            btnStartTrivia.setEnabled(true);
        }
        else
        {
            _ProgressDialog = new ProgressDialog(this);
            _ProgressDialog.setCancelable(false);
            _ProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _ProgressDialog.show();
            imageTrivia.setVisibility(View.INVISIBLE);
            lblTriviaLoadStatus.setText(getString(R.string.label_loading_trivia));
            btnStartTrivia.setEnabled(false);
        }
    }

    @Override
    public void setQuestions(ArrayList<Question> questions)
    {
        this.questions = questions;
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            if (_ProgressDialog != null && _ProgressDialog.isShowing())
            {
                _ProgressDialog.dismiss();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}