package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
        teamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(teamList.getSelectedItem()!=null)
                    setRemoveButtonEnabled(true);
            }
        });
        teams = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.simple_list_item,teams);
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
        getTeamFromUser();
        saveTeams();
    }

    private void saveTeams(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        JSONObject jsonTeams = new JSONObject();
        try {
            jsonTeams.put("teams", new JSONArray(teams));
        }catch(JSONException e){
            e.printStackTrace();
        }
        String teamsArray = jsonTeams.toString();
        values.put(TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS, teamsArray);

// Which row to update, based on the ID
        String selection = TournamentContract.TournamentEntry._ID + " LIKE ?";
        String[] selectionArgs = { tournamentID+"" };

        db.update(
                TournamentContract.TournamentEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();
    }

    private void getTeamFromUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a new team");

// Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String teamName = input.getText().toString();
                if(!teams.contains(teamName)) {
                    addTeam(teamName);
                    dialog.dismiss();
                }else{
                    displayMessage("That team name is already added.");
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void removeTeam(View view) {
        removeTeam(teamList.getSelectedItemPosition());
        setRemoveButtonEnabled(false);
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
            for(int i = 0; i < teamsArray.length(); i++) {
                addTeam(teamsArray.getString(i));
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
        db.close();
    }

    private void addTeam(String team) {
        adapter.add(team);
        teams.add(team);
    }

    private void removeTeam(int index){
        adapter.remove(adapter.getItem(index));
        teams.remove(index);
    }

    private void displayMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void setRemoveButtonEnabled(boolean isEnabled){
        ((Button)findViewById(R.id.removeButton)).setEnabled(isEnabled);
    }

}
