package com.test.primenumbers.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.test.primenumbers.R;
import com.test.primenumbers.base.AbstractTaskLoader;
import com.test.primenumbers.base.ITaskLoaderListener;
import com.test.primenumbers.base.TaskProgressDialogFragment;
import com.test.primenumbers.utils.CacheToFile;

import java.util.ArrayList;

/*
* Class to
* 1. make caltulation in usual asynctaskloader
* 2. send messages with handler to progressdialog
 */
public class MyAsyncTask extends AbstractTaskLoader {
    private static long mMaxValue;
    private static ArrayList<Long> mCachedData;
    private Context mContext;
    private static String cachedMax = "CACHED_MAX";

    public static void execute(FragmentActivity fa,
                               ITaskLoaderListener taskLoaderListener, long maxValue) {
        mMaxValue = maxValue;
        MyAsyncTask loader = new MyAsyncTask(fa);
        new TaskProgressDialogFragment.Builder(fa, loader, "Wait...")
                .setCancelable(true)
                .setTaskLoaderListener(taskLoaderListener)
                .show();
    }

    protected MyAsyncTask(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Object loadInBackground() {
        long cacheMinLimit = 1000;
        String result = getContext().getResources().getString(R.string.txt_cache_from);
        publishMessage(result);
        //get cached data only if max value is really big
        if(mMaxValue>cacheMinLimit) {
            mCachedData = (ArrayList<Long>) (new CacheToFile(mContext)).readObject(mContext.getResources().getString(R.string.cache_file_name));
        }
        //calculate new values, based on saved data
        ArrayList<Long> resultArray = new ArrayList<Long>();
        resultArray = calculatePrimeNumbers();
        //save data
        if(mMaxValue>cacheMinLimit) {
            //Not cache too long objects to save time
            if (resultArray.get(resultArray.size() - 1) < Long.parseLong(mContext.getResources().getString(R.string.max_recommended_value))) {
                SharedPreferences prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
                //check if new array is bigger then previous
                if (prefs.getLong(cachedMax, 0) < resultArray.get(resultArray.size() - 1)) {
                    result = getContext().getResources().getString(R.string.txt_wait_caching);
                    publishMessage(result);
                    (new CacheToFile(mContext)).writeObject(resultArray, mContext.getResources().getString(R.string.cache_file_name));
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putLong(cachedMax, resultArray.get(resultArray.size() - 1)).commit();
                }
            }
        }
        return resultArray;
    }

    @Override
    public Bundle getArguments() {
        return null;
    }

    @Override
    public void setArguments(Bundle args) {
    }

    /*
    * method to calculate prime numbers, previosly uses cached array.
    * Not the best algorithm, just a sample.
     */
    private ArrayList<Long> calculatePrimeNumbers() {
        // array preparing
        long updateInfoTextStep = mMaxValue / 1000;
        long lastUpdate = 0;
        ArrayList<Long> outArray = new ArrayList<Long>();
        long startI = 3l;
        if (mCachedData != null) {
            outArray = mCachedData;
            while (outArray.get(outArray.size() - 1) >= mMaxValue) {
                outArray.remove(outArray.size() - 1);
            }
            if (outArray.size() > 0) {
                startI = outArray.get(outArray.size() - 1) + 2;
            }
            if(startI==4){
                startI=3;
            }
        } else {
            outArray.add(2l);
        }
        //prime numbers calculations
        for (long i = startI; i <= mMaxValue; i = i + 2) {
            if (!(i > 10 && i % 10 == 5)) {
                boolean stopped = false;
                for (long item : outArray) {
                    if (item * item - 1 > i) {
                        break;
                    } else {
                        if (i % item == 0) {
                            stopped = true;
                            break;
                        }
                    }
                }
                if (!stopped) {
                    outArray.add(i);
                    // updating progress text should be not too often
                    if (lastUpdate == 0 || i > lastUpdate + updateInfoTextStep) {
                        int percent = (int) (100 * i * i * Math.log(i) / mMaxValue / mMaxValue / Math.log(mMaxValue));
                        String result = getContext().getResources().getString(R.string.txt_wait_percent_start) + " " + percent + getContext().getResources().getString(R.string.txt_wait_percent_end);
                        publishMessage(result);
                        lastUpdate = i;
                    }
                }
                ;

            }
        }
        return outArray;
    }
}
