package com.zearoconsulting.zearopos.presentation.presenter;

/**
 * Created by saravanan on 03-03-2017.
 */

public class ParsingStatusListener {

    public interface OnParsingStateListener {
        void onParsingSuccess();
    }

    private static ParsingStatusListener mInstance;
    private OnParsingStateListener mListener;

    private ParsingStatusListener() {}

    public static ParsingStatusListener getInstance() {
        if(mInstance == null) {
            mInstance = new ParsingStatusListener();
        }
        return mInstance;
    }

    public void setListener(OnParsingStateListener listener) {
        mListener = listener;
    }

    public void parsingStatus() {
        if(mListener != null) {
            mListener.onParsingSuccess();
        }
    }
}
