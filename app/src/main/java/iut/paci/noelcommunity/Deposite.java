package iut.paci.noelcommunity;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.layer.overlay.Marker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Deposite extends Place {

    public static MapActivity ma;

    private final int emptyCount;

    public Deposite(int id, String name, double longitude, double latitude, Date openingTime, Date closingTime, int emptyCount) {
        super(id, name, longitude, latitude, openingTime, closingTime);
        this.emptyCount = emptyCount;
    }

    public int getEmptyCount() {
        return emptyCount;
    }

    @Override
    public void drawMarker(MapActivity activity, LatLong geoPoint) {
        activity.drawMarker(R.drawable.ic_place_deposite, geoPoint);
    }

    public Dialog getDialog(Context context) {
        return new DepositeDialog(context, this);
    }

    public Marker getMarker(Context context, Bitmap bitmap) {
        return new DepositeMarker(this, context, bitmap);
    }

    public static class DepositeMarker extends Marker {

        private final Deposite deposite;
        private final Context context;

        public DepositeMarker(Deposite deposite, Context context, Bitmap bitmap) {
            super(new LatLong(deposite.getLatitude(), deposite.getLongitude()), bitmap, 0, -bitmap.getHeight() /2);
            this.deposite = deposite;
            this.context = context;
        }

        @Override
        public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
            if(contains(layerXY, tapXY)) {
                Dialog d = deposite.getDialog(context);
                d.show();
                return true;
            }
            return false;
        }

        @Override
        public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
            return super.onLongPress(tapLatLong, layerXY, tapXY);
        }
    }

    public static class DepositeDialog extends Dialog {

        Deposite deposite;

        public DepositeDialog(Context context, Deposite deposite) {
            super(context);
            this.deposite = deposite;
            setContentView(R.layout.dialog_map_deposite);
            setContent(deposite.getName(), deposite.getEmptyCount(), deposite.getOpeningTime(), deposite.getClosingTime());
        }

        public void setContent(String nom, int nbrEmpty, Date open, Date close) {
            this.setTitle(nom);

            TextView tv_nbrEmptys = (TextView) findViewById(R.id.textView3),
                    tv_horaire = (TextView) findViewById(R.id.textView6);

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

            tv_nbrEmptys.setText(nbrEmpty + "");
            tv_nbrEmptys.setTextColor(getContext().getResources().getColor(nbrEmpty > 0 ? R.color.valid : R.color.invalid));

            Calendar ouv = Calendar.getInstance(), clo = Calendar.getInstance(), now = Calendar.getInstance();
            ouv.setTime(open);
            ouv.set(Calendar.YEAR, 0);
            ouv.set(Calendar.MONTH, 0);
            ouv.set(Calendar.DAY_OF_MONTH, 0);

            clo.setTime(close);
            clo.set(Calendar.YEAR, 0);
            clo.set(Calendar.MONTH, 0);
            clo.set(Calendar.DAY_OF_MONTH, 0);

            now.setTime(new Date());
            now.set(Calendar.YEAR, 0);
            now.set(Calendar.MONTH, 0);
            now.set(Calendar.DAY_OF_MONTH, 0);

            tv_horaire.setText(dateFormat.format(open) + " - " + dateFormat.format(close));
            boolean ouvert = ouv.compareTo(now) <= 0 && clo.compareTo(now) > 0;
            tv_horaire.setTextColor(getContext().getResources().getColor(ouvert ? R.color.valid : R.color.invalid));

            Button b = (Button) findViewById(R.id.mapdial_but);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    MapActivity ma = Deposite.ma;
                    LatLong ll = new LatLong(deposite.getLatitude(), deposite.getLongitude());
                    ma.path.add(ll);
                    ma.drawPath(ma.path);
                }
            });
        }
    }
}
