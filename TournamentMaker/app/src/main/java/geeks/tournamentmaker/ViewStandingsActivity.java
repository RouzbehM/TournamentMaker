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
        dbHelper  = new TournamentDBHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID", 0);

        standings = (ListView) findViewById(R.id.standingsview);
        loadStandings();

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                winnersandwins );

        standings.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_standings, menu);
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
        }else if(id == android.R.id.home){
            onBackPressed();
        }

        return true;
    }
    @Override
    public void onBackPressed(){
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
        Cursor c = db.query(
                TournamentContract.MatchEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID + " = ?",   // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );

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

            Integer[] cArr = countItems(sortedwinners);

            int num = -1;
            for (int i = 0; i < cArr.length; i++) {
                num += cArr[i];
                winnersandwins.add(sortedwinners[num] + " " + cArr[i].toString() + " wins");
            }
        }
    }


}
