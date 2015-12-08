package geeks.tournamentmaker.help;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;

import geeks.tournamentmaker.R;

/**
 * The RemoveTournaments class displays instructions on how to delete tournaments.
 */
public class RemoveTournament extends ActionBarActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_remove_tournament);


        mImageView = (ImageView) findViewById(R.id.imageView4);
        mImageView.setImageResource(R.drawable.remove_tournament);
    }
}
