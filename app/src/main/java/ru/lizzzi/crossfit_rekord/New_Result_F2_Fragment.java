package ru.lizzzi.crossfit_rekord;

import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterExercises;
import ru.lizzzi.crossfit_rekord.data.DefinitionDBHelper;
import ru.lizzzi.crossfit_rekord.data.DefinitionDbContarct;
import ru.lizzzi.crossfit_rekord.data.WodDBHelper;
import ru.lizzzi.crossfit_rekord.data.WodDbContract;


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

    String m_new_quantity;
    String m_new_exercise;
    String m_new_weigth;
    String key;

    SQLiteDatabase db2;
    private WodDBHelper mDbHelper2 = new WodDBHelper(getContext());

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

        final EditText new_quantity = (EditText) view.findViewById(R.id.et_new_qantity);
        final AutoCompleteTextView new_exercise = (AutoCompleteTextView) view.findViewById(R.id.aCTV_new_exercise);
        final EditText new_weigth = (EditText) view.findViewById(R.id.et_new_weight);

        final Bundle bundle = getArguments();
        key = bundle.getString("tag");


        Button add_exercise = (Button) view.findViewById(R.id.bt_add_exercise);
        Button save = (Button) view.findViewById(R.id.bt_save);


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

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.aCTV_new_exercise);
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, Item_list));


        //fillData(); использовалв первых тестах, пока оставил на случай если понадобиться позже
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rlv_exercise_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapterExercises(exercises, new ListenerActivity() {
            @Override
            public void Remove(String exercise, int position) {
                exercises.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, getItemCount());
                autoCompleteTextView.setText(" ");
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        add_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_new_quantity = new_quantity.getText().toString();
                m_new_exercise = new_exercise.getText().toString();
                m_new_weigth = new_weigth.getText().toString();

                exercises.add(new Exercises(m_new_quantity,m_new_exercise,m_new_weigth, key));
                mAdapter.notifyDataSetChanged();
            }
        });

        mDbHelper2 = new WodDBHelper(getContext());
        db2 = mDbHelper2.getReadableDatabase();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < exercises.size() ; j++ ){
                    m_new_quantity = String.valueOf(exercises.get(j).quantity);
                    m_new_exercise = String.valueOf(exercises.get(j).exercise);
                    m_new_weigth = String.valueOf(exercises.get(j).weight);
                    key = String.valueOf(exercises.get(j).wodkey);

                    ContentValues values = new ContentValues();
                    values.put(WodDbContract.DBWod.Column_quantity, m_new_quantity);
                    values.put(WodDbContract.DBWod.Column_exercise, m_new_exercise);
                    values.put(WodDbContract.DBWod.Column_weight, m_new_weigth);
                    values.put(WodDbContract.DBWod.Column_wodkey, key);

                    db2.insert(WodDbContract.DBWod.TABLE_NAME,
                            null,
                            values);
                }
            }
        });

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
        final Bundle bundle = getArguments();
        final String ri = bundle.getString("tag");
        exercises.clear();
        exercises.add(new Exercises("1", "1","1", ri));
    }

    public int getItemCount(){
        return exercises.size();
    }
}
