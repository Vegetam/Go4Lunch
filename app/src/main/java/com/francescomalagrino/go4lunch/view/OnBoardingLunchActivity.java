package com.francescomalagrino.go4lunch.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.repo.SliderAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnBoardingLunchActivity extends AppCompatActivity {
    public static final String ON_BOARDING_TAG = OnBoardingLunchActivity.class.getSimpleName();


    @BindView(R.id.slider)
    ViewPager viewPager;
    @BindView(R.id.get_started_btn)
    Button getStartedBtn;
    @BindView(R.id.dots)
    LinearLayout dots;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    Animation animation;
    int currentPosition;


    SliderAdapter sliderAdapter;

    TextView[] dotsL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        Log.d(ON_BOARDING_TAG, "onCreate: onBoarding screen ");
        //Initialize
        ButterKnife.bind(this);
        //call adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        //for dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
        //remove status bar
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);





    }


    public void skip(View view) {
        startActivity(new Intent(this, MainActivity.class));

    }

    public void started(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }


    public void next(View view) {
        viewPager.setCurrentItem(currentPosition + 1);
        finish();

    }

    private void addDots(int position) {
        dotsL = new TextView[4];
        dots.removeAllViews();

        for (int i = 0; i < dotsL.length; i++) {
            dotsL[i] = new TextView(this);
            dotsL[i].setText(Html.fromHtml("&#8226"));
            dotsL[i].setTextSize(35);
            dots.addView(dotsL[i]);
        }

        if (dotsL.length > 0) {
            dotsL[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }


    //adapter dots
    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPosition = position;

            if (position == 0) {
                getStartedBtn.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                getStartedBtn.setVisibility(View.INVISIBLE);
            } else if (position == 2) {
                getStartedBtn.setVisibility(View.INVISIBLE);
            } else {
                animation = AnimationUtils.loadAnimation(OnBoardingLunchActivity.this, R.anim.side_anim);
                getStartedBtn.setAnimation(animation);
                getStartedBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
