package geeks.tournamentmaker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID", 0);
        loadStandings();
        standings = (ListView) findViewById(R.id.standingsview);

 
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                winnersandwins );

        standings.setAdapter(arrayAdapter);

    }
    public static Integer[] countItems(String[] arr)
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

    private void loadStandings()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.MatchEntry.COLUMN_NAME_WINNER};
        String[] selectionArgs = {""+tournamentID};
        Cursor c = db.query(
                TournamentContract.MatchEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID + " = " + tournamentID,   // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        c.moveToFirst();
        if (c != null) {
            do {
                for (int i = 0; i < c.getColumnCount(); i++) {

                    winners.add(c.getString(i));
                }
            }while (c.moveToNext());
        }
        db.close();

        sortedwinners= new String[winners.size()];
        winners.toArray(sortedwinners);
        Arrays.sort(sortedwinners);

        Integer[] cArr = countItems(sortedwinners);
        int num = 0;
        for(int i = 0; i < cArr.length; i++)
        {
            num += cArr[i]-1;
            winnersandwins.add(sortedwinners[num] + " " + cArr[i].toString() + " wins");
        }
    }


}
