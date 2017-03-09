package com.zearoconsulting.zearopos.presentation.view.component;

import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;

/**
 * Created by user on 6/30/2016.
 */
public class ReboundListener  extends SimpleSpringListener{

    private View mView;

    @Override
    public void onSpringUpdate(Spring spring) {
        // On each update of the spring value, we adjust the scale of the
        // image view to match the
        // springs new value. We use the SpringUtil linear interpolation
        // function mapValueFromRangeToRange
        // to translate the spring's 0 to 1 scale to a 100% to 50% scale
        // range and apply that to the View
        // with setScaleX/Y. Note that rendering is an implementation detail
        // of the application and not
        // Rebound itself. If you need Gingerbread compatibility consider
        // using NineOldAndroids to update
        // your view properties in a backwards compatible manner.
        float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(
                spring.getCurrentValue(), 0, 1, 1, 0.5);

        float value = (float) spring.getCurrentValue();
        float scale = 1f - (value * 0.5f);

        View animateView = getAnimateView();
        if (animateView != null){
            animateView.setScaleX(mappedValue);
            animateView.setScaleY(mappedValue);
        }
    }

    public void animateView(View animateView){
        this.mView = animateView;
    }

    private View getAnimateView(){
     return this.mView;
    }
}
