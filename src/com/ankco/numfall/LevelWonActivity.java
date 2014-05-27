package com.ankco.numfall;

import com.example.numfall.R;

import model.LevelInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import databaseHelper.LevelAdapter;

public class LevelWonActivity extends FragmentActivity {

	private LevelAdapter mDbHelper;
	Button menuButton;
	Button retryButton;
	Button nextButton;
	private static int bestScoreInt = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new LevelAdapter(this);
		setContentView(R.layout.level_won);
		LevelInfo currentLevelInfo = CurrentLevel.getCurrentLevel();
		int currScoreInt = CurrentLevel.getCurrScore();
		if (currentLevelInfo.getUserRating() == 0
				|| currentLevelInfo.getUserRating() > currScoreInt)
			bestScoreInt = currScoreInt;
		else
			bestScoreInt = currentLevelInfo.getUserRating();
		String bestScoreString = getStringFromTime(bestScoreInt);
		String currScoreString = getStringFromTime(currScoreInt);
		TextView currScore = (TextView) findViewById(R.id.timeCurrentValue);
		TextView bestScore = (TextView) findViewById(R.id.timeHighValue);
		currScore.setText(currScoreString);
		bestScore.setText(bestScoreString);

		menuButton = (Button) findViewById(R.id.levelMenu);
		retryButton = (Button) findViewById(R.id.levelRetry);
		nextButton = (Button) findViewById(R.id.levelNext);
		menuButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				saveGame();
				Intent intent = new Intent("com.example.numfall.MenuActivity");
				startActivity(intent);
			}
		});
		retryButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"com.example.numfall.GamePlayActivity");
				startActivity(intent);
			}
		});
		nextButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				saveGame();
				Intent intent = new Intent(
						"com.example.numfall.GamePlayActivity");
				startActivity(intent);
			}
		});
	}

	private void saveGame() {
		LevelInfo currentLevel = CurrentLevel.getCurrentLevel();
		mDbHelper.open();
		mDbHelper.setLevelToComplete(currentLevel.getId(), bestScoreInt);
		currentLevel = mDbHelper.getLevelFromLevelId(currentLevel.getId() + 1);
		CurrentLevel.setCurrentLevel(currentLevel);
		mDbHelper.close();
	}

	private String getStringFromTime(int totalTime) {
		String timeRemainingString = String.valueOf(totalTime / 60);
		int sec = totalTime % 60;
		if (sec < 10)
			timeRemainingString = timeRemainingString + ":0" + sec;
		else
			timeRemainingString = timeRemainingString + ":" + sec;
		return timeRemainingString;
	}
}
