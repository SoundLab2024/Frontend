package com.soundlab.app.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import com.example.soundlab.R;

import java.util.ArrayList;
import java.util.List;

import com.soundlab.app.presenter.adapter.SlideAdapter;


public class SlideActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SlideAdapter slideAdapter;
    private List<Integer> slideLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        slideLayouts = new ArrayList<>();
        slideLayouts.add(R.layout.slide_one); // ID del layout della prima slide
        slideLayouts.add(R.layout.slide_two); // ID del layout della seconda slide

        viewPager = findViewById(R.id.viewPager);
        slideAdapter = new SlideAdapter(this, slideLayouts, viewPager);
        viewPager.setAdapter(slideAdapter);
        viewPager.setPageTransformer(null);


    }
}
