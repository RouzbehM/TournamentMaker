package geeks.tournamentmaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ViewTournament extends ActionBarActivity {

    private int tournamentID;
    private String tournamentType;
    private TournamentDBHelper dbHelper;
    private Cursor cursor;

    @Override
    /**
     * Creates the activity to view the tournament.
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tournament);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new TournamentDBHelper(this);

        //get the tournament type and tournament ID
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID",-1);
        tournamentType = intent.getStringExtra("type");

        if(tournamentType==null){
            //get tournament type by querying the database for tournament with provided ID
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor c = db.rawQuery(
                    "SELECT " + TournamentContract.TournamentEntry.COLUMN_NAME_TYPE +
                    " FROM " + TournamentContract.TournamentEntry.TABLE_NAME +
                    " WHERE " + TournamentContract.TournamentEntry._ID + " = " + tournamentID,null);
            if(c.moveToFirst()){
                tournamentType = c.getString(c.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_TYPE));
            }
        }
        //disable ability to add matches for a round robin tournament
        if(tournamentType.equals(Tournament.ROUND_ROBIN)){
            removeView(findViewById(R.id.addMatchButton));
        }
        loadMatches();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_tournament, menu);
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
        }else if(id == android.R.id.home){//back button in action bar
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed(){
        //return to the main page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void onStop(){
        super.onStop();
        if(!(cursor==null)&&!cursor.isClosed())
            cursor.close();
    }

    private void removeView(View view){
        ((ViewGroup)view.getParent()).removeView(view);
    }

    /**
     * Creates a new match for the tournament
     * @param view the view that was clicked
     */
    public void createMatch(View view){
        Intent intent = new Intent(this,AddMatchActivity.class);
        intent.putExtra("tournamentID", tournamentID);
        startActivity(intent);
    }

    /**
     * Views the standings of the tournament
     * @param view the view that was clicked
     */
    public void viewStandings(View view){
        Intent intent = new Intent(this,ViewStandingsActivity.class);
        intent.putExtra("tournamentID", tournamentID);
        startActivity(intent);
    }

    public void deleteTournament(View view){
        // Create dialog to confirm deletion
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
     * deletes the specified tournament
     */
    private void deleteTournament(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = TournamentContract.TournamentEntry._ID + " = ?";
        String[] selectionArgs = { tournamentID+"" };

        String selection2 = TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID + " = ?";
        String[] selectionArgs2 = { tournamentID+"" };

        //delete tournament from the tournaments database
        db.delete(TournamentContract.TournamentEntry.TABLE_NAME, selection, selectionArgs);
        //delete all the matches associated with the tournament
        db.delete(TournamentContract.MatchEntry.TABLE_NAME,selection2,selectionArgs2);

        db.close();
        //return to the main page
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    /**
     * Loads the matches of the tournament from the database
     */
    private void loadMatches(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //Query database for all matches with the provided tournament ID
        cursor = db.rawQuery(
                "SELECT * FROM " + TournamentContract.MatchEntry.TABLE_NAME +
                        " WHERE " + TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID +
                        " = " + tournamentID, null);
        //create and add cursor adapter to populate list of matches
        MatchCursorAdapter cursorAdapter = new MatchCursorAdapter(this,cursor,0);
        ListView matchList = (ListView) findViewById(R.id.matchList);
        matchList.setAdapter(cursorAdapter);
    }

}
