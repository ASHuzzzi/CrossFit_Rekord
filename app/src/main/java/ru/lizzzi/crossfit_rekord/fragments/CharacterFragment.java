package ru.lizzzi.crossfit_rekord.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;

public class CharacterFragment extends Fragment {

    private ArrayList<String> itemListCharacter = new ArrayList<>();
    private ListView lvDefinition;

    public CharacterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        lvDefinition = view.findViewById(R.id.lvdefinition);

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, itemListCharacter);
        lvDefinition.setAdapter(adapter);
        lvDefinition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = itemListCharacter.get(i);

                DefinitionFragment yfc = new DefinitionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("tag", selectedItem);
                yfc.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.container, yfc);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        mDbHelper.close();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
