package com.soundlab.app.presenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.soundlab.R;

import java.util.List;

import com.soundlab.app.view.CustomButton;
import com.soundlab.app.view.activity.LoginActivity;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {
    private Context context;
    private List<Integer> slideLayouts;

    private ViewPager2 viewPager;

    public SlideAdapter(Context context, List<Integer> slideLayouts, ViewPager2 viewPager) {
        this.context = context;
        this.slideLayouts = slideLayouts;
        this.viewPager = viewPager;
    }

    public class SlideViewHolder extends RecyclerView.ViewHolder {
        public SlideViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public SlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SlideViewHolder holder, int position) {
        // Qui puoi configurare il contenuto della slide se necessario
        if(position == 0){
            CustomButton nextButton = holder.itemView.findViewById(R.id.nextButton);
            nextButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(viewPager != null && viewPager.getCurrentItem() < slideLayouts.size() - 1){
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                }
            });
        }

        if(position == 1){
            CustomButton startButton = holder.itemView.findViewById(R.id.startButton);
            startButton.setOnClickListener(v -> {
                if(context instanceof Activity){
                    ((Activity) context).finish();
                }
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return slideLayouts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return slideLayouts.get(position);
    }
}

