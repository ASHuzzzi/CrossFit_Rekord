package ru.lizzzi.crossfit_rekord;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.data.DefinitionDbContarct;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link New_Result_F2_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link New_Result_F2_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class New_Result_F2_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<Exercises> exercises = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    DefinitionDBHelper mDbHelper = new DefinitionDBHelper(getContext());
    private ArrayList<String> Item_list = new ArrayList<>();
    AutoCompleteTextView autoCompleteTextView;

    private OnFragmentInteractionListener mListener;

    public New_Result_F2_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment New_Result_F2_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static New_Result_F2_Fragment newInstance(String param1, String param2) {
        New_Result_F2_Fragment fragment = new New_Result_F2_Fragment();
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
        View view = inflater.inflate(R.layout.fragment_new_result_f2, container, false);
        // Inflate the layout for this fragment

        mDbHelper = new DefinitionDBHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Item_list.clear();

        String[] columns = new  String[]{DefinitionDbContarct.DBdefinition.Column_termin};
        Cursor cursor = db.query(DefinitionDbContarct.DBdefinition.TABLE_NAME,
                columns,
                DefinitionDbContarct.DBdefinition.Column_attribute + "= 'exercise'",
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
                Item_list.add(name);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView1);
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, Item_list));


        fillData();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_1);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapterExercises(exercises, new ListenerActivity() {
            @Override
            public void Remove(String exercise, int position) {
                exercises.remove(position);
                mAdapter.notifyItemChanged(position);
                mAdapter.notifyItemRangeChanged(position, getItemCount());
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return view;
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

    void fillData(){
        exercises.clear();
        exercises.add(new Exercises("1", "1","1","1"));
    }

    public int getItemCount(){
        return exercises.size();
    }
}
