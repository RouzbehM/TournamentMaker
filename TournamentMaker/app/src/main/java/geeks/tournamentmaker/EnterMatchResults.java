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
import android.widget.EditText;

public class EnterMatchResults extends ActionBarActivity {

    private TournamentDBHelper dbHelper;
    private int matchID;
    private int tournamentID;
    private EditText score1;
    private EditText score2;
    private String teamName1;
    private String teamName2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_match_results);

        //get match info
        Intent intent = getIntent();
        matchID = intent.getIntExtra("matchID",-1);
        tournamentID = intent.getIntExtra("tournamentID",-1);

        //query database for the teams involved in the match
        dbHelper = new TournamentDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.MatchEntry.COLUMN_NAME_TEAM1,
                TournamentContract.MatchEntry.COLUMN_NAME_TEAM2
        };
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
        //get the team names from the cursor
        c.moveToFirst();
        teamName1 = c.getString(c.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1));
        teamName2 = c.getString(c.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2));
        c.close();

        //display the team names in the score entry fields
        score1 = (EditText)findViewById(R.id.homeTeamScore);
        score2 = (EditText)findViewById(R.id.awayTeamScore);
        score1.setHint(teamName1);
        score2.setHint(teamName2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add menu items
        getMenuInflater().inflate(R.menu.menu_enter_match_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_help){
            Intent intent = new Intent(this, ViewHelp.class);
            startActivity(intent);
        }
        else if(id == R.id.action_about){
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }else if(id == android.R.id.home){
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed(){
        //return to viewing the current tournament details
        Intent intent = new Intent(this, ViewTournament.class);
        intent.putExtra("tournamentID",tournamentID);
        startActivity(intent);
    }


    public void onClick(View v){
        //Get the scores entered by the user
        String score1Text;
        String score2Text;
        score1Text = score1.getText().toString();
        score2Text = score2.getText().toString();

        if(!score1Text.equals("")&&!score2Text.equals("")) {
            int scoreTeam1 = Integer.parseInt(score1Text);
            int scoreTeam2 = Integer.parseInt(score2Text);

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String selection = TournamentContract.MatchEntry._ID + " = ?";
            String[] selectionArgs = {"" + matchID};
            //enter match data into map of values
            ContentValues values = new ContentValues();
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1, scoreTeam1);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2, scoreTeam2);
            //assuming there will be no ties
            if (scoreTeam1 > scoreTeam2) {
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, teamName1);
            } else {
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, teamName2);
            }
            //update the match entry with the new data
            db.update(
                    TournamentContract.MatchEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();
            //return to viewing the current tournament details
            Intent myIntent = new Intent(this, ViewTournament.class);
            myIntent.putExtra("tournamentID", tournamentID);
            startActivity(myIntent);
        }else{
            DialogHelper.makeLongToast(this,"Please enter 2 scores");
        }
    }

}
