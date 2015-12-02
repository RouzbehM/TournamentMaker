package geeks.tournamentmaker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class AddMatchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
