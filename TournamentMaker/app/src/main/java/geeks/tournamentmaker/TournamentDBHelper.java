package geeks.tournamentmaker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The TournamentDBHelper class is used for assisting with database operations.
 */
public class TournamentDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TournamentMaker.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TOURNAMENT_TABLE =
        "CREATE TABLE " + TournamentContract.TournamentEntry.TABLE_NAME + " (" +
            TournamentContract.TournamentEntry._ID + " INTEGER PRIMARY KEY," +
            TournamentContract.TournamentEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            TournamentContract.TournamentEntry.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
            TournamentContract.TournamentEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
            TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS + TEXT_TYPE +
        " )";

    private static final String SQL_CREATE_TEAM_TABLE =
        "CREATE TABLE " + TournamentContract.TeamEntry.TABLE_NAME + " (" +
            TournamentContract.TeamEntry._ID + " INTEGER PRIMARY KEY," +
            TournamentContract.TeamEntry.COLUMN_NAME_NAME + TEXT_TYPE +
        " )";

    private static final String SQL_CREATE_MATCH_TABLE =
        "CREATE TABLE " + TournamentContract.MatchEntry.TABLE_NAME + " (" +
            TournamentContract.MatchEntry._ID + " INTEGER PRIMARY KEY," +
            TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID + " INTEGER" + COMMA_SEP +
            TournamentContract.MatchEntry.COLUMN_NAME_SCORE1 + TEXT_TYPE + COMMA_SEP +
            TournamentContract.MatchEntry.COLUMN_NAME_SCORE2 + TEXT_TYPE + COMMA_SEP +
            TournamentContract.MatchEntry.COLUMN_NAME_TEAM1 + TEXT_TYPE + COMMA_SEP +
            TournamentContract.MatchEntry.COLUMN_NAME_TEAM2 + TEXT_TYPE + COMMA_SEP +
            TournamentContract.MatchEntry.COLUMN_NAME_WINNER + TEXT_TYPE +
        " )";

    private static final String SQL_DELETE_TOURNAMENT_ENTRIES =
        "DROP TABLE IF EXISTS " + TournamentContract.TournamentEntry.TABLE_NAME;

    private static final String SQL_DELETE_TEAM_ENTRIES =
        "DROP TABLE IF EXISTS " + TournamentContract.TeamEntry.TABLE_NAME;
    private static final String SQL_DELETE_MATCH_ENTRIES =
        "DROP TABLE IF EXISTS " + TournamentContract.MatchEntry.TABLE_NAME;

    public TournamentDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MATCH_TABLE);
        db.execSQL(SQL_CREATE_TEAM_TABLE);
        db.execSQL(SQL_CREATE_TOURNAMENT_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TOURNAMENT_ENTRIES);
        db.execSQL(SQL_DELETE_TEAM_ENTRIES);
        db.execSQL(SQL_DELETE_MATCH_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
