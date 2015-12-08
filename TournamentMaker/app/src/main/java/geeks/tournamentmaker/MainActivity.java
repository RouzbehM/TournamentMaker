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

import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The MainActivity class displays a list of tournaments that the user has created.
 * The user is able to select a tournament to view its details and to add a new tournament.
 */
public class MainActivity extends ActionBarActivity {

    private TournamentDBHelper dbHelper;
    private ListView tournamentList;

    @Override
    /**
     * creates the main activity showing the list of tournaments that have already been created
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tournamentList = (ListView)findViewById(R.id.tournamentView);
        dbHelper = new TournamentDBHelper(this);

        loadTournamentList();
    }

    @Override
    public void onBackPressed(){
        //make back button do nothing;
    }

    public void createTournament(View view){
        Intent intent = new Intent(this, AddTournament.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add menu items.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * loads the list of tournaments that have been created
     */
    private void loadTournamentList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TournamentContract.TournamentEntry.TABLE_NAME,null);
        // Setup cursor adapter for populating tournament list
        LoadTournamentCursorAdapter loadTournamentAdapter = new LoadTournamentCursorAdapter(this, c, 0);
        tournamentList.setAdapter(loadTournamentAdapter);
    }
}


/**
 * The LoadTournamentCursorAdapter class is used for populating tournament list items.
 */
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
         // Get the tournament list item
         TextView tournamentList = (TextView) view.findViewById(R.id.list_item_text);
         // Extract tournament info from cursor
         String body = cursor.getString(cursor.getColumnIndexOrThrow(TournamentContract.TournamentEntry.COLUMN_NAME_NAME));
         final int tournamentID = cursor.getInt(cursor.getColumnIndex(TournamentContract.TournamentEntry._ID));
         final String status = cursor.getString(cursor.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_STATUS));
         final String type = cursor.getString(cursor.getColumnIndex(TournamentContract.TournamentEntry.COLUMN_NAME_TYPE));
         // Populate tournament list item
         tournamentList.setText(body);
         //start another activity when the list item is pressed
         tournamentList.setOnClickListener(new View.OnClickListener(){
             public void onClick(View view){
                 Intent intent = new Intent();
                 if(status.equals(Tournament.STARTED)) {
                     //activity for viewing tournament details
                     intent = new Intent(view.getContext(), ViewTournament.class);
                 }else if(status.equals(Tournament.NOT_STARTED)){
                     //activity for adding teams
                     intent = new Intent(view.getContext(), AddTeamActivity.class);
                 }
                 intent.putExtra("tournamentID", tournamentID);
                 intent.putExtra("type",type);
                 view.getContext().startActivity(intent);
             }
         });

     }


}
