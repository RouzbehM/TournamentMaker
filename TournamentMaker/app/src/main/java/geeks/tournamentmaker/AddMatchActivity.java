package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddMatchActivity extends ActionBarActivity {
    private TournamentDBHelper dbHelper;
    Button add;
    int tournamentID;
    private ArrayList teams = new ArrayList();
    ListView teamlist;
    ArrayList chosenteams = new ArrayList();
    EditText score1 = (EditText)findViewById(R.id.team1score);
    EditText score2 = (EditText)findViewById(R.id.team2score);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID", 0);
        add = (Button)findViewById(R.id.addbtn);

        add.setOnClickListener((View.OnClickListener)AddMatchActivity.this);

        dbHelper = new TournamentDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS};
        String[] selectionArgs = {""+tournamentID};
        Cursor c = db.query(
                TournamentContract.TournamentEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TournamentContract.TournamentEntry._ID + " = " + tournamentID,   // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        c.moveToFirst();
        if (c != null) {
            do {
                for (int i = 0; i < c.getColumnCount(); i++) {

                    teams.add(c.getString(i));
                }
            }while (c.moveToNext());
        }
        db.close();

        teamlist = (ListView) findViewById(R.id.teamlisT);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                teams );

        teamlist.setAdapter(arrayAdapter);

        teamlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (teamlist.getSelectedItem() != null) {
                    chosenteams.add(teamlist.getSelectedItem().toString());
                }
            }
        });

        TextView team1 = (TextView)findViewById(R.id.team1);
        TextView team2 = (TextView)findViewById(R.id.team2);

        team1.setText((CharSequence)chosenteams.get(0));
        team2.setText((CharSequence)chosenteams.get(1));

    }

    public void onClick(View v)
    {

        if (v == add)
        {

            String firstteam = (String)chosenteams.get(0);
            String secondteam = (String)chosenteams.get(1);


            if (firstteam.equals("") == false && secondteam.equals("") == false)
            {

                // Gets the data repository in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID, tournamentID);
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1,firstteam);
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2,secondteam);
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1,score1.getText().toString());
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2,score2.getText().toString());


// Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = db.insert(
                        TournamentContract.MatchEntry.TABLE_NAME,null,values);


                Intent myIntent = new Intent(AddMatchActivity.this, AddTeamActivity.class);
                AddMatchActivity.this.startActivity(myIntent);
            }
            else
            {
                Context context = getApplicationContext();
                CharSequence text = "Missing Fields";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
        return;

    }
}
