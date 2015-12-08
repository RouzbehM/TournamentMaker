package geeks.tournamentmaker.help;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;

import geeks.tournamentmaker.R;

/**
 * The ViewTeams class displays instructions on how to add and remove teams from a tournament.
 */
public class ViewTeams extends ActionBarActivity {
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view_teams);


        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable.add_remove_teams);
    }
}
