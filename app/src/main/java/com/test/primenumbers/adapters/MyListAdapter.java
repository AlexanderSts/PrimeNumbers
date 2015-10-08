package com.test.primenumbers.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.primenumbers.R;

import java.util.ArrayList;

/**
 * Adapter for items of ListView in MainActivity
 * Could be needed if style of showing items will be changed
 */
public class MyListAdapter extends ArrayAdapter<Long> {

    private ArrayList<Long> numbersList;
    Activity mActivity;

    public MyListAdapter(Activity context, int textViewResourceId,
                         ArrayList<Long> numbersList) {

        super(context, textViewResourceId, numbersList);
        mActivity = context;
        this.numbersList = new ArrayList<Long>();
        this.numbersList.addAll(numbersList);
    }

    private class ViewHolder {
        private TextView name;
        public void setName(TextView name) {
            this.name = name;
        }
        public TextView getName() {
            return name;
        }
    }

    public void add(long number) {
        this.numbersList.add(number);
    }

    public void addTo(long number, int position) {
        this.numbersList.add(position, number);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_element, null);
            holder = new ViewHolder();
            holder.setName((TextView) convertView.findViewById(R.id.list_element));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (this.numbersList.size() <= position) {
            return convertView;
        }
        final long number = this.numbersList.get(position);
        if (holder.getName() != null && number > 0) {
            holder.getName().setText("" + number);
        }
        return convertView;
    }
}
