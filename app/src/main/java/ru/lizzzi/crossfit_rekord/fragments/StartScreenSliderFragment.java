package ru.lizzzi.crossfit_rekord.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.lizzzi.crossfit_rekord.R;

public class StartScreenSliderFragment extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    private Drawable backgroundImage;

    public static StartScreenSliderFragment newInstance(int page) {
        StartScreenSliderFragment pageFragment = new StartScreenSliderFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
            switch (pageNumber){
                case 0:
                    backgroundImage = getResources().getDrawable(R.drawable.foto_for_star_screen_1);
                    break;
                case 1:
                    backgroundImage = getResources().getDrawable(R.drawable.foto_for_star_screen_2);
                    break;
                case 2:
                    backgroundImage = getResources().getDrawable(R.drawable.foto_for_star_screen_3);
                    break;
                case 3:
                    backgroundImage = getResources().getDrawable(R.drawable.foto_for_star_screen_4);
                    break;
            }
        }else {
            backgroundImage = getResources().getDrawable(R.drawable.foto_for_star_screen_1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_screen_slider, container, false);

        ImageView imBackgroundImage = v.findViewById(R.id.ivSlider);
        imBackgroundImage.setImageDrawable(backgroundImage);
        return v;
    }
}
