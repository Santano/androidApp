package com.ankco.numfall;

import com.example.numfall.R;

import model.LevelInfo;
import databaseHelper.LevelAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends FragmentActivity {
	Button startButton;
	Button scoreButton;
	Button helpButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		startButton = (Button) findViewById(R.id.playButton);
		scoreButton = (Button) findViewById(R.id.levelButton);
		helpButton = (Button) findViewById(R.id.helpButton);
		startButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				startGame();
			}
		});
		scoreButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				showHighScores();
			}
		});
		helpButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				showHelpPage();
			}
		});
	}

	protected void showHelpPage() {
		Intent intent = new Intent("com.example.numfall.HelpActivity");
		startActivity(intent);
	}

	protected void showHighScores() {
		Intent intent = new Intent("com.example.numfall.LevelActivity");
		startActivity(intent);
	}

	protected void startGame() {
		LevelAdapter mDbHelper = new LevelAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		LevelInfo currentLevel = mDbHelper.getNextLevel();
		CurrentLevel.setCurrentLevel(currentLevel);
		mDbHelper.close();
		Intent intent = new Intent("com.example.numfall.GamePlayActivity");
		startActivity(intent);
	}
}
