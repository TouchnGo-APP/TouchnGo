//This activity

package com.example.touchngo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.List;

//Creating the search-bars for the busline, the street name, a point close to the searched activity
//and the type of activity that is searched for

public class search extends AppCompatActivity {

    public String point_bus_line;
    public String point_street_name;
    public String point_near_type;
    public String point_class;

    //Hides the keyboard in beginning
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    };

    @Override
    //Get's the layout from the activity_search layout
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Implements actionbar with Homebutton to get back to the last activity
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Access to the categories in database
        String[] bus_lines = getResources().getStringArray(R.array.bus_line);
        String[] streetnames = getResources().getStringArray(R.array.street_names);
        String[] classes = getResources().getStringArray(R.array.category);


        //implements an autocomplete text for more userfriendlyness with the possible options
        final AutoCompleteTextView edit_bus_line = findViewById(R.id.t_bus_line);
        final AutoCompleteTextView edit_street_name = findViewById(R.id.t_street_name);
        final AutoCompleteTextView edit_near_type = findViewById(R.id.t_near_type);
        final AutoCompleteTextView edit_class = findViewById(R.id.t_class);

        // adapting arrays of search keywords to use further as a datasource
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item, bus_lines);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item, streetnames);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item,classes);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item,classes);


        //display items in the list
        edit_bus_line.setAdapter(adapter);
        edit_street_name.setAdapter(adapter1);
        edit_near_type.setAdapter(adapter2);
        edit_class.setAdapter(adapter3);

        //point_category = editText.getText().toString();
        //point_elbe = editText1.getText().toString();
        //point_fk = editText2.getText().toString();

        // keyboard will disappear when tapping the screen for more userfriendlyness after providing input by user
        edit_bus_line.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // keyboard will disappear when tapping the screen for more userfriendlyness after providing input by user
        edit_street_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // keyboard will disappear when tapping the screen for more userfriendlyness after providing input by user
        edit_near_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // keyboard will disappear when tapping the screen for more userfriendlyness after providing input by user
        edit_class.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //Implements the customized image button to submit the parameter input by user
        ImageButton submit = (ImageButton)findViewById(R.id.submit);
        final CheckBox checkBox_bus = (CheckBox)findViewById(R.id.bus);
        final CheckBox checkBox_others  = (CheckBox)findViewById(R.id.others);;

        //enables the checkbox and makes sure the other checkbox isnt checked. disables streetname when buscheckbox is checked
        checkBox_bus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    checkBox_bus.setChecked(true);
                    checkBox_others.setChecked(false);
                    edit_bus_line.setEnabled(true);
                    edit_street_name.setText("");
                    edit_street_name.setEnabled(false);

                }

            }
        });
        //enables the checkbox and makes sure the other checkbox isnt checked. disables busline when streetcheckbox is checked
        checkBox_others.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    checkBox_others.setChecked(true);
                    checkBox_bus.setChecked(false);
                    edit_street_name.setEnabled(true);
                    edit_bus_line.setText("");
                    edit_bus_line.setEnabled(false);
                }

            }
        });
        //Submits parameters after clicking the submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                point_bus_line = edit_bus_line.getText().toString();
                point_street_name = edit_street_name.getText().toString();
                point_near_type = edit_near_type.getText().toString();
                point_class = edit_class.getText().toString();

                //store the variables' values
                Intent intent = new Intent(search.this, MainActivity.class);
                intent.putExtra("point_bus_line",  point_bus_line);
                intent.putExtra("point_street_name",  point_street_name);
                intent.putExtra("point_near_type",  point_near_type);
                intent.putExtra("point_class",  point_class);
                search.this.startActivity(intent);
            }
        });
    }
}
