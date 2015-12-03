package geeks.tournamentmaker.help;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;

import geeks.tournamentmaker.R;

public class ViewStandings extends ActionBarActivity {

    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view_standings2);



        mImageView = (ImageView) findViewById(R.id.imageView2);
        mImageView.setImageResource(R.drawable.remove_tournament);
    }
}
