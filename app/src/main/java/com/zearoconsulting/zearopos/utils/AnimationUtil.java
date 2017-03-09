package com.zearoconsulting.zearopos.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.zearoconsulting.zearopos.presentation.view.fragment.AbstractFragment;

/**
 * Created by saravanan on 22-08-2016.
 * AnimationUtil used for recyclerview
 */
public class AnimationUtil {

    public static void aimate(RecyclerView.ViewHolder holder,boolean goesDown){

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY",goesDown==true?200:-200,0);
        animatorTranslateY.setDuration(1000);

        animatorSet.playTogether(animatorTranslateY);
        animatorSet.start();

    }

    public static void aimateProduct(RecyclerView.ViewHolder holder){
        YoYo.with(Techniques.StandUp)
                .duration(500)
                .playOn(holder.itemView);
    }

    public static void aimateCategory(RecyclerView.ViewHolder holder){
        YoYo.with(Techniques.Shake)
                .duration(1000)
                .playOn(holder.itemView);
    }
}
