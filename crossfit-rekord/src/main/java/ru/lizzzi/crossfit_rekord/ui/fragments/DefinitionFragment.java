package ru.lizzzi.crossfit_rekord.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.ui.adapters.RecyclerAdapterDefinition;
import ru.lizzzi.crossfit_rekord.data.DefinitionStorage;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.items.TermItem;

public class DefinitionFragment extends Fragment {

    private String selectedCharacter;
    private RecyclerAdapterDefinition adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition, container, false);

        Bundle bundle = getArguments();
        selectedCharacter = (bundle != null) ? bundle.getString("tag") : "A";

        RecyclerView recViewDefinitions = view.findViewById(R.id.rvDefinition);
        adapter = new RecyclerAdapterDefinition();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recViewDefinitions.setLayoutManager(mLayoutManager);
        recViewDefinitions.setAdapter(adapter);

        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_Definition_Fragment,
                    R.string.title_Character_Fragment);
        }
        adapter.add(getListDefinitions(selectedCharacter));
        adapter.notifyDataSetChanged();

    }

    private List<TermItem> getListDefinitions(String selectedCharacter) {
        DefinitionStorage dbStorage = new DefinitionStorage(getContext());
        return dbStorage.getTermsAndDefinitions(selectedCharacter);
    }
}