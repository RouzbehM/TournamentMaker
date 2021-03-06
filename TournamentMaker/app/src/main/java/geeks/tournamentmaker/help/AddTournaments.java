package geeks.tournamentmaker.help;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;

import geeks.tournamentmaker.R;

/**
 * The AddTournaments class displays instructions on how to create new tournaments.
 */
public class AddTournaments extends ActionBarActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_add_tournaments
        );


        mImageView = (ImageView) findViewById(R.id.imageView5);
        mImageView.setImageResource(R.drawable.add_tournament);
    }
}
