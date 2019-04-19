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

    private ArrayList<String> listCharacter = new ArrayList<>();
    private RecyclerView recViewCharacter;

    public CharacterFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        recViewCharacter = view.findViewById(R.id.rvCharacter);
        return  view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Character_Fragment, R.string.title_Character_Fragment);
        }

        DefinitionDBHelper definitionDBHelper = new DefinitionDBHelper(getContext());

        try {
            definitionDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        //definitionDBHelper.openDataBase();

        listCharacter.clear();
        listCharacter = definitionDBHelper.getListCharacters();
        //definitionDBHelper.close();
        RecyclerAdapterCharacter adapter = new RecyclerAdapterCharacter(listCharacter, new ListernerCharacter() {
            @Override
            public void SelectCharacter(String selectCharacter) {
                DefinitionFragment fragment = new DefinitionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tag", selectCharacter);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right);
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recViewCharacter.setLayoutManager(layoutManager);
        recViewCharacter.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
