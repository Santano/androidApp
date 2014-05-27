package databaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.LevelInfo;
import model.UserRating;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LevelAdapter {
	protected static final String TAG = "DataAdapter";

	private final Context mContext;
	private SQLiteDatabase mDb;
	private DataBaseHelper mDbHelper;

	public LevelAdapter(Context context) {
		this.mContext = context;
		mDbHelper = new DataBaseHelper(mContext);
	}

	public LevelAdapter createDatabase() throws SQLException {
		try {
			mDbHelper.createDataBase();
		} catch (IOException mIOException) {
			Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public LevelAdapter open() throws SQLException {
		try {
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getReadableDatabase();
		} catch (SQLException mSQLException) {
			Log.e(TAG, "open >>" + mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public LevelInfo getNextLevel() {
		try {
			String sql = "select * from levels where user_rating = 0 order by _id limit 1;";
			Cursor cursor = mDb.rawQuery(sql, null);
			cursor.moveToFirst();
			return currToLevel(cursor);
		} catch (Exception e) {
			return null;
		}
	}

	public LevelInfo getLevelFromLevelId(int levelID) {
		try {
			String sql = "select * from levels where _id = " + levelID + ";";
			Cursor cursor = mDb.rawQuery(sql, null);
			cursor.moveToFirst();
			return currToLevel(cursor);
		} catch (Exception e) {
			return null;
		}
	}

	public void setLevelToComplete(int id, int userRating) {
		try {
			String sql = "update levels set user_rating = " + userRating
					+ " where _id = " + id + ";";
			Cursor cursor = mDb.rawQuery(sql, null);
			cursor.moveToFirst();
			cursor.close();
		} catch (Exception e) {
		}
	}

	public List<UserRating> getAllUserRating() {
		ArrayList<UserRating> ratingList = new ArrayList<UserRating>();
		String sql = "select _id, user_rating from levels order by _id;";
		Cursor cursor = mDb.rawQuery(sql, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserRating userRating = currToUserRating(cursor);
			ratingList.add(userRating);
			cursor.moveToNext();
		}
		cursor.close();
		return ratingList;
	}

	private UserRating currToUserRating(Cursor cursor) {
		UserRating userRating = new UserRating();
		userRating.setLevel_id(cursor.getInt(0));
		userRating.setUser_rating(cursor.getInt(1));
		return userRating;
	}

	private LevelInfo currToLevel(Cursor mCur) {
		LevelInfo level = new LevelInfo();
		level.setId(mCur.getInt(0));
		level.setOneStartargetScore(mCur.getInt(1));
		level.setNumberOfMoves(mCur.getInt(2));
		level.setRoundTime(mCur.getInt(3));
		level.setDropSpeed(mCur.getInt(4));
		level.setUserRating(mCur.getInt(5));
		return level;
	}
}