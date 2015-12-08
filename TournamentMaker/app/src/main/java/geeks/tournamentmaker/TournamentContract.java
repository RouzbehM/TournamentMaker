package geeks.tournamentmaker;

import android.provider.BaseColumns;

/**
 * The TournamentContract class is used for defining database tables for the app.
 */
public final class TournamentContract {
    public TournamentContract(){}
    public static abstract class TournamentEntry implements BaseColumns {
        public static final String TABLE_NAME = "tournaments";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TEAMS = "teams";
    }
    public static abstract class MatchEntry implements BaseColumns{
        public static final String TABLE_NAME = "matches";
        public static final String COLUMN_NAME_TOURNAMENT_ID = "tournamentid";
        public static final String COLUMN_NAME_TEAM1 = "team1";
        public static final String COLUMN_NAME_TEAM2 = "team2";
        public static final String COLUMN_NAME_SCORE1 = "score1";
        public static final String COLUMN_NAME_SCORE2 = "score2";
        public static final String COLUMN_NAME_WINNER = "winner";
    }
    public static abstract class TeamEntry implements BaseColumns{
        public static final String TABLE_NAME = "teams";
        public static final String COLUMN_NAME_NAME = "name";
    }
}

