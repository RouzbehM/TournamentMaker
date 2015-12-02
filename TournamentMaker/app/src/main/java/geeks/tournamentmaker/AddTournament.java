package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tournament);

        create  = (Button)findViewById(R.id.CreateBtn);
        typespinner = (Spinner)findViewById(R.id.TournamentTypes);

        String[] myItems = {Tournament.KNOCK_OUT, Tournament.ROUND_ROBIN,Tournament.COMBINATION};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, myItems);
        typespinner.setAdapter(adapter);
    }

    public void onClick(View v)
    {
            EditText tournamentname = (EditText)findViewById(R.id.Message1Box);
            String name = tournamentname.getText().toString();
            String type = typespinner.getSelectedItem().toString();

            if (name.equals("") == false && type.equals("") == false)
            {
               Tournament aTournament = new Tournament(name,type);

                // Gets the data repository in write mode
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(TournamentContract.TournamentEntry.COLUMN_NAME_NAME, aTournament.getName());
                values.put(TournamentContract.TournamentEntry.COLUMN_NAME_STATUS, aTournament.getStatus());
                values.put(TournamentContract.TournamentEntry.COLUMN_NAME_TYPE, aTournament.getTournamenttype());

// Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = db.insert(
                        TournamentContract.TournamentEntry.TABLE_NAME,null,values);


                Intent myIntent = new Intent(AddTournament.this, AddTeamActivity.class);
                myIntent.putExtra("tournamentID",(int)newRowId);
                myIntent.putExtra("type",aTournament.getTournamenttype());
                startActivity(myIntent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tournament, menu);
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
