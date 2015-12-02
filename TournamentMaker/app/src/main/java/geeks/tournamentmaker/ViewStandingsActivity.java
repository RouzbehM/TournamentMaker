package geeks.tournamentmaker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ViewStandingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_standings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
