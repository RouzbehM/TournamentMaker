package geeks.tournamentmaker;

/**
 * Created by Oliver on 12/1/2015.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TeamListAdapter extends BaseAdapter {

    ArrayList<String> teams = new ArrayList<String>();
    LayoutInflater inflater;
    Context context;


    public TeamListAdapter(Context context, ArrayList<String> teams) {
        this.teams = teams;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return teams.size();
    }

    @Override
    public String getItem(int position) {
        return teams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public ArrayList<String> getTeams(){
        return teams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_list_item, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String currentListData = getItem(position);
        mViewHolder.text.setText(currentListData);

        return convertView;
    }

    private class ViewHolder {
        TextView text;

        public ViewHolder(View item) {
            text = (TextView) item.findViewById(R.id.list_item_text);
        }
    }
}
