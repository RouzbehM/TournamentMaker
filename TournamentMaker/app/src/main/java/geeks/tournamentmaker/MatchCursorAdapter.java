package geeks.tournamentmaker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Oliver on 12/2/2015.
 */
public class MatchCursorAdapter extends CursorAdapter {
    public MatchCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.simple_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find list item to populate in inflated template
        TextView matchItem = (TextView) view.findViewById(R.id.list_item_text);
        // Extract match data from cursor
        String team1= cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1));
        String team2=cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2));
        String score1=cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1));
        String score2=cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2));
        final int tournamentID = cursor.getInt(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TOURNAMENT_ID));
        final int matchId = cursor.getInt(cursor.getColumnIndex(TournamentContract.MatchEntry._ID));

        // Populate match list item
        matchItem.setText(team1 + " vs. " + team2 );
        if(score1!=null&&score2!=null){
            //append the match score and disable match result entry
            matchItem.setText( matchItem.getText() + " | Results: " + score1 + "-" + score2);
            matchItem.setClickable(false);
        }else if(!team1.equals("BYE")&&!team2.equals("BYE")){
            //add click listener for matches that have no results yet
            matchItem.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    //start activity for entering match results
                    Intent intent = new Intent(view.getContext(),EnterMatchResults.class);
                    intent.putExtra("matchID",matchId);
                    intent.putExtra("tournamentID",tournamentID);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
