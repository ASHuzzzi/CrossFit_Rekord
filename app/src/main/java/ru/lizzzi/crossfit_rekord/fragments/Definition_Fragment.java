package ru.lizzzi.crossfit_rekord.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapter_Definition;
import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.data.DefinitionDbContarct.DBdefinition;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Definition_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Definition_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Definition_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DefinitionDBHelper mDbHelper = new DefinitionDBHelper(getContext());
    private ArrayList<String> Item_list_termin = new ArrayList<>();
    private ArrayList<String> Item_list_character = new ArrayList<>();
    private ArrayList<String> Item_list_definition = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Definition_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Definition_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Definition_Fragment newInstance(String param1, String param2) {
        Definition_Fragment fragment = new Definition_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        final View v = inflater.inflate(R.layout.fragment_definition, container, false);
        ListView lvdefinition1 = (ListView)v.findViewById(R.id.lvtest);



        mDbHelper = new DefinitionDBHelper(getContext());

        final Bundle bundle = getArguments();
        final String ri = bundle.getString("tag");
        CreateItemList(ri);
        RecyclerAdapter_Definition adapter = new RecyclerAdapter_Definition(getContext(), Item_list_termin, Item_list_definition, R.layout.item_lv_description);
        lvdefinition1.setAdapter(adapter);

        return  v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void CreateItemList(String ri){
        // Подумать на досуге о упрощении запроса до одного
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Item_list_termin.clear();
        Item_list_definition.clear();

        String[] columns = new  String[]{DBdefinition.Column_termin};
        Cursor cursor = db.query(DBdefinition.TABLE_NAME,
                columns,
                DBdefinition.Column_character + "= '" + ri + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                String name = "";
                for (String cn : cursor.getColumnNames()) {
                    name = cursor.getString(cursor.getColumnIndex(cn));
                }
                Item_list_termin.add(name);
            }while (cursor.moveToNext());
        }
        cursor.close();

        columns = new  String[]{DBdefinition.Column_description};
        cursor = db.query(DBdefinition.TABLE_NAME,
                columns,
                DBdefinition.Column_character + "= '" + ri + "'",
                null,
                null,
                null,
                null);
        if (cursor !=null && cursor.moveToFirst()){
            do {
                String name = "";
                for (String cn : cursor.getColumnNames()) {
                    name = cursor.getString(cursor.getColumnIndex(cn));
                }
                Item_list_definition.add(name);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

}