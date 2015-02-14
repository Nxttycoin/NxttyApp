package com.nxtty.callback;


public interface ResultCallback {
    public void onCompleted(Exception e, String result, String methodInfo);
}
