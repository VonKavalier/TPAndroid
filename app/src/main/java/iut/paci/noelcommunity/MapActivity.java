package iut.paci.noelcommunity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "mapActivity";
    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private District district;
    private MapDialog dialog;
    public List<LatLong> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidGraphicFactory.createInstance(this.getApplication());

        this.mapView = new MapView(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setContentView(this.mapView);

        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setZoomLevelMin((byte) 10);
        mapView.setZoomLevelMax((byte) 20);

        this.tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        this.district = (District) extra.getSerializable("district");

        this.dialog = new MapDialog(this);

        this.path = new ArrayList();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Deposite.ma = this;
        Store.ma = this;
        final int imageResourceId = R.drawable.ic_place_black_24dp;
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        File file = new File(Environment.getExternalStorageDirectory(), "/maps/paris.map");

        MapDataStore mapDataStore = new MapFile(file);
        tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                mapView.getModel().mapViewPosition,
                AndroidGraphicFactory.INSTANCE) {
            @Override
            public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
                vibrator.vibrate(500);

                final LatLong tll = tapLatLong;

                dialog.start(district.getNom(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        path.add(tll);
                        drawMarker(imageResourceId, tll);
                        drawPath(path);
                    }
                });

                return true;
            }
        };
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        LatLong pos = new LatLong(district.getLatitude(), district.getLongitude());

        mapView.setCenter(pos);
        mapView.setZoomLevel((byte) 19);

        this.drawMarker(imageResourceId, pos);

        this.path.clear();
        this.path.add(pos);

        this.appelHttp(this.district.getId());
    }

    public void drawMarker(int ressourceId, LatLong geoPoint) {
        System.out.println(ressourceId);
        Drawable drawable = getResources().getDrawable(ressourceId);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        bitmap.scaleTo(130, 130);
        Marker marker = new Marker(geoPoint, bitmap, 0, -bitmap.getHeight() / 2) {
            @Override
            public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
                if(contains(layerXY, tapXY)) {
                    Toast.makeText(MapActivity.this, "clicked marker", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        };
        mapView.getLayerManager().getLayers().add(marker);
    }

    public void drawMarker(int ressourceId, Place place) {
        System.out.println(ressourceId);
        Drawable drawable = getResources().getDrawable(ressourceId);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        bitmap.scaleTo(130, 130);
        Marker marker = place.getMarker(this, bitmap);
        mapView.getLayerManager().getLayers().add(marker);
    }

    public void drawPath(List<LatLong> path) {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(15);
        paint.setStyle(Style.STROKE);

        Polyline polyline = new Polyline(paint, AndroidGraphicFactory.INSTANCE);

        List<LatLong> coordinateList = polyline.getLatLongs();

        for(LatLong geoPoint : path) {
            coordinateList.add(geoPoint);
        }
        mapView.getLayerManager().getLayers().add(polyline);
    }

    private void appelHttp(int id) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("iut.96.lt")
                .appendPath("community")
                .appendPath("getDistrict.php")
                .appendQueryParameter("id", id + "");
        String urlString = builder.build().toString();
        urlString = "http://iut.96.lt/community/getDistrict.php?id=1";
        urlString = "http://paci.misc-lab.org/getDistrict.php?id=1";

        Log.d(TAG, "appelHttp: " + urlString);
        new DistrictTask(MapActivity.this, urlString).execute(urlString);
    }

    @Override
    protected void onDestroy() {
        mapView.destroy();
        AndroidGraphicFactory.clearResourceMemoryCache();

        super.onDestroy();
    }
}
