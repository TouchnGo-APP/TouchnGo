package com.example.touchngo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.AnnotationManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.icu.text.MessagePattern.ArgType.SELECT;


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {
    Dialog myDialog;

    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;

    ImageButton note;
    ImageButton search;
    ImageButton cancel;
    ImageButton english;
    ImageButton chinese;
    ImageButton german;
    RelativeLayout IntroView;

    private List<Double> lat_list = new ArrayList<>();
    private List<Double> lon_list = new ArrayList<>() ;
    private List<String> text_list = new ArrayList<>();
    private List<String> desc_list = new ArrayList<>();

    private String bus_line ;
    private String street_name ;
    private String near_type ;
    private String point_class ;

    String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get Mapbox token for accessing the Map
        Mapbox.getInstance(this, "pk.eyJ1Ijoicm9zaWVkb3NpZSIsImEiOiJjam9mZ3pibjkwNGJuM2xwZmlyMzV5NjJqIn0.J770v8JrQ8dy33NHswuFaA");
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        note = (ImageButton)findViewById(R.id.note);
        search = (ImageButton)findViewById(R.id.search);
        cancel = (ImageButton)findViewById(R.id.cancel);
        english = (ImageButton)findViewById(R.id.english);
        chinese = (ImageButton)findViewById(R.id.chinese);
        german = (ImageButton)findViewById(R.id.german);
        IntroView = findViewById(R.id.IntroView);

        // click listener for setting introduction view appears
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                note.setVisibility(View.GONE);  //hide old button
                search.setVisibility(View.GONE);  //hide old button
                IntroView.setVisibility(View.VISIBLE);  //show layout2
            }
        });
        // click listener for setting search view appears
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                note.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
                IntroView.setVisibility(View.GONE);
            }
        });
        // select english introduction
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView introduction = (TextView) findViewById(R.id.intro);
                String intro_english = getResources().getString(R.string.intro_english);
                introduction.setText(intro_english);
            }
        });
        //select chinese introduction
        chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView introduction = (TextView) findViewById(R.id.intro);
                String intro_chinese = getResources().getString(R.string.intro_chinese);
                introduction.setText(intro_chinese);
            }
        });
        // select german introduction
        german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView introduction = (TextView) findViewById(R.id.intro);
                String intro_german = getResources().getString(R.string.intro_german);
                introduction.setText(intro_german);
            }
        });
        // connect to search activity
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent( MainActivity.this, search.class);
                startActivity(i);
            }
        });

        // create intent to reach the values of the variables from search class
        Intent extras = getIntent();
        bus_line= extras.getStringExtra("point_bus_line");
        street_name = extras.getStringExtra("point_street_name");
        near_type = extras.getStringExtra("point_near_type");
        point_class = extras.getStringExtra("point_class");

        // connect to Database
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        SQLiteDatabase database = databaseAccess.open();

        //SQL query for bus based searching and street name based searching
        final String query1 = "WITH POI_selection(selected_id) AS" +
                "( WITH bus_selection(bus_point_id) AS (SELECT point_id FROM points_in_bus where bus_id = '"+bus_line+ "') " +
                "SELECT DISTINCT point_id FROM points_near_POI,bus_selection " +
                "where poi_class = '"+near_type+ "' and points_near_POI.point_id = bus_selection.bus_point_id) " +
                "SELECT * FROM points,POI_selection WHERE points.point_id = POI_selection.selected_id and points.category = '"+point_class+ "'";
        final String query2 = "WITH POI_selection(selected_id) AS " +
                "( WITH street_selection(street_point_id) AS (SELECT point_id FROM points_in_street where street ='"+street_name+ "')" +
                "SELECT DISTINCT point_id FROM points_near_POI,street_selection " +
                "where poi_class = '"+near_type+ "' and points_near_POI.point_id = street_selection.street_point_id)" +
                "SELECT * FROM points,POI_selection\n WHERE points.point_id = POI_selection.selected_id and points.category = '"+point_class+ "'";

        // Choose suitable SQL query for using
        if (bus_line==null){ query = query2 ;} else{ query = query1;};

        // using cursor to retrieve the SQL research results
        Cursor cursor = database.rawQuery( query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            lat_list.add(cursor.getDouble(4));
            lon_list.add(cursor.getDouble(3));
            text_list.add(cursor.getString(1));
            desc_list.add(cursor.getString(5));
            cursor.moveToNext();
        }
        // close the Dataabse connection
        cursor.close();
        databaseAccess.close();
        bus_line = null;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;

        // create alter dialog when there are no results after searching
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if ((lat_list.size()==0) && (point_class!=null)){
            //Uncomment the below code to Set the message and title from the strings.xml file
            //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

            //Setting message manually and performing action on button click
            builder.setMessage("Sorry,we cannot help you find the location you want")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();

                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            //alert.setTitle("AlertDialogExample");
            alert.show();
        };

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/rosiedosie/ck5mmueq601l11ip08r46wlqn"), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

                SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

                mapboxMap.getStyle().addImage("result-marker", BitmapFactory.decodeResource(getResources(), R.drawable.search_result));
                final Map<String, String> desc = new HashMap<String, String>();
                //search new_search = new search();

                // looping all searching results and show them on the Map
                for(int i=0;i< lat_list.size() ; i++){
                    symbolManager.create(new SymbolOptions().
                            withLatLng(new LatLng(lat_list.get(i), lon_list.get(i))).
                            withIconImage("result-marker").
                            withIconAnchor("bottom").
                            withTextField(text_list.get(i)));
                    desc.put(text_list.get(i),desc_list.get(i));
                }

                // click the symbol on the map and show up the pop-up with information
                symbolManager.addClickListener(new OnSymbolClickListener() {

                    @Override
                    public void onAnnotationClick(Symbol symbol) {
                        String name = symbol.getTextField();
                        String description = desc.get(name);
                        TextView txtclose;
                        ImageButton btnGoogle;

                        myDialog.setContentView(R.layout.custompopup);
                        myDialog.show();

                        TextView describ = (TextView)myDialog.findViewById(R.id.describ);
                        describ.setText(description);
                        TextView name_text = (TextView)myDialog.findViewById(R.id.name);
                        name_text.setText(name);
                        btnGoogle = (ImageButton) myDialog.findViewById(R.id.btnGoogle);
                        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        btnGoogle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("geo:51.02855,13.723903"));
                                startActivity(intent);

                            }
                        });
                        Toast.makeText(MainActivity.this, "Click on Button for Navigation",
                                Toast.LENGTH_LONG).show();

                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(symbol.getLatLng())
                                        .build()));
                    }
                });

            }
        });
    }
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // suing customized icon for the current location
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(this)
                            .foregroundDrawable(R.drawable.triangle)
                            .build();
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(locationComponentOptions)
                            .build();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //set location permission dialog
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
