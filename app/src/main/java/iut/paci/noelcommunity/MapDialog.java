package iut.paci.noelcommunity;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class MapDialog extends Dialog {

    private final Button mapdial_but;

    public MapDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_map_store);

        this.mapdial_but = (Button) findViewById(R.id.mapdial_but);
    }

    public void start(String titre, View.OnClickListener onClick) {
        this.setTitle(titre);
        mapdial_but.setOnClickListener(onClick);
        this.show();
    }
}
