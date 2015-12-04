package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddMatchActivity extends ActionBarActivity {
    private TournamentDBHelper dbHelper;
    private int tournamentID;
    private ArrayList teams = new ArrayList();
    private Spinner spinner1;
    private Spinner spinner2;
    private EditText score1;
    private EditText score2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get the EditTexts used for entering the scores
        score1 = (EditText)findViewById(R.id.team1score);
        score2 = (EditText)findViewById(R.id.team2score);

        //get the Spinners used for selecting teams
        spinner1 = (Spinner) findViewById(R.id.team1spinner);
        spinner2 = (Spinner) findViewById(R.id.team2spinner);

        //get tournament info
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID", 0);

        //get the list of teams for the current tournament
        dbHelper = new TournamentDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS};
        String[] selectionArgs = {""+tournamentID};
        Cursor c = db.query(
                TournamentContract.TournamentEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TournamentContract.TournamentEntry._ID + " =?",   // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );

        if (c.moveToFirst()) {//cursor is not empty
            try {
                String teamString = c.getString(c.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS));
                if(teamString!=null) {
                    //The teams array is stored as a JSON object
                    JSONObject json = new JSONObject(teamString);
                    JSONArray teamsArray = json.optJSONArray("teams");
                    for (int i = 0; i < teamsArray.length(); i++) {
                        //populate the list used for keeping track of all the teams
                        teams.add(teamsArray.getString(i));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();

        //set the adapter that populates the Spinners for selecting teams
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, teams);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner1.setAdapter(spinnerArrayAdapter);
        spinner2.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add menu items
        getMenuInflater().inflate(R.menu.menu_add_match, menu);
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

    public void onClick(View v)
    {
        //get teams selected by user
        String firstTeam = spinner1.getSelectedItem().toString();
        String secondTeam = spinner2.getSelectedItem().toString();

        if (firstTeam.equals("") || secondTeam.equals(""))
        {
            DialogHelper.makeLongToast(this,"Missing Fields");
        }else if(firstTeam.equals(secondTeam)){
            DialogHelper.makeLongToast(this,"Please select 2 different teams");
        }
        else
        {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID, tournamentID);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1,firstTeam);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2,secondTeam);
            String scoreTeam1 = score1.getText().toString();
            String scoreTeam2 = score2.getText().toString();
            if(!scoreTeam1.equals("")&&!scoreTeam2.equals("")) {
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1, score1.getText().toString());
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2, score2.getText().toString());
                if(Integer.parseInt(scoreTeam1)>Integer.parseInt(scoreTeam2)){
                    values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, firstTeam);
                } else {
                    values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, secondTeam);
                }
            }

            // Insert new match into the database
            db.insert(TournamentContract.MatchEntry.TABLE_NAME,null,values);

            //return to viewing the current tournament details
            Intent myIntent = new Intent(this, ViewTournament.class);
            myIntent.putExtra("tournamentID", tournamentID);
            startActivity(myIntent);
        }
    }
}
