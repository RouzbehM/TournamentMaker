package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EnterMatchResults extends ActionBarActivity {

    Button submitResults;
    private TournamentDBHelper dbHelper;
    private int matchID;
    private int tournamentID;
    private EditText score1;
    private EditText score2;
    String teamName1;
    String teamName2;

    public void onClick(View v){
        int scoreTeam1 = Integer.parseInt(score1.getText().toString());
        int scoreTeam2 = Integer.parseInt(score2.getText().toString());

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            ContentValues values = new ContentValues();
            String selection = TournamentContract.MatchEntry._ID + " = ?";
            String[] selectionArgs = {"" + matchID};

            values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1, scoreTeam1);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2, scoreTeam2);
            if(scoreTeam1 > scoreTeam2){
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, teamName1);
            } else {
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, teamName2);
            }

            db.update(
                    TournamentContract.MatchEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
            Intent myIntent = new Intent(this, ViewTournament.class);
            myIntent.putExtra("tournamentID", tournamentID);
            startActivity(myIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_match_results);
        dbHelper = new TournamentDBHelper(this);
        submitResults = (Button)findViewById(R.id.submitScores);
        Intent intent = getIntent();
        matchID = intent.getIntExtra("matchID",-1);
        tournamentID = intent.getIntExtra("tournamentID",-1);

        String[] projection = {TournamentContract.MatchEntry.COLUMN_NAME_TEAM1,
                TournamentContract.MatchEntry.COLUMN_NAME_TEAM2
        };
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TournamentContract.MatchEntry._ID + " = ?";
        String[] selectionArgs = {"" + matchID};
        Cursor c = db.query(
                TournamentContract.MatchEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        c.moveToFirst();
        teamName1 = c.getString(c.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1));
        teamName2 = c.getString(c.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2));
        c.close();
        score1 = (EditText)findViewById(R.id.homeTeamScore);
        score2 = (EditText)findViewById(R.id.awayTeamScore);
        score1.setHint(teamName1);
        score2.setHint(teamName2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_match_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_help){
            Intent intent = new Intent(this, ViewHelp.class);
            startActivity(intent);
        }
        else if(id == R.id.action_about){
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
