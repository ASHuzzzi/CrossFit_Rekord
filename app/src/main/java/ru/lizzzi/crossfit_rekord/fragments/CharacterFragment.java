package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterCharacter;
import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListernerCharacter;

public class CharacterFragment extends Fragment {

    private ArrayList<String> itemListCharacter = new ArrayList<>();
    private RecyclerView rvCharacter;

    public CharacterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        rvCharacter = view.findViewById(R.id.rvCharacter);

        return  view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Character_Fragment, R.string.title_Character_Fragment);
        }

        DefinitionDBHelper mDbHelper = new DefinitionDBHelper(getContext());

        try {
            mDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        mDbHelper.openDataBase();

        itemListCharacter.clear();
        itemListCharacter = mDbHelper.selectCharacter();
        mDbHelper.close();
        RecyclerAdapterCharacter adapterCharacter = new RecyclerAdapterCharacter(itemListCharacter, new ListernerCharacter() {
            @Override
            public void SelectCharacter(String stCharacter) {
                DefinitionFragment yfc = new DefinitionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tag", stCharacter);
                yfc.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right);
                ft.replace(R.id.container, yfc);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvCharacter.setLayoutManager(mLayoutManager);
        rvCharacter.setAdapter(adapterCharacter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
