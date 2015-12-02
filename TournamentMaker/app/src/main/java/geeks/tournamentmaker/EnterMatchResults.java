package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterMatchResults extends ActionBarActivity {

    Button submitResults;
    private TournamentDBHelper dbHelper;
    private int matchID;

    public void onClick(View v){
        if(v == submitResults) {
        EditText score1 = (EditText)findViewById(R.id.homeTeamScore);
        EditText score2 = (EditText)findViewById(R.id.awayTeamScore);

        int scoreTeam1 = Integer.parseInt(score1.getText().toString());
        int scoreTeam2 = Integer.parseInt(score2.getText().toString());

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            ContentValues values = new ContentValues();
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1, scoreTeam1);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2, scoreTeam2);
            if(scoreTeam1 > scoreTeam2){
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, TournamentContract.MatchEntry.COLUMN_NAME_TEAM1);
            } else {
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, TournamentContract.MatchEntry.COLUMN_NAME_TEAM2);
            }

            String selection = TournamentContract.MatchEntry._ID + " LIKE ?";
            String[] selectionArgs = { matchID+"" };

            db.update(
                    TournamentContract.MatchEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();


        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_match_results);
        submitResults = (Button)findViewById(R.id.submitScores);
        submitResults.setOnClickListener((View.OnClickListener)EnterMatchResults.this);


        Intent intent = getIntent();
        matchID = intent.getIntExtra("matchID",-1);

        dbHelper = new TournamentDBHelper(this);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
