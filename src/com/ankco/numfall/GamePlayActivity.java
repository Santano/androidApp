package com.ankco.numfall;

import com.example.numfall.R;

import model.LevelInfo;
import resource.Constants;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GamePlayActivity extends FragmentActivity {
	private LevelInfo currentLevel;
	private TextView timeRemainingTextView;
	private TextView movesRemainingTextView;
	private TextView levelHeaderTextView;
	private TextView currentHighlight = null;
	private TextView userScoreTextView;
	private TextView highScore;
	private Button pauseButton;
	private Button restartButton;
	private TextView[][] allCells = new TextView[Constants.rows][Constants.cols];
	private int numCount = 0;
	private int symCount = 0;
	private MyCount timeRemainingThread;
	private int secondsCompleted;
	private int movesRemaining;
	private int currentHighlightRow;
	private int currentHighlightCol;
	private int userScoreValue = 0;
	private Boolean pauseRowShiftTimer = false;
	private Boolean isGamePaused = false;
	private String timeRemainingString = "";
	private long roundTimeLeft = 0;
	private Boolean isLevelComplete = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentLevel = CurrentLevel.getCurrentLevel();
		movesRemaining = currentLevel.getNumberOfMoves();
		setContentView(R.layout.game);
		generateBoard(this);
		loadView(currentLevel);
		timerHandler.postDelayed(timerRunnable, currentLevel.getDropSpeed());
	}

	private void setRemainingRoundTimeString(int secondsElapsed) {
		int totalTime = (currentLevel.getRoundTime() * 60 - secondsElapsed);
		timeRemainingString = String.valueOf(totalTime / 60);
		int sec = totalTime % 60;
		if (sec < 10)
			timeRemainingString = timeRemainingString + ":0" + sec;
		else
			timeRemainingString = timeRemainingString + ":" + sec;
	}

	public void generateBoard(GamePlayActivity activity) {
		TableLayout gameBoard = (TableLayout) findViewById(R.id.board);
		highScore = (TextView) findViewById(R.id.highScoreValue);
		movesRemainingTextView = (TextView) findViewById(R.id.starValue);
		timeRemainingTextView = (TextView) findViewById(R.id.timeValue);
		levelHeaderTextView = (TextView) findViewById(R.id.levelValue);
		pauseButton = (Button) findViewById(R.id.playpause);
		restartButton = (Button) findViewById(R.id.restart);
		pauseButton.setText("Pause");
		pauseButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				pauseOrPlayGame();
			}
		});
		restartButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				restartGame(currentLevel);
			}
		});
		userScoreTextView = (TextView) findViewById(R.id.scoreValue);
		for (int i = 0; i < Constants.rows; i++) {
			TableRow row = new TableRow(activity);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(lp);
			row.setGravity(Gravity.CENTER_HORIZONTAL);
			for (int j = 0; j < Constants.cols; j++) {
				TextView txtviewCell = new TextView(activity);
				TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(
						60, 60);
				txtviewCell.setBackgroundColor(0xfffaf8ef);
				txtviewCell.setGravity(Gravity.CENTER);
				paramsExample.setMargins(5, 5, 5, 5);
				txtviewCell.setLayoutParams(paramsExample);
				final int currRow = i;
				final int currCol = j;
				txtviewCell.setOnTouchListener(new OnSwipeTouchListener(
						activity) {
					public void onSwipeTop() {
						handleSwipe(currRow, currCol, currRow - 1, currCol,
								currRow - 2, currCol, false);
					}

					public void onSwipeRight() {
						handleSwipe(currRow, currCol, currRow, currCol + 1,
								currRow, currCol + 2, true);
					}

					public void onSwipeLeft() {
						handleSwipe(currRow, currCol, currRow, currCol - 1,
								currRow, currCol - 2, true);
					}

					public void onSwipeBottom() {
						handleSwipe(currRow, currCol, currRow + 1, currCol,
								currRow + 2, currCol, false);
					}

					public void onNormalTouch() {
						handleTouchEvent(currRow, currCol);
					}

					public boolean onTouch(View v, MotionEvent event) {
						return gestureDetector.onTouchEvent(event);
					}
				});
				allCells[i][j] = txtviewCell;
				row.addView(txtviewCell);
			}
			gameBoard.addView(row);
		}
	}

	public void loadView(LevelInfo currLevel) {
		isLevelComplete = false;
		highScore.setText(String.valueOf(currentLevel.getOneStartargetScore()));
		if (isGamePaused)
			pauseOrPlayGame();
		movesRemaining = currLevel.getNumberOfMoves();
		movesRemainingTextView.setText(String.valueOf(movesRemaining));
		setRemainingRoundTimeString(0);
		for (int i = 0; i < Constants.rows; i++) {
			for (int j = 0; j < Constants.cols; j++) {
				allCells[i][j].setText("");
			}
		}
		if (currentHighlight != null) {
			currentHighlight.setBackgroundColor(Constants.lightCream);
			currentHighlight = null;
		}
		if (timeRemainingThread != null)
			timeRemainingThread.cancel();
		secondsCompleted = 0;
		timeRemainingThread = new MyCount(
				currentLevel.getRoundTime() * 60 * 1000 * 2, 1000);
		timeRemainingThread.start();
		levelHeaderTextView.setText("Level - "
				+ String.valueOf(currLevel.getId()));
		timeRemainingTextView.setText(timeRemainingString);
		userScoreValue = 0;
		userScoreTextView.setText(String.valueOf(userScoreValue));
		generateRow(0);
	}

	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			if (!pauseRowShiftTimer) {
				shiftOneRowBelow();
				generateRow(0);
			}
			timerHandler.postDelayed(this, currentLevel.getDropSpeed());
		}
	};

	public void pauseOrPlayGame() {
		if (isGamePaused) {
			pauseButton.setText("Pause");
			timeRemainingThread = new MyCount(roundTimeLeft * 2, 1000);
			timeRemainingThread.start();
			pauseRowShiftTimer = false;
		} else {
			pauseButton.setText("play");
			timeRemainingThread.cancel();
			pauseRowShiftTimer = true;
		}
		isGamePaused = !isGamePaused;
	}

	public void restartGame(LevelInfo level) {
		loadView(level);
	}

	public void handleTouchEvent(int currRow, int currCol) {
		if (isGamePaused)
			return;
		if (allCells[currRow][currCol].getText().equals(""))
			return;
		if (currentHighlight == null) {
			currentHighlightRow = currRow;
			currentHighlightCol = currCol;
			currentHighlight = allCells[currRow][currCol];
			currentHighlight.setBackgroundColor(Constants.lightBrown);
			return;
		}
		currentHighlight.setBackgroundColor(Constants.lightCream);
		if (currCol == currentHighlightCol && currRow == currentHighlightRow) {
			currentHighlight = null;
			return;
		}
		if (!isNeighborOfPreviousHighlight(currRow, currCol)) {
			currentHighlightRow = currRow;
			currentHighlightCol = currCol;
			currentHighlight = allCells[currRow][currCol];
			currentHighlight.setBackgroundColor(Constants.lightBrown);
			return;
		} else {
			if (movesRemaining <= 0)
				return;
			currentHighlight = null;
			CharSequence temp = allCells[currentHighlightRow][currentHighlightCol]
					.getText();
			allCells[currentHighlightRow][currentHighlightCol]
					.setText(allCells[currRow][currCol].getText());
			allCells[currRow][currCol].setText(temp);
			movesRemaining--;
			movesRemainingTextView.setText(String.valueOf(movesRemaining));
		}
	}

	private boolean isNeighborOfPreviousHighlight(int currRow, int currCol) {
		if (currRow == currentHighlightRow
				&& (currCol == currentHighlightCol - 1 || currCol == currentHighlightCol + 1)) {
			return true;
		} else if (currCol == currentHighlightCol
				&& (currRow == currentHighlightRow - 1 || currRow == currentHighlightRow + 1)) {
			return true;
		}
		return false;
	}

	public void levelComplete() {
		stopAllTimers();
		if (userScoreValue >= currentLevel.getOneStartargetScore())
			userWon();
		else
			userLost();
	}

	private void stopAllTimers() {
		timeRemainingThread.cancel();
	}

	private void userLost() {
		new RetryBox().show(getSupportFragmentManager(), "Retry");
	}

	private void userWon() {
		CurrentLevel.setCurrScore(secondsCompleted);
		Intent intent = new Intent("com.example.numfall.LevelWonActivity");
		startActivity(intent);
		
	}

	public void generateRow(int row) {
		for (int i = 0; i < Constants.cols; i++) {
			if (!allCells[row][i].getText().equals(""))
				continue;
			int randomNumber = ((int) (Math.random() * 100)) % 14;
			if (symCount <= numCount / 3) {
				while (randomNumber < 10)
					randomNumber = ((int) (Math.random() * 100)) % 14;
			} else if (symCount >= numCount / 2) {
				while (randomNumber > 10 || randomNumber == 0)
					randomNumber = ((int) (Math.random() * 100)) % 14;
			}
			if (randomNumber == 0 || randomNumber > 9)
				symCount++;
			else
				numCount++;
			allCells[row][i].setText(Constants.symbolArray.get(randomNumber));
		}
	}

	private void handleSwipe(int row1, int col1, int row2, int col2, int row3,
			int col3, boolean isHorizontal) {
		if (isGamePaused)
			return;
		Boolean isCorrectSwipe = validateSwipe(row1, col1, row2, col2, row3,
				col3);
		if (!isCorrectSwipe)
			return;
		pauseRowShiftTimer = true;
		int result = getResult(row1, col1, row2, col2, row3, col3);
		allCells[row3][col3].setText(String.valueOf(result));
		if (isHorizontal) {
			if (result == 0)
				shiftForLeftOrRightSwipe(row1, col3, col1);
			else
				shiftForLeftOrRightSwipe(row1, col2, col1);
			generateRow(0);
		} else {
			if (result == 0)
				shiftForTopDownSwipe(col1, row3, row1);
			else
				shiftForTopDownSwipe(col1, row2, row1);
			generateRow(0);
			generateRow(1);
			generateRow(2);
		}
		allCells[currentHighlightRow][currentHighlightCol]
				.setBackgroundColor(Constants.lightCream);
		currentHighlight = null;
		pauseRowShiftTimer = false;
		if (isLevelComplete)
			levelComplete();
	}

	private Boolean validateSwipe(int row1, int col1, int row2, int col2,
			int row3, int col3) {
		if (row1 < 0 || col1 < 0 || row3 >= Constants.rows
				|| col3 >= Constants.cols)
			return false;
		if (allCells[row1][col1].getText().toString().matches("^-?[0-9]\\d*")
				&& allCells[row3][col3].getText().toString()
						.matches("^-?[0-9]\\d*")
				&& !allCells[row2][col2].getText().toString()
						.matches("^-?[0-9]\\d*")) {
			int i1 = Integer
					.parseInt(allCells[row1][col1].getText().toString());
			int i2 = Integer
					.parseInt(allCells[row3][col3].getText().toString());
			if (!allCells[row2][col2].getText().equals("/"))
				return true;
			if (allCells[row2][col2].getText().equals("/") && i1 % i2 == 0)
				return true;
			else
				return false;
		}
		return false;
	}

	public void shiftOneRowBelow() {
		Boolean shouldShiftHighlight = false;
		for (int i = 0; i < Constants.cols; i++) {
			int lastRow = Constants.rows - 1;
			while (!allCells[lastRow][i].getText().equals("") && lastRow > 0)
				lastRow--;
			if (lastRow == 0)
				continue;
			CharSequence prev = "";
			int j = 0;
			while (j <= lastRow) {
				CharSequence temp = allCells[j][i].getText();
				allCells[j][i].setText(prev);
				if (!shouldShiftHighlight && currentHighlight != null
						&& j - 1 == currentHighlightRow
						&& i == currentHighlightCol)
					shouldShiftHighlight = true;
				prev = temp;
				j++;
			}
		}
		if (shouldShiftHighlight)
			shiftHighlightCellBelow();
	}

	private void shiftHighlightCellBelow() {
		allCells[currentHighlightRow][currentHighlightCol]
				.setBackgroundColor(Constants.lightCream);
		currentHighlightRow++;
		currentHighlight = allCells[currentHighlightRow][currentHighlightCol];
		allCells[currentHighlightRow][currentHighlightCol]
				.setBackgroundColor(Constants.lightBrown);
	}

	private void shiftForLeftOrRightSwipe(int row, int col1, int col2) {
		int colStart = col1 > col2 ? col2 : col1;
		int colEnd = col1 > col2 ? col1 : col2;
		for (int i = colStart; i <= colEnd; i++) {
			CharSequence prev = "";
			int r = 0;
			while (r <= row) {
				CharSequence temp = allCells[r][i].getText();
				allCells[r][i].setText(prev);
				prev = temp;
				r++;
			}
		}
	}

	private void shiftForTopDownSwipe(int col, int row1, int row2) {
		int i = row1 > row2 ? row1 : row2;
		int j = row1 < row2 ? row1 : row2;
		j--;
		while (j >= 0) {
			allCells[i][col].setText(allCells[j][col].getText());
			i--;
			j--;
		}
		while (i >= 0) {
			allCells[i][col].setText("");
			i--;
		}
	}

	private int getResult(int row1, int col1, int row2, int col2, int row3,
			int col3) {
		int i1 = Integer.parseInt(allCells[row1][col1].getText().toString());
		int i2 = Integer.parseInt(allCells[row3][col3].getText().toString());
		String sym = allCells[row2][col2].getText().toString();
		int result = 0;
		if (sym.equals("+"))
			result = i1 + i2;
		else if (sym.equals("-"))
			result = i1 - i2;
		else if (sym.equals("X"))
			result = i1 * i2;
		else
			result = i1 / i2;
		userScoreValue = userScoreValue + Math.abs(i1) + Math.abs(i2);
		userScoreTextView.setText(String.valueOf(userScoreValue));
		if (userScoreValue >= currentLevel.getOneStartargetScore())
			isLevelComplete = true;
		return result % 1000;
	}

	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			levelComplete();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			roundTimeLeft = millisUntilFinished;
			secondsCompleted++;
			setRemainingRoundTimeString(secondsCompleted);
			timeRemainingTextView.setText(timeRemainingString);
			if (secondsCompleted == currentLevel.getRoundTime() * 60)
				levelComplete();
		}
	}
}
