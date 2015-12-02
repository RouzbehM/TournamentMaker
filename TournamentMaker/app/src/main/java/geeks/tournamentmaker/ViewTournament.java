package geeks.tournamentmaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

public class ViewTournament extends ActionBarActivity {

    private int tournamentID;
    private String tournamentType;
    private TournamentDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tournament);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new TournamentDBHelper(this);

        Intent intent = getIntent();
        tournamentID = intent.getIntExtra("tournamentID",-1);
        tournamentType = intent.getStringExtra("type");
        if(tournamentType.equals(Tournament.COMBINATION)){
            removeView(findViewById(R.id.nextRoundButton));
        }else if(tournamentType.equals(Tournament.ROUND_ROBIN)){
            removeView(findViewById(R.id.addMatchButton));
            removeView(findViewById(R.id.nextRoundButton));
        }else if(tournamentType.equals(Tournament.KNOCK_OUT)){
            removeView(findViewById(R.id.addMatchButton));
        }
    }

    private void removeView(View view){
        ((ViewGroup)view.getParent()).removeView(view);
    }

    public void createMatch(View view){
        Intent intent = new Intent(this,AddMatchActivity.class);
        intent.putExtra("tournamentID", tournamentID);
        startActivity(intent);
    }

    public void viewStandings(View view){
        Intent intent = new Intent(this,AddMatchActivity.class);
        intent.putExtra("tournamentID", tournamentID);
        startActivity(intent);
    }

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
    private void deleteTournament(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TournamentContract.TournamentEntry._ID + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { tournamentID+"" };
// Issue SQL statement.
        db.delete(TournamentContract.TournamentEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void loadMatches(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT * FROM "+ TournamentContract.MatchEntry.TABLE_NAME +
                " WHERE " + TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID +
                " = " + tournamentID,null);

    }

    public void generateNextRound(View view){

    }

}
