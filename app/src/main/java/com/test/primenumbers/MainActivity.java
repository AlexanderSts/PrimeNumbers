package com.test.primenumbers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.primenumbers.adapters.MyListAdapter;
import com.test.primenumbers.async.MyAsyncTask;
import com.test.primenumbers.base.ITaskLoaderListener;
import com.test.primenumbers.utils.CacheToFile;

import java.util.ArrayList;
/*
* Main activity class,
* EditText, Button and ListView
 */
public class MainActivity extends FragmentActivity implements ITaskLoaderListener {
    private EditText mEditText;
    ArrayList<Long> productList;
    MyListAdapter dataAdapter = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_start_stop_button).setOnClickListener(mButtonClckListener);
        mEditText = (EditText) findViewById(R.id.main_input_field);
        listView = (ListView) findViewById(R.id.main_results_list);
        //field editing finished
        mEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_NEXT ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                            startCalculation();
                        }
                        return false;
                    }
                });
        initList();
    }

    private void startCalculation() {
        hideKeyboard();
        doExecuteCalculationAsync();
    }

    /*
    * hide keyboard when button clicked
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


    private void initList() {
        //create an ArrayAdaptar from the String Array
        productList = new ArrayList<Long>();
        dataAdapter = new MyListAdapter(this,
                R.layout.list_element, productList);
        listView.setAdapter(dataAdapter);
        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);
    }

    private void clearResults() {
        productList.clear();
        dataAdapter.clear();
        dataAdapter.notifyDataSetChanged();
        if (listView != null) {
            listView.invalidateViews();
        }
        initList();
    }

    public synchronized void addToList(ArrayList<Long> productArray) {
        if (productArray != null) {
            if (productArray.size() > 0) {
                for (int i = 0; i < productArray.size(); i++) {
                    if (productArray.get(i) != null) {
                        dataAdapter.addTo(productArray.get(i), 0);
                        productList.add(0, productArray.get(i));
                        dataAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    protected void doExecuteCalculationAsync() {
        String inputValue = mEditText.getText().toString();
        if (checkForCorrectInput(inputValue)) {
            long maxValue = Long.parseLong(inputValue);
            MyAsyncTask.execute(this, this, maxValue);
        }
    }

    private boolean checkForCorrectInput(String inputValue) {
        long maxValue;
        try {
            maxValue = Long.parseLong(inputValue);
            if (maxValue < Long.parseLong(getResources().getString(R.string.min_value)) || maxValue > Long.parseLong(getResources().getString(R.string.max_value))) {
                String diapasonString = getResources().getString(R.string.txt_need_diapason_begin) + " " + getResources().getString(R.string.min_value) + " " + getResources().getString(R.string.txt_need_diapason_center) + " " + getResources().getString(R.string.max_value) + " " + getResources().getString(R.string.txt_need_diapason_end) + " " + getResources().getString(R.string.max_recommended_value);
                Toast.makeText(this, diapasonString, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.txt_need_long), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onLoadFinished(Object data) {
        clearResults();
        addToList((ArrayList<Long>) data);
    }

    @Override
    public void onCancelLoad() {
        clearResults();
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnClickListener mButtonClckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startCalculation();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.menu_clear));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.menu_clear))) {
            //fill caching file with new array
            ArrayList<Long> temp = new ArrayList<>();
            temp.add(2l);
            temp.add(3l);
            (new CacheToFile(this.getApplicationContext())).writeObject(temp, getResources().getString(R.string.cache_file_name));
            Toast.makeText(this, getResources().getString(R.string.txt_cache_cleared), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
