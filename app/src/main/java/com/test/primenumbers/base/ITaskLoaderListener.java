package com.test.primenumbers.base;

/*
* description of callbacks from loader
 */
public interface ITaskLoaderListener {
    void onLoadFinished(Object data);

    void onCancelLoad();
}
