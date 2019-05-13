package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterDefinition;
import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;


public class DefinitionFragment extends Fragment {

    private List<Map<String, Object>> termsOfSelectedCharacter;
    private String selectCharacter;
    RecyclerView recViewDefinitions;

    public DefinitionFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_definition, container, false);
        recViewDefinitions = view.findViewById(R.id.rvDefinition);

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectCharacter = bundle.getString("tag");
        } else {
            selectCharacter = "A";
        }
        return  view;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Definition_Fragment, R.string.title_Character_Fragment);
        }
        termsOfSelectedCharacter = new ArrayList<>();
        termsOfSelectedCharacter = getListDefinitions(selectCharacter);
        RecyclerAdapterDefinition adapter = new RecyclerAdapterDefinition(
                getContext(),
                termsOfSelectedCharacter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recViewDefinitions.setLayoutManager(mLayoutManager);
        recViewDefinitions.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private List<Map<String, Object>> getListDefinitions(String selectCharacter) {
        DefinitionDBHelper definitionDBHelper = new DefinitionDBHelper(getContext());
        return definitionDBHelper.getTerminsAndDefinitions(selectCharacter);
    }
}
