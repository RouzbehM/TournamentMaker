package geeks.tournamentmaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import geeks.tournamentmaker.help.*;

public class ViewHelp extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_help);
    }

    public void viewHelpAddTournaments(){
        Intent intent = new Intent(this, AddTournaments.class);
        startActivity(intent);
    }

    public void viewHelpRemoveTournaments(){
        Intent intent = new Intent(this, RemoveTournament.class);
        startActivity(intent);
    }

    public void viewHelpStandings(){
        Intent intent = new Intent(this, ViewStandings.class);
        startActivity(intent);
    }

    public void viewHelpTeams(){
        Intent intent = new Intent(this, ViewTeams.class);
        startActivity(intent);
    }

    public void viewHelpAddResults(){
        Intent intent = new Intent(this, ViewAddResults.class);
        startActivity(intent);
    }

    public void viewHelpViewResults(){
        Intent intent = new Intent(this, ViewResults.class);
        startActivity(intent);
    }



}
