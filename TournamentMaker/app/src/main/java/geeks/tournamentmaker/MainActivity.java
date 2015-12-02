package geeks.tournamentmaker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
        if(id == R.id.action_settings){
            Intent intent = new Intent(this, ViewHelp.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadTournamentList(){


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {TournamentContract.TournamentEntry.COLUMN_NAME_NAME, TournamentContract.TournamentEntry._ID};
        String[] selectionArgs = {""+tournamentID};
        Cursor c = db.query(
                TournamentContract.TournamentEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                "_id = ?" ,                             // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );

        // Find ListView to populate
        ListView tournamentList = (ListView)findViewById(R.id.tournamentView);
        // Setup cursor adapter using cursor from last step
        LoadTournamentCursorAdapter loadTournamentAdapter = new LoadTournamentCursorAdapter(this, c, 0);
        // Attach cursor adapter to the ListView
        tournamentList.setAdapter(loadTournamentAdapter);



        /*
        c.moveToFirst();
        if(c!=null){
            do{
                for(int i =0; i < c.getColumnCount(); i++){


                }

            } while (c.moveToNext());
        }
        /*try {
            JSONObject json = new JSONObject(
                    c.getString(c.getColumnIndexOrThrow(TournamentContract.TournamentEntry._ID)));
            JSONArray tournamentsArray = json.optJSONArray("name");
            for(int i = 0; i < tournamentsArray.length(); i++) {
                addTournament(tournamentsArray.getString(i));
            }

        }catch(JSONException e){
            e.printStackTrace();


        db.close();
        */
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
 class LoadTournamentCursorAdapter extends CursorAdapter{
     public LoadTournamentCursorAdapter(Context context, Cursor cursor, int flags) {
         super(context, cursor, 0);
     }

     // The newView method is used to inflate a new view and return it,
     // you don't bind any data to the view at this point.
     @Override
     public View newView(Context context, Cursor cursor, ViewGroup parent) {
         return LayoutInflater.from(context).inflate(R.layout.simple_list_item, parent, false);
     }

     public void bindView(View view, Context context, Cursor cursor) {
         // Find fields to populate in inflated template
         TextView tournamentList = (TextView) view.findViewById(R.id.list_item_text);

         // Extract properties from cursor
         String body = cursor.getString(cursor.getColumnIndexOrThrow("List"));

         // Populate fields with extracted properties
         tournamentList.setText(body);

     }


}
