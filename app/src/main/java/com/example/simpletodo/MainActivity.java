package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
//import android.os.FileUtils;
import org.apache.commons.io.FileUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        items = new ArrayList<>();

        // Load the data when the app starts
        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener =  new ItemsAdapter.OnLongClickListener(){
            @Override
            public void OnItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                // notify the adapter at which position the item was deleted
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                // Save the changes made
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener(){
            @Override
            public void OnItemClicked(int position) {
                Log.d("MainActivity", "Single click at position " + position);
                // Create new Activity
                    // first parameter MainActivity.this --- create an instance
                    // second parameter EditActivity.class --- we want to go to this class
                Intent i = new Intent(MainActivity.this, EditActivity.class);

                // pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);

                // display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        //Construct adapter and pass in the items
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        // set the adapter on the recycler view
        rvItems.setAdapter(itemsAdapter);
        // by default this is going to put things on your UI in vertical orientation
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                // Add item to the model
                items.add(todoItem);
                // notify adapted that an item was inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                // clear the edit text
                etItem.setText("");
                // a small pop-up to notify the user the item was successfully added
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                // Save the changes made
                saveItems();
            }
        });
    }

    @Override
    /**
     * Handle the result of the EditActivity
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_TEXT_CODE && resultCode == RESULT_OK) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // Extract the original position of the edited item rom the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //update the model at the right position with the new item text
            items.set(position, itemText);
            // notify the adapter
            itemsAdapter.notifyItemChanged(position);
            // persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "unknown call to onActivityResult");
        }
    }

    /**
     * Get the File to store the todolist in
     * @return
     */
    private File getDataFile(){
        // Parameters: directory of the app, file name
        return new File(getFilesDir(), "data.txt");
    }

    /**
     * Will load Items by reading every line of the data file
     * This method should only be called when the app first starts
     */
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    /**
     * Saves the items by writing them into the data file
     * This method should be called whenever we make any changes to the todolist
     */
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}