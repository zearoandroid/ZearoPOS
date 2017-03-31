package com.zearoconsulting.zearopos.presentation.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zearoconsulting.zearopos.R;
import com.zearoconsulting.zearopos.presentation.view.component.ReboundListener;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by saravanan on 31-03-2017.
 */

public class AlertFragment extends AbstractDialogFragment {

    private static Context context;
    private String title, message;
    private TextView mTxtTitle, mTxtMessage;
    private FancyButton mBtnOk;

    public AlertFragment() {
        // Required empty public constructor
    }

    public static AlertFragment newInstance(Context paramContext, String paramString1, String paramString2) {
        AlertFragment localDialogFragment = new AlertFragment();
        Bundle localBundle = new Bundle();
        localBundle.putString("title", paramString1);
        localBundle.putString("message", paramString2);
        localDialogFragment.setArguments(localBundle);
        context = paramContext;

        return localDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mReboundListener = new ReboundListener();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(View paramView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(paramView, savedInstanceState);

        this.title = getArguments().getString("title", "");
        this.message = getArguments().getString("message", "");

        getDialog().getWindow().setSoftInputMode(3);
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(false);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    //Stop back event here!!!
                    return true;
                } else
                    return false;
            }
        });


        this.mTxtTitle = (TextView) paramView.findViewById(R.id.txtTitle);
        this.mTxtMessage = (TextView) paramView.findViewById(R.id.txtDescription);
        this.mBtnOk = ((FancyButton) paramView.findViewById(R.id.btnAlertOk));


        this.mTxtTitle.setText(title);
        this.mTxtMessage.setText(message);

        // Add an OnTouchListener to the root view.
        this.mBtnOk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mReboundListener.animateView(mBtnOk);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mSpring.setEndValue(0);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                            }
                        }, 200);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //Add a listener to the spring
        mSpring.addListener(mReboundListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Remove a listener to the spring
        mSpring.removeListener(mReboundListener);
    }
}
