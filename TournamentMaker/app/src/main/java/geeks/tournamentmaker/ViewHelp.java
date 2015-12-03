package geeks.tournamentmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import geeks.tournamentmaker.help.*;

public class ViewHelp extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_help);
    }

    public void viewHelpAddTournaments(View view){
        Intent intent = new Intent(this, AddTournaments.class);
        startActivity(intent);
    }

    public void viewHelpRemoveTournaments(View view){
        Intent intent = new Intent(this, RemoveTournament.class);
        startActivity(intent);
    }

    public void viewHelpStandings(View view){
        Intent intent = new Intent(this, ViewStandings.class);
        startActivity(intent);
    }

    public void viewHelpTeams(View view){
        Intent intent = new Intent(this, ViewTeams.class);
        startActivity(intent);
    }

    public void viewHelpAddResults(View view){
        Intent intent = new Intent(this, ViewAddResults.class);
        startActivity(intent);
    }

    public void viewHelpViewResults(View view){
        Intent intent = new Intent(this, ViewResults.class);
        startActivity(intent);
    }



}
