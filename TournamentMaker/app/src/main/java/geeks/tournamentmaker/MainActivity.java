package geeks.tournamentmaker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private TournamentDBHelper dbHelper;
    private ListView tournamentList;
    private ArrayList<String> tournaments;
    private ArrayAdapter<String> adapter;
    private int tournamentID;
    private String tournamentType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tournamentList = (ListView)findViewById(R.id.tournamentView);
        tournamentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(tournamentList.getSelectedItem()!=null) {
                    viewTournament(tournamentList.getSelectedItem());
                }
            }
        });

        tournaments = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.simple_list_item,tournaments);
        tournamentList.setAdapter(adapter);

//        Intent intent = getIntent();
//        tournamentID = intent.getIntExtra("tournamentID",-1);
//        tournamentType = intent.getStringExtra("type");

        dbHelper = new TournamentDBHelper(this);

        loadTournamentList();
    }

    public void createTournament(){

        Intent intent = new Intent(this, AddTournament.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void loadTournamentList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.TournamentEntry.COLUMN_NAME_NAME};
        String[] selectionArgs = {""+tournamentID};
        Cursor c = db.query(
                TournamentContract.TournamentEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                "rowid = ?" ,                             // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        c.moveToFirst();
        try {
            JSONObject json = new JSONObject(
                    c.getString(c.getColumnIndexOrThrow(TournamentContract.TournamentEntry._ID)));
            JSONArray tournamentsArray = json.optJSONArray("name");
            for(int i = 0; i < tournamentsArray.length(); i++) {
                addTournament(tournamentsArray.getString(i));
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
        db.close();
    }

    public void viewTournament(Object selected){
        Intent intent = new Intent(this, ViewTournament.class);
        intent.putExtra("selected",(String)selected);
        startActivity(intent);
    }
    private void addTournament(String team) {
        adapter.add(team);
        tournaments.add(team);
    }

    private void removeTournament(int index){
        adapter.remove(adapter.getItem(index));
        tournaments.remove(index);
    }

    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
