package geeks.tournamentmaker;

import android.content.Context;
import android.widget.Toast;

/**
 * The DialogHelper class is used to make small notification messages for an activty
 */
public class DialogHelper {
    public static void makeLongToast(Context context, String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
