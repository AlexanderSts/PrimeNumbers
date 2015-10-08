package com.test.primenumbers.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.AsyncTaskLoader;

public abstract class AbstractTaskLoader extends AsyncTaskLoader<Object> {

    //type of the published values
    public static int MSGCODE_MESSAGE = 1;
    private Handler handler;
    //custom canceled flag
    private boolean canseled = false;
    public abstract Bundle getArguments();
    public abstract void setArguments(Bundle args);
    protected AbstractTaskLoader(Context context) {
        super(context);
    }

    protected void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * Helper to retrieve the string value from message
     *
     * @param msg - progress message
     * @return
     */
    public static String getMessageValue(Message msg) {
        Bundle data = msg.getData();
        if (data.containsKey("message")) {
            return data.getString("message");
        } else {
            return null;
        }
    }

    /**
     * Helper to publish string value
     *
     * @param value
     */
    protected void publishMessage(String value) {
        if (handler != null) {
            Bundle data = new Bundle();
            data.putString("message", value);
			/* Creating a message */
            Message msg = new Message();
            msg.setData(data);
            msg.what = MSGCODE_MESSAGE;
			/* Sending the message */
            handler.sendMessage(msg);
        }
    }

    public void setCanseled(boolean canselled) {
        this.canseled = canselled;
    }

    public boolean isCanselled() {
        return canseled;
    }
}
