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

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.simple_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView matchItem = (TextView) view.findViewById(R.id.list_item_text);
        // Extract properties from cursor
        String team1= cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM1));
        String team2=cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_TEAM2));
        String score1=cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_SCORE1));
        String score2=cursor.getString(cursor.getColumnIndex(TournamentContract.MatchEntry.COLUMN_NAME_SCORE2));
        final int matchId = cursor.getInt(cursor.getColumnIndex(TournamentContract.MatchEntry._ID));

        // Populate fields with extracted properties
        matchItem.setText(team1 + " vs. " + team2 );
        if(score1!=null&&score2!=null){
            matchItem.setText( matchItem.getText() + " | Results: " + score1 + "-" + score2);
        }else if(!team1.equals("BYE")&&!team2.equals("BYE")){
            matchItem.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    Intent intent = new Intent(view.getContext(),EnterMatchResults.class);
                    intent.putExtra("matchID",matchId);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
