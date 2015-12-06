package geeks.tournamentmaker;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
    /**
     * The onCreate for this activity creates a listView , receives the tournament info, then
     * initializes the database helper and loads the list of teams
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get listview that will be populated
        teamList = (ListView)findViewById(R.id.teamList);

        teams = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,R.layout.simple_list_item,teams);
        teamList.setAdapter(adapter);
        //keep track of which item is selected
        teamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                selectedPosition=position;
                //allow removal of selected item
                setRemoveButtonEnabled(true);
            }
        });
        //get tournament info
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID",-1);
        tournamentType = intent.getStringExtra("type");

        //initialize database helper and populate the team list
        dbHelper = new TournamentDBHelper(this);
        loadTeamList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add menu items
        getMenuInflater().inflate(R.menu.menu_add_player, menu);
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

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * This method adds a team to a tournament
     * @param view the view that was clicked
     */
    public void addTeam(View view){
        getTeamFromUser();
    }

    /**
     * This method saves all the teams involved in a tournament to the database
     *
     */
    private void saveTeams(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        //to store the array with the team names, we convert it into a JSON object
        JSONObject jsonTeams = new JSONObject();
        try {
            jsonTeams.put("teams", new JSONArray(teams));
        }catch(JSONException e){
            e.printStackTrace();
        }
        //convert the JSON object to a string so that it can be stored in the SQLite database
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

    /**
     * This method creates a team, the name of the team is chosen by the user
     */
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
                if(nameIsValid(teamName)){
                    addTeam(teamName);
                    saveTeams();
                    dialog.dismiss();
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

    /**
     * This method compares a team name with all of the other team names in the same tournament,
     * returning whether there is a team with the same name or not
     * @param name team name to be compared
     * @return if the name is valid or not
     */
    private boolean nameIsValid(String name){
        if(teams.contains(name)){
            DialogHelper.makeLongToast(this, "That team name is already added.");
            return false;
        }else if(name.equals("BYE")){
            DialogHelper.makeLongToast(this, "You cannot pick that name!");
            return false;
        }else if(name.equals("")){
            DialogHelper.makeLongToast(this,"Please enter a name.");
            return false;
        }else if(name.length()>15){
            DialogHelper.makeLongToast(this,"That name is too long!");
            return false;
        }
        return true;
    }

    /**
     * Deletes an already created team from the tournament
     * @param view the view that was clicked
     */
    public void removeTeam(View view) {
        removeTeam(teams.get(selectedPosition));
        saveTeams();
        setRemoveButtonEnabled(false);
    }

    /**
     * This method starts the tournament selected, changing the status of the tournament to
     * 'started'. If the tournament is a knockout or a round robin tournament, matches will be
     * generated automatically
     * @param view the view that was clicked
     */
    public void startTournament(View view){
        if(teams.size()>1) {
            //Update the current tournament status
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(TournamentContract.TournamentEntry.COLUMN_NAME_STATUS, Tournament.STARTED);

            String selection = TournamentContract.TournamentEntry._ID + "=?";
            String[] selectionArgs = {tournamentID + ""};

            db.update(
                    TournamentContract.TournamentEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            db.close();

            //automatically generate some matches depending on the tournament type
            generateMatches();

            //start activity for viewing the tournament
            Intent intent = new Intent(this, ViewTournament.class);
            intent.putExtra("tournamentID", tournamentID);
            intent.putExtra("type",tournamentType);
            startActivity(intent);
        }else{
            DialogHelper.makeLongToast(this,"You must have at least 2 teams to start a tournament");
        }
    }

    /**
     * If the tournament is a round robin tournament, the matches that are generated are made so
     * that each team plays every team once
     *
     * If the tournament is a knockout tournament, the teams will be randomly shuffled and then
     * the first round of matches are generated by selecting two teams to play each other. If
     * needed, first round byes are created if there is too many teams
     *
     * If the tournament is a combination tournament, the user will have to add matches manually
     */
    private void generateMatches(){
        ArrayList<Match> matches = new ArrayList<>();
        //create matches
        if(tournamentType.equals(Tournament.ROUND_ROBIN)){
            //generate all round robin matches
            for(int i = 0; i < teams.size()-1; i++){
                for(int j = i + 1; j < teams.size(); j++){
                    matches.add(new Match(teams.get(i),teams.get(j)));
                }
            }
        }else if(tournamentType.equals(Tournament.KNOCK_OUT)){
            //randomize team order
            Collections.shuffle(teams);
            //add byes until power of 2 is reached
            int targetSize = getNextTwoPower(teams.size());
            for(int i = 0; teams.size()<targetSize; i+=2){
                teams.add(i,"BYE");
            }
            //generate first round of matches
            for(int i = 0; i<teams.size();i+=2){
                matches.add(new Match(teams.get(i),teams.get(i+1)));
            }
        }else if(tournamentType.equals(Tournament.COMBINATION)){
            //too complicated for now
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
            if(match.getTeam1().equals("BYE")){
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER,match.getTeam2());
            }else if(match.getTeam2().equals("BYE")){
                values.put(TournamentContract.MatchEntry.COLUMN_NAME_WINNER,match.getTeam1());
            }

            db.insert(
                    TournamentContract.MatchEntry.TABLE_NAME,
                    null,
                    values);
        }
        db.close();
    }

    /**
     * Finds the lowest power of 2 that the number is greater than or equal to
     * @param num the number that is being used
     * @return the lowest power of 2 that is greater than or equal to the number
     */
    private int getNextTwoPower(int num){
        //find lowest power of 2 that the number is greater than or equal to
        double y = Math.floor(Math.log(num - 0.00001) / Math.log(2));
        //return the next power of 2
        return (int)Math.pow(2, y + 1);
    }

    /**
     * Loads the list of teams all from the specific tournament
     */
    private void loadTeamList(){
        //Query database for the list of teams
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
                    //teams are stored in a JSON object
                    JSONObject json = new JSONObject(teamString);
                    JSONArray teamsArray = json.optJSONArray("teams");
                    //populate the teams list with values from the JSON array
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
        if(teams.size()>1)//ensures that a tournament cannot be started if it does not have enough teams
            findViewById(R.id.startTournamentButton).setEnabled(true);
    }

    private void removeTeam(String string){
        adapter.remove(string);
        if(teams.size()<2)//ensures that a tournament cannot be started if it does not have enough teams
            findViewById(R.id.startTournamentButton).setEnabled(false);
    }

    private void setRemoveButtonEnabled(boolean isEnabled){
        findViewById(R.id.removeButton).setEnabled(isEnabled);
    }

    /**
     * Creates a dialog to confirm the deletion of a tournament
     * @param view the view that was clicked
     */
    public void deleteTournament(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deleting tournament");
        builder.setMessage("Are you sure you want to delete this tournament?");
        // Add the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTournament();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * deletes the tournament from the database
     */
    private void deleteTournament(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TournamentContract.TournamentEntry._ID + " = ?";
        String[] selectionArgs = { tournamentID+"" };
        String selection2 = TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID + " = ?";
        String[] selectionArgs2 = { tournamentID+"" };
        //delete the tournament from the database
        db.delete(TournamentContract.TournamentEntry.TABLE_NAME, selection, selectionArgs);
        //delete the matches associated with the tournament from the database
        db.delete(TournamentContract.MatchEntry.TABLE_NAME,selection2,selectionArgs2);
        db.close();
        //return to the main activity
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
