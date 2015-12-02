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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class AddTeamActivity extends AppCompatActivity {

    private TournamentDBHelper dbHelper;
    private ListView teamList;
    private ArrayList<String> teams;
    private ArrayAdapter<String> adapter;
    private int tournamentID;
    private int selectedPosition;
    private String tournamentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        teamList = (ListView)findViewById(R.id.teamList);

        teams = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,R.layout.simple_list_item,teams);
        teamList.setAdapter(adapter);

        teamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                selectedPosition=position;
                setRemoveButtonEnabled(true);
            }
        });
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID",-1);
        tournamentType = intent.getStringExtra("type");

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
        String selection = TournamentContract.TournamentEntry._ID + " = ?";
        String[] selectionArgs = {tournamentID + ""};

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
        removeTeam(teams.get(selectedPosition));
        saveTeams();
        setRemoveButtonEnabled(false);
    }

    public void startTournament(View view){
        if(teams.size()>1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(TournamentContract.TournamentEntry.COLUMN_NAME_STATUS, Tournament.STARTED);

// Which row to update, based on the ID
            String selection = TournamentContract.TournamentEntry._ID + "=?";
            String[] selectionArgs = {tournamentID + ""};

            db.update(
                    TournamentContract.TournamentEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();

            generateMatches();

            Intent intent = new Intent(this, ViewTournament.class);
            intent.putExtra("tournamentID", tournamentID);
            startActivity(intent);
        }else{
            displayMessage("You must have at least 2 teams to start a tournament");
        }
    }

    private void generateMatches(){
        ArrayList<Match> matches = new ArrayList<>();
        //create matches
        if(tournamentType.equals(Tournament.ROUND_ROBIN)){
            //generate all matches
            for(int i = 0; i < teams.size()-1; i++){
                for(int j = i + 1; j < teams.size(); j++){
                    matches.add(new Match(teams.get(i),teams.get(j)));
                }
            }
        }else if(tournamentType.equals(Tournament.KNOCK_OUT)){
            //add byes until power of 2 is reached
            int targetSize = getNextTwoPower(teams.size());
            while(teams.size()<targetSize){
                teams.add("BYE");
            }
            //randomize team order
            Collections.shuffle(teams);
            //generate first round of matches
            for(int i = 0; i<teams.size();i+=2){
                matches.add(new Match(teams.get(i),teams.get(i+1)));
            }
        }else if(tournamentType.equals(Tournament.COMBINATION)){
            //too complicated
            //let user create matches manually
        }
        //randomize match order
        Collections.shuffle(matches);
        //add matches to database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(Match match: matches){
            ContentValues values = new ContentValues();
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID, tournamentID);
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1, match.getTeam1());
            values.put(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2, match.getTeam2());

            db.insert(
                    TournamentContract.MatchEntry.TABLE_NAME,
                    null,
                    values);
        }
        db.close();
    }

    private int getNextTwoPower(int num){
        double y = Math.floor(Math.log(num) / Math.log(2));
        return (int)Math.pow(2, y + 1);
    }

    private void loadTeamList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS};
        String[] selectionArgs = {""+tournamentID};
        Cursor c = db.query(
                TournamentContract.TournamentEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TournamentContract.TournamentEntry._ID + "=?",   // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        if(c.moveToFirst()) {
            try {
                String teamString = c.getString(c.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_TEAMS));
                if(teamString!=null) {
                    JSONObject json = new JSONObject(teamString);
                    JSONArray teamsArray = json.optJSONArray("teams");
                    for (int i = 0; i < teamsArray.length(); i++) {
                        addTeam(teamsArray.getString(i));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    private void addTeam(String team) {
        adapter.add(team);
        if(teams.size()>1)
            findViewById(R.id.startTournamentButton).setEnabled(true);
    }

    private void removeTeam(String string){
        adapter.remove(string);
        if(teams.size()<2)
            findViewById(R.id.startTournamentButton).setEnabled(false);
    }

    private void displayMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void setRemoveButtonEnabled(boolean isEnabled){
        findViewById(R.id.removeButton).setEnabled(isEnabled);
    }

}
