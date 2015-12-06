package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

class Tournament{
    String tournamentname;
    String tournamentstatus;
    String tournamenttype;

    public static final String NOT_STARTED = "NOT STARTED";
    public static final String STARTED = "STARTED";
    public static final String ROUND_ROBIN = "Round Robin";
    public static final String KNOCK_OUT = "Knock Out";
    public static final String COMBINATION = "Combination";

    public Tournament(String tournamentname, String tournamenttype)
    {
        this.tournamentname = tournamentname;
        this.tournamenttype = tournamenttype;
        this.tournamentstatus = NOT_STARTED;
    }

    public void setStatus(String status)
    {
        this.tournamentstatus = status;
    }

    public String getStatus()
    {
        return this.tournamentstatus;
    }

    public void setName(String name)
    {
        this.tournamentstatus = name;
    }

    public String getName()
    {
        return this.tournamentname;
    }

    public void setTournamentType(String Status)
    {
        this.tournamentstatus = Status;
    }

    public String getTournamenttype()
    {
        return this.tournamenttype;
    }
}

public class AddTournament extends ActionBarActivity {

    Button create ;
    Spinner typespinner;
    TournamentDBHelper mDbHelper = new TournamentDBHelper(AddTournament.this);

    @Override
    /**
     * Creates a spinner to specify what kind of tournament will be made, and a button to confirm
     * the creation of a tournament
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tournament);

        create  = (Button)findViewById(R.id.CreateBtn);
        typespinner = (Spinner)findViewById(R.id.TournamentTypes);

        String[] myItems = {Tournament.KNOCK_OUT, Tournament.ROUND_ROBIN,Tournament.COMBINATION};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typespinner.setAdapter(adapter);
    }

    /**
     * Creates a tournament with its own name and type.
     * @param v the view that was clicked
     */
    public void onClick(View v)
    {
            EditText tournamentname = (EditText)findViewById(R.id.Message1Box);
            String name = tournamentname.getText().toString();
            String type = typespinner.getSelectedItem().toString();

            // Checking for valid tournament name
            if (name.equals("") == false && type.equals("") == false)
            {
               Tournament aTournament = new Tournament(name,type);

                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String[] projection = {TournamentContract.TournamentEntry._ID};
                String[] selectionArgs = {""+name};
                Cursor c = db.query(
                        TournamentContract.TournamentEntry.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        TournamentContract.TournamentEntry.COLUMN_NAME_NAME + "=?",   // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null
                );
                if(c.moveToFirst()) {
                    DialogHelper.makeLongToast(this,"A tournament with that name already exists!");
                    c.close();
                }else {
                    c.close();
                    db = mDbHelper.getWritableDatabase();
                    // Create a new map of values to add in a new tournament entry
                    ContentValues values = new ContentValues();
                    values.put(TournamentContract.TournamentEntry.COLUMN_NAME_NAME, aTournament.getName());
                    values.put(TournamentContract.TournamentEntry.COLUMN_NAME_STATUS, aTournament.getStatus());
                    values.put(TournamentContract.TournamentEntry.COLUMN_NAME_TYPE, aTournament.getTournamenttype());

                    //Insert the new row, returning the primary key value of the new row
                    long newRowId;
                    newRowId = db.insert(
                            TournamentContract.TournamentEntry.TABLE_NAME, null, values);
                    db.close();

                    //Start activity for adding teams
                    Intent myIntent = new Intent(AddTournament.this, AddTeamActivity.class);
                    myIntent.putExtra("tournamentID", (int) newRowId);
                    myIntent.putExtra("type", aTournament.getTournamenttype());
                    startActivity(myIntent);
                }
            }
            else
            {
                DialogHelper.makeLongToast(this,"Missing Fields");
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add menu items.
        getMenuInflater().inflate(R.menu.menu_add_tournament, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
