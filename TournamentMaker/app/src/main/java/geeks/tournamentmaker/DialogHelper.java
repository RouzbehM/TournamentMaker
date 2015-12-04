package geeks.tournamentmaker;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Oliver on 12/4/2015.
 */
public class DialogHelper {
    public static void makeLongToast(Context context, String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
