package view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.soundlab.R;

import java.util.ArrayList;

import model.Artist;
import presenter.adapter.ArtistPagerAdapter;
import view.CustomCardView;
import utils.Utilities;

public class ArtistFragment extends Fragment {

    private Artist artist;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        Log.d("ArtistFragment", "onCreateView called");

        Bundle bundle = getArguments();

        if (bundle != null) {
            artist = (Artist) bundle.getSerializable("artist");
        }

        viewPager = view.findViewById(R.id.view_pager);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new SongArtistFragment());
        fragments.add(new AlbumArtistFragment());

        Lifecycle lifecycle = getViewLifecycleOwner().getLifecycle();

        ArtistPagerAdapter adapter = new ArtistPagerAdapter(getChildFragmentManager(), lifecycle, fragments);
        viewPager.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Utilities.changeStatusBarColorFragment(this, R.color.drak_pink);

        if (artist != null) {
            TextView nameTextView = view.findViewById(R.id.nomeArtista_TextView);
            nameTextView.setText(artist.getName());
        }

        TextView braniTextView = view.findViewById(R.id.brani_TextView);
        TextView albumTextView = view.findViewById(R.id.album_TextView);

        CustomCardView braniCradView = view.findViewById(R.id.brani_cardView);
        CustomCardView albumCardView = view.findViewById(R.id.album_cardView);

        ImageView braniSelector_imageView = view.findViewById(R.id.braniSelector);
        ImageView albumSelector_imageView = view.findViewById(R.id.albumSelector);

        albumSelector_imageView.setImageAlpha(0);
        braniTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.pink));

        braniCradView.setOnClickListener(v -> viewPager.setCurrentItem(0, false));

        albumCardView.setOnClickListener(v -> viewPager.setCurrentItem(1, false));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        braniSelector_imageView.setImageAlpha(255);
                        braniTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.pink));
                        albumSelector_imageView.setImageAlpha(0);
                        albumTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                        break;
                    case 1:
                        braniSelector_imageView.setImageAlpha(0);
                        braniTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                        albumSelector_imageView.setImageAlpha(255);
                        albumTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.pink));
                        break;
                }
            }
        });

    }


}