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

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterDefinition;
import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;


public class DefinitionFragment extends Fragment {

    private DefinitionDBHelper mDbHelper = new DefinitionDBHelper(getContext());
    private ArrayList<String> itemListTermin = new ArrayList<>();
    private ArrayList<String> itemListDefinition = new ArrayList<>();

    public DefinitionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_definition, container, false);
        RecyclerView lvdefinition1 = v.findViewById(R.id.rvDefinition);

        mDbHelper = new DefinitionDBHelper(getContext());

        final Bundle bundle = getArguments();
        final String ri = bundle.getString("tag");
        CreateItemList(ri);
        RecyclerAdapterDefinition adapter = new RecyclerAdapterDefinition(itemListTermin, itemListDefinition);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        lvdefinition1.setLayoutManager(mLayoutManager);
        lvdefinition1.setAdapter(adapter);

        return  v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Definition_Fragment, R.string.title_Character_Fragment);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    private void CreateItemList(String ri){
        // Подумать на досуге о упрощении запроса до одного

        itemListTermin.clear();
        itemListDefinition.clear();

        itemListTermin = mDbHelper.selectTermin(ri);
        itemListDefinition = mDbHelper.selectDescription(ri);

    }

}
