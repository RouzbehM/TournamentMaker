package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AddMatchActivity extends ActionBarActivity {
    private TournamentDBHelper dbHelper;
    Button add;
    int tournamentID;
    private ArrayList teams = new ArrayList();
    private Spinner spinner1;
    private Spinner spinner2;
    EditText score1;
    EditText score2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
        score1 = (EditText)findViewById(R.id.team1score);
        score2 = (EditText)findViewById(R.id.team2score);
        spinner1 = (Spinner) findViewById(R.id.team1spinner);
        spinner2 = (Spinner) findViewById(R.id.team2spinner);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID", 0);
        add = (Button)findViewById(R.id.addbtn);

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

        if (c.moveToFirst()) {
            try {
                String teamString = c.getString(c.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS));
                if(teamString!=null) {
                    JSONObject json = new JSONObject(teamString);
                    JSONArray teamsArray = json.optJSONArray("teams");
                    for (int i = 0; i < teamsArray.length(); i++) {
                        teams.add(teamsArray.getString(i));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, teams);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        spinner1.setAdapter(spinnerArrayAdapter);
        spinner2.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ViewTournament.class);
        intent.putExtra("tournamentID",tournamentID);
        startActivity(intent);
    }

    public void onClick(View v)
    {
        String firstteam = spinner1.getSelectedItem().toString();
        String secondteam = spinner2.getSelectedItem().toString();


        if (firstteam.equals("") || secondteam.equals(""))
        {
            CharSequence text = "Missing Fields";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }else if(firstteam.equals(secondteam)){
            CharSequence text = "Please select 2 different teams";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
        else
        {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID, tournamentID);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1,firstteam);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2,secondteam);
            String scoreTeam1 = score1.getText().toString();
            String scoreTeam2 = score2.getText().toString();
            if(!scoreTeam1.equals("")&&!scoreTeam2.equals("")) {
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1, score1.getText().toString());
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2, score2.getText().toString());
                if(Integer.parseInt(scoreTeam1)>Integer.parseInt(scoreTeam2)){
                    values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, firstteam);
                } else {
                    values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER, secondteam);
                }
            }


// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    TournamentContract.MatchEntry.TABLE_NAME,null,values);


            Intent myIntent = new Intent(this, ViewTournament.class);
            myIntent.putExtra("tournamentID", tournamentID);
            startActivity(myIntent);

        }


    }
}
