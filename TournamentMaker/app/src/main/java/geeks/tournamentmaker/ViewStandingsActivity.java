package geeks.tournamentmaker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewStandingsActivity extends ActionBarActivity {
    private TournamentDBHelper dbHelper;
    private ListView standings;
    private ArrayList winners = new ArrayList();
    public String[] sortedwinners;
    private ArrayList winnersandwins = new ArrayList();
    int tournamentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_standings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get the list used for displaying the team standings
        standings = (ListView) findViewById(R.id.standingsview);

        //get the tournament ID
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID", 0);

        //initialize the database helper and generate the standings
        dbHelper  = new TournamentDBHelper(this);
        loadStandings();

        // set adapter for populating the list of teams
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                winnersandwins );

        standings.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add menu items to the action bar
        getMenuInflater().inflate(R.menu.menu_view_standings, menu);
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
        }else if(id == android.R.id.home){//action bar back button
            onBackPressed();
        }

        return true;
    }
    @Override
    public void onBackPressed(){
        //return to viewing the matches for the current tournament
        Intent intent = new Intent(this, ViewTournament.class);
        intent.putExtra("tournamentID",tournamentID);
        startActivity(intent);
    }

    private Integer[] countItems(String[] arr)
    {
        List<Integer> itemCount = new ArrayList<Integer>();
        Integer counter = 0;
        String lastItem = arr[0];

        for(int i = 0; i < arr.length; i++)
        {
            if(arr[i].equals(lastItem))
            {
                counter++;
            }
            else
            {
                itemCount.add(counter);
                counter = 1;
            }
            lastItem = arr[i];
        }
        itemCount.add(counter);

        return itemCount.toArray(new Integer[itemCount.size()]);
    }

    private void addToWinners(String s){
        if(s!=null)
            winners.add(s);
    }

    private void loadStandings()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.MatchEntry.COLUMN_NAME_WINNER};
        String[] selectionArgs = {""+tournamentID};
        //find all the match winners for the current tournament
        Cursor c = db.query(
                TournamentContract.MatchEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID + " = ?",   // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        //add winners to list of winners
        if (c.moveToFirst()) {
            addToWinners(c.getString(c.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_WINNER)));
            while(c.moveToNext()){
                addToWinners(c.getString(c.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_WINNER)));
            }
        }
        c.close();

        sortedwinners= new String[winners.size()];
        if(sortedwinners.length!=0) {
            winners.toArray(sortedwinners);
            Arrays.sort(sortedwinners);
            //tally up the number of wins
            Integer[] cArr = countItems(sortedwinners);
            //populate the list that will be used for displaying the standings
            int num = -1;
            for (int i = 0; i < cArr.length; i++) {
                num += cArr[i];
                winnersandwins.add(sortedwinners[num] + " " + cArr[i].toString() + " wins");
            }
        }
    }


}
