package com.jclark.androidpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView prefText;
    private SharedPreferences prefs;
    private Button clearButton;
    private Button addButton;
    private LinearLayout labelHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputText = findViewById(R.id.new_url);
        prefText = findViewById(R.id.pref_list);
        clearButton = findViewById(R.id.clear_all);
        addButton = findViewById(R.id.add_url);
        labelHolder = findViewById(R.id.item_list_layout);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
//        loadPrefs();
        setButtonListeners();
        setInitialPrefLabels();
    }



    /**
     * Gets the shared preferences setting for the current list
     * @param defaultVal new list if null
     * @return Set of type String
     */
    private Set<String> getStringSet(Set<String> defaultVal) {
        return this.prefs.getStringSet("list", defaultVal);
    }

    /**
     * Save the given set to saved preferences
     * @param set set of strings to save
     * @return true after the commit is saved
     */
    private boolean saveSet(Set<String> set){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("list", set);
        return editor.commit();
    }

    /**
     * Adds a String to the given set by making a copy of it, then saving to shared preferences
     * @param originalSet original shared preference string set
     * @param newUrl new url to add
     * @return true after the commit is saved
     */
    private boolean addStringToSet(Set<String> originalSet, String newUrl){
        HashSet<String> prefList = new HashSet<>();
        prefList.addAll(originalSet);
        prefList.add(newUrl);
        return saveSet(prefList);
    }



    /**
     * Gets the current value for the list from preferences and sets the prefText field to that value
     */
    private void loadPrefs(){
        Set<String> prefList =  getStringSet(new HashSet<String>());
        String readableList = String.join(", ", prefList);
        prefText.setText(readableList);
    }

    /**
     * Adds all the URLs from the loaded preferenes as labels in the view
     */
    private void setInitialPrefLabels(){
        Set<String> existing = getStringSet(new HashSet<String>());
        if(existing.size() > 0){
            for(String str : existing){
                addUrlView(str);
            }
        }
    }


    /**
     * Set button listeners for the Add Url button and Clear all button
     */
    private void setButtonListeners(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUrl = inputText.getText().toString();
                Set<String> existing = getStringSet(new HashSet<String>());
                boolean saved = addStringToSet(existing, newUrl);
                if(saved){
//                    loadPrefs();
                    addUrlView(newUrl);
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashSet<String> blankSet = new HashSet<>();
                saveSet(blankSet);
//                loadPrefs();
                // Remove all views from the Linear Layout holding the labels
                if(labelHolder.getChildCount() > 0)
                    labelHolder.removeAllViews();
            }
        });
    }

    /**
     * Creates a textView with the given string, and adds it to the item list
     * @param text String to add to the list
     */
    private void addUrlView(String text){
        TextView valueTV = new TextView(getApplicationContext());
        valueTV.setText(text);
        valueTV.setId(Integer.parseInt("5"));
        valueTV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        labelHolder.addView(valueTV);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
