package com.ankco.numfall;

import java.util.List;

import com.example.numfall.R;

import model.LevelInfo;
import model.UserRating;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import databaseHelper.LevelAdapter;

public class LevelActivity extends FragmentActivity {

	private TableLayout levelLayout;
	private LevelAdapter mDbHelper;
	private List<UserRating> userRatings;
	private TableRow currRow = null;
	private Button backToMenu;
	private Boolean lockall = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level);
		backToMenu = (Button) findViewById(R.id.backToMenu);
		backToMenu.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.example.numfall.MenuActivity");
				startActivity(intent);
			}
		});
		levelLayout = (TableLayout) findViewById(R.id.levelLayout);
		mDbHelper = new LevelAdapter(this);
		mDbHelper.createDatabase();
		mDbHelper.open();
		userRatings = mDbHelper.getAllUserRating();
		mDbHelper.close();
		int numRatings = 1;

		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 10, 10, 10);
		for (UserRating userRating : userRatings) {
			if (numRatings % 3 == 1) {
				if (currRow != null)
					levelLayout.addView(currRow);
				currRow = new TableRow(this);
				currRow.setLayoutParams(lp);
				currRow.setGravity(Gravity.CENTER_HORIZONTAL);
			}
			TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(
					185, 60);
			paramsExample.setMargins(10, 10, 10, 10);
			TextView txtviewCell = new TextView(this);
			txtviewCell.setGravity(Gravity.CENTER);
			txtviewCell.setLayoutParams(paramsExample);
			if (lockall)
				txtviewCell.setBackgroundResource(R.drawable.lock);
			else {
				txtviewCell.setBackgroundColor(0xffbbada0);
				final int levelNum = numRatings;
				txtviewCell.setText(String.valueOf("Level "
						+ userRating.getLevel_id()));
				txtviewCell.setOnTouchListener(new OnSwipeTouchListener(this) {
					public void onSwipeTop() {
						handleTouchEvent(levelNum);
					}

					public void onSwipeRight() {
						handleTouchEvent(levelNum);
					}

					public void onSwipeLeft() {
						handleTouchEvent(levelNum);
					}

					public void onSwipeBottom() {
						handleTouchEvent(levelNum);
					}

					public void onNormalTouch() {
						handleTouchEvent(levelNum);
					}

					public boolean onTouch(View v, MotionEvent event) {
						return gestureDetector.onTouchEvent(event);
					}
				});
			}
			if (userRating.getUser_rating() == 0)
				lockall = true;
			currRow.addView(txtviewCell);
			numRatings++;
		}
		levelLayout.addView(currRow);
	}

	private void handleTouchEvent(int numRatings) {
		mDbHelper.open();
		LevelInfo clickedLevel = mDbHelper.getLevelFromLevelId(numRatings);
		mDbHelper.close();
		CurrentLevel.setCurrentLevel(clickedLevel);
		Intent intent = new Intent("com.example.numfall.GamePlayActivity");
		startActivity(intent);
	}
}
