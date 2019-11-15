package com.jclark.androidpreferences;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private SharedPreferences prefs;
    private Button clearButton;
    private Button addButton;
    private LinearLayout labelHolder;
    private Button deleteSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputText = findViewById(R.id.new_url);
        clearButton = findViewById(R.id.clear_all);
        addButton = findViewById(R.id.add_url);
        labelHolder = findViewById(R.id.item_list_layout);
        deleteSelected = findViewById(R.id.delete_selected);
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
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
        HashSet<String> prefList = new HashSet<>(originalSet);
        prefList.add(newUrl);
        return saveSet(prefList);
    }

    /**
     * Removes the given string from shared prefs
     * Gets the current shared prefs, copies it (leaving out the given string), then saves to shared prefs
     * @param remove string to remove
     * @return true once commit is finished
     */
    private boolean removeStringFromSet(String remove){
        if(!(remove == null || remove.isEmpty())) {
            Set<String> existing = getStringSet(new HashSet<String>());
            HashSet<String> prefList = new HashSet<>();
            if (existing.size() > 0) {
                for (String str : existing) {
                    if (!str.equalsIgnoreCase(remove)) {
                        prefList.add(str);
                    }
                }
                return saveSet(prefList);
            }
        }
        return false;
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
     * Set button listeners for the Add Url, delete selected, and Clear all button
     */
    private void setButtonListeners(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUrl = inputText.getText().toString();
                Set<String> existing = getStringSet(new HashSet<String>());
                boolean saved = addStringToSet(existing, newUrl);
                if(saved){
                    addUrlView(newUrl);
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save an empty set to shared preferences
                HashSet<String> blankSet = new HashSet<>();
                saveSet(blankSet);
                // Remove all views from the Linear Layout holding the labels
                if(labelHolder.getChildCount() > 0)
                    labelHolder.removeAllViews();
            }
        });
        deleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Keep track of rows that need to be deleted
                HashMap<String, LinearLayout> deleteViews = new HashMap<>();
                // Find rows that are checked and note the row number
                for (int i = 0; i < labelHolder.getChildCount(); i++) {
                    LinearLayout itemRow = (LinearLayout)labelHolder.getChildAt(i);
                    if(isItemRowChecked(itemRow)){
                        deleteViews.put(getRowText(itemRow), itemRow);
                    }
                }
                // Delete all checked rows
                Iterator it = deleteViews.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if(removeStringFromSet(pair.getKey().toString())){
                        labelHolder.removeView((LinearLayout)pair.getValue());
                    }
                }
            }
        });
    }

    /**
     * Creates a textView with the given string, and adds it to the item list
     * @param text String to add to the list
     */
    private void addUrlView(String text){
        final String rowName = text;
        // Create a new Layout to hold items
        final LinearLayout itemRow = new LinearLayout(getApplicationContext());
        itemRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL));
        itemRow.setGravity(Gravity.CENTER);
        itemRow.setPadding(0,0,0, 32);

//        // Create delete button
//        ImageButton deleteButton = new ImageButton(getApplicationContext());
//        deleteButton.setImageResource(R.drawable.ic_close_black_24dp);
//        deleteButton.setBackgroundColor(Color.TRANSPARENT);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.rightMargin = 64;
//        deleteButton.setLayoutParams(params);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(removeStringFromSet(rowName)) {
//                    deleteRow(itemRow);
//                }
//            }
//        });

        // Create checkbox
        CheckBox check = new CheckBox(getApplicationContext());
        check.setPadding(16,0,64,0);

        // Create text
        TextView nameText = new TextView(getApplicationContext());
        nameText.setText(text);
        nameText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Add everything
//        itemRow.addView(deleteButton);
        itemRow.addView(check);
        itemRow.addView(nameText);
        labelHolder.addView(itemRow);
    }

    /**
     * Delete a row from shared prefs and the Linear Layout
     * @param itemRow LinearLayout of the row to be deleted
     */
    private void deleteRow(View itemRow){
        ((ViewManager)itemRow.getParent()).removeView(itemRow);
    }

    /**
     * Find the checkbox in the item row and tell us if it's checked
     * @param itemRow linearLayout containing a checkbox
     * @return true if the checkbox is checked
     */
    private boolean isItemRowChecked(LinearLayout itemRow){
        for (int i = 0; i < itemRow.getChildCount(); i++) {
            if(itemRow.getChildAt(i) instanceof CheckBox){
                CheckBox check = (CheckBox) itemRow.getChildAt(i);
                return check.isChecked();
            }
        }
            return false;
    }

    /**
     * Get the URL string from a row
     * @param itemRow linear layout containing a checkbox and text field
     * @return text from the text field
     */
    private String getRowText(LinearLayout itemRow){
        for (int i = 0; i < itemRow.getChildCount(); i++) {
            if (itemRow.getChildAt(i) instanceof TextView && !(itemRow.getChildAt(i) instanceof CheckBox)) {
                TextView urlText = (TextView)itemRow.getChildAt(i);
                String text = urlText.getText().toString();
                return text;
            }
        }
        return null;
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
