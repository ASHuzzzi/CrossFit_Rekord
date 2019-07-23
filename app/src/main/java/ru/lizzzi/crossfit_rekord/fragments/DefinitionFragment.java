package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterDefinition;
import ru.lizzzi.crossfit_rekord.data.SQLiteStorageDefinition;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;

public class DefinitionFragment extends Fragment {

    private String selectedCharacter;
    private RecyclerView recViewDefinitions;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition, container, false);
        recViewDefinitions = view.findViewById(R.id.rvDefinition);
        Bundle bundle = getArguments();
        selectedCharacter = (bundle != null) ? bundle.getString("tag") : "A";
        return view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Definition_Fragment, R.string.title_Character_Fragment);
        }
        List<Map<String, String>> termsOfSelectedCharacter = getListDefinitions(selectedCharacter);
        RecyclerAdapterDefinition adapter = new RecyclerAdapterDefinition(
                getContext(),
                termsOfSelectedCharacter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recViewDefinitions.setLayoutManager(mLayoutManager);
        recViewDefinitions.setAdapter(adapter);
    }

    private List<Map<String, String>> getListDefinitions(String selectedCharacter) {
        SQLiteStorageDefinition dbStorage = new SQLiteStorageDefinition(getContext());
        return dbStorage.getTerminsAndDefinitions(selectedCharacter);
    }
}
