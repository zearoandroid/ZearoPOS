package com.zearoconsulting.zearopos.presentation.presenter;

/**
 * Created by saravanan on 28-05-2016.
 */
public interface FragmentLifecycle {

    public void onPauseFragment(int pos);
    public void onResumeFragment(int pos, long categoryId);
}
