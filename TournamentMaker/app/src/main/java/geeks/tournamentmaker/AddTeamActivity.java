package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddTeamActivity extends AppCompatActivity {

    private TournamentDBHelper dbHelper;
    private ListView teamList;
    private ArrayList<String> teams;
    private ArrayAdapter<String> adapter;
    private int tournamentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        teamList = (ListView)findViewById(R.id.teamList);
        teams = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.activity_add_team,teams);
        teamList.setAdapter(adapter);

        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID",-1);

        dbHelper = new TournamentDBHelper(this);

        loadTeamList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_player, menu);
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

    public void addTeam(View view){

    }

    public void removeTeam(View view){
        teamList.removeView(teamList.getSelectedView());
        //disable the remove button
        view.setEnabled(false);
    }

    public void startTournament(View view){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(TournamentContract.TournamentEntry.COLUMN_NAME_STATUS, "started");

// Which row to update, based on the ID
        String selection = TournamentContract.TournamentEntry._ID + " LIKE ?";
        String[] selectionArgs = { tournamentID+"" };

        db.update(
                TournamentContract.TournamentEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();

        generateMatches();

        Intent intent = new Intent(this, ViewTournament.class);
        intent.putExtra("tournamentID",tournamentID);
        startActivity(intent);
    }

    private void generateMatches(){

    }

    private void loadTeamList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS};
        String[] selectionArgs = {""+tournamentID};
        Cursor c = db.query(
                TournamentContract.TournamentEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TournamentContract.TournamentEntry._ID,   // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        c.moveToFirst();
        try {
            JSONObject json = new JSONObject(
                    c.getString(c.getColumnIndexOrThrow(TournamentContract.TournamentEntry._ID)));
            JSONArray teamsArray = json.optJSONArray("teams");
            for(int i = 0; i < teamsArray.length(); i++){
                adapter.add(teamsArray.getString(i));
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
        db.close();

    }

}
