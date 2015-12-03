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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tournamentList = (ListView)findViewById(R.id.tournamentView);
        dbHelper = new TournamentDBHelper(this);

        loadTournamentList();
    }

    @Override
    public void onBackPressed(){

    }

    public void createTournament(View view){

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

    private void loadTournamentList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TournamentContract.TournamentEntry.TABLE_NAME,null);

        // Find ListView to populate
        ListView tournamentList = (ListView)findViewById(R.id.tournamentView);
        // Setup cursor adapter using cursor from last step
        LoadTournamentCursorAdapter loadTournamentAdapter = new LoadTournamentCursorAdapter(this, c, 0);
        // Attach cursor adapter to the ListView
        tournamentList.setAdapter(loadTournamentAdapter);
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
         String body = cursor.getString(cursor.getColumnIndexOrThrow(TournamentContract.TournamentEntry.COLUMN_NAME_NAME));
         final int tournamentID = cursor.getInt(cursor.getColumnIndex(TournamentContract.TournamentEntry._ID));
         final String status = cursor.getString(cursor.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_STATUS));
         final String type = cursor.getString(cursor.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_TYPE));
         // Populate fields with extracted properties
         tournamentList.setText(body);
         tournamentList.setOnClickListener(new View.OnClickListener(){
             public void onClick(View view){
                 Intent intent = new Intent();
                 if(status.equals(Tournament.STARTED)) {
                     intent = new Intent(view.getContext(), ViewTournament.class);
                 }else if(status.equals(Tournament.NOT_STARTED)){
                     intent = new Intent(view.getContext(), AddTeamActivity.class);
                 }
                 intent.putExtra("tournamentID", tournamentID);
                 intent.putExtra("type",type);
                 view.getContext().startActivity(intent);
             }
         });

     }


}
