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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterCharacter;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageDefinition;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;

public class CharacterFragment extends Fragment {

    private RecyclerAdapterCharacter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character, container, false);

        RecyclerView recViewCharacter = view.findViewById(R.id.rvCharacter);
        adapter = new RecyclerAdapterCharacter(CharacterFragment.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recViewCharacter.setLayoutManager(layoutManager);
        recViewCharacter.setAdapter(adapter);
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_Character_Fragment,
                    R.string.title_Character_Fragment);
        }

        SQLiteStorageDefinition dbStorage = new SQLiteStorageDefinition(getContext());
        adapter.add(dbStorage.getListCharacters());
        adapter.notifyDataSetChanged();

    }

    public void openDefinitionFragment(String selectCharacter) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", selectCharacter);
        DefinitionFragment fragment = new DefinitionFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
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
    }
}