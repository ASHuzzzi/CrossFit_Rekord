package ru.lizzzi.crossfit_rekord.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TL1WodFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TL1WodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TL1WodFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView tvWarmUp;
    TextView tvSkill;
    TextView tvWOD;
    TextView tvLevelA;
    TextView tvLevelB;
    TextView tvLevelC;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerFragment;
    private Thread threadOpenFragment;

    private OnFragmentInteractionListener mListener;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    public TL1WodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tl1wod, container, false);
        tvWarmUp = v.findViewById(R.id.tvWarmUp);
        tvSkill = v.findViewById(R.id.tvSkill);
        tvWOD = v.findViewById(R.id.tvWOD);
        tvLevelA = v.findViewById(R.id.tvLevelA);
        tvLevelB = v.findViewById(R.id.tvLevelB);
        tvLevelC = v.findViewById(R.id.tvLevelC);

        Runnable runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    loadExerciseAsyncTaskLoader();

                }else {

                }
            }
        };
        threadOpenFragment = new Thread(runnableOpenFragment);
        threadOpenFragment.setDaemon(true);


        return v;
    }

    private void loadExerciseAsyncTaskLoader(){

        SharedPreferences mSettings = getContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String selectedDay =  mSettings.getString(APP_PREFERENCES_SELECTEDDAY, "");

        Bundle bundle = new Bundle();
        bundle.putString("Selected_day", selectedDay);
        bundle.putString("Table", "exercises");
        int LOADER_ID2 = 2;
        getLoaderManager().initLoader(LOADER_ID2, bundle, this).forceLoad();
    }

    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        Loader<List<Map>> loader;
        loader = new WorkoutDetailsLoaders(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Map>> loader, List<Map> data) {
        if (data != null && data.size() > 0){
                tvWarmUp.setText(String.valueOf(data.get(0).get("warmup")));
                tvSkill.setText(String.valueOf(data.get(0).get("skill")));
                tvWOD.setText(String.valueOf(data.get(0).get("wod")));
                tvLevelA.setText(String.valueOf(data.get(0).get("A")));
                tvLevelB.setText(String.valueOf(data.get(0).get("B")));
                tvLevelC.setText(String.valueOf(data.get(0).get("C")));
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();

        //bundle = getArguments();
        //final String ri = bundle.getString("tag");
        //bundle.putString("Selected_day", ri);
        if (tvWarmUp.length() < 1){
            threadOpenFragment.start();
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
}
