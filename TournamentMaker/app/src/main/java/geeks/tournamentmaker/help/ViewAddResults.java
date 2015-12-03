package geeks.tournamentmaker.help;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;

import geeks.tournamentmaker.R;

public class ViewAddResults extends ActionBarActivity{

private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view_add_results);


        mImageView = (ImageView) findViewById(R.id.imageView3);
        mImageView.setImageResource(R.drawable.add_results);
    }
}
