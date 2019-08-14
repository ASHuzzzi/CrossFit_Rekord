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

import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterCharacter;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageDefinition;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.interfaces.CharacterListener;

public class CharacterFragment extends Fragment {

    private RecyclerView recViewCharacter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        recViewCharacter = view.findViewById(R.id.rvCharacter);
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof TitleChange) {
            TitleChange listernerTitleChange = (TitleChange) getActivity();
            listernerTitleChange.changeTitle(R.string.title_Character_Fragment, R.string.title_Character_Fragment);
        }

        SQLiteStorageDefinition dbStorage = new SQLiteStorageDefinition(getContext());
        if (!dbStorage.checkDataBase()) {
            dbStorage.createDataBase();
        }

        List<String> listCharacter = dbStorage.getListCharacters();
        RecyclerAdapterCharacter adapter = new RecyclerAdapterCharacter(
                listCharacter,
                new CharacterListener() {
            @Override
            public void selectCharacter(String selectCharacter) {
                Bundle bundle = new Bundle();
                bundle.putString("tag", selectCharacter);
                DefinitionFragment fragment = new DefinitionFragment();
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
}