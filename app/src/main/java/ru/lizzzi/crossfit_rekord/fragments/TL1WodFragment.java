package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;


public class TL1WodFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvWarmUp;
    private TextView tvSkill;
    private TextView tvWOD;
    private TextView tvLevelSc;
    private TextView tvLevelRx;
    private TextView tvLevelRxPlus;
    private LinearLayout llMain;
    private LinearLayout llLayoutError;
    private LinearLayout llEmptyData;
    private ProgressBar pbProgressBar;

    private NetworkCheck NetworkCheck; //переменная для проврки сети

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private OnFragmentInteractionListener mListener;

    private static final String APP_PREFERENCES = "audata";
    private static final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private Runnable runnableOpenFragment;

    public TL1WodFragment() {
        // Required empty public constructor
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tl1wod, container, false);
        tvWarmUp = v.findViewById(R.id.tvWarmUp);
        tvSkill = v.findViewById(R.id.tvSkill);
        tvWOD = v.findViewById(R.id.tvWOD);
        tvLevelSc = v.findViewById(R.id.tvLevelSc);
        tvLevelRx = v.findViewById(R.id.tvLevelRx);
        tvLevelRxPlus = v.findViewById(R.id.tvLevelRxplus);

        llMain = v.findViewById(R.id.llMain);
        Button buttonError = v.findViewById(R.id.button5);
        llLayoutError = v.findViewById(R.id.Layout_Error);
        llEmptyData = v.findViewById(R.id.llEmptyData);
        pbProgressBar = v.findViewById(R.id.progressBar3);

        llLayoutError.setVisibility(View.INVISIBLE);
        llMain.setVisibility(View.INVISIBLE);
        llEmptyData.setVisibility(View.INVISIBLE);
        pbProgressBar.setVisibility(View.VISIBLE);


        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String result_check = bundle.getString("result");
                if (result_check != null){
                    if (result_check.equals("false")){
                        llLayoutError.setVisibility(View.VISIBLE);
                        pbProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                NetworkCheck = new NetworkCheck(getContext());
                boolean resultCheck = NetworkCheck.checkInternet();
                if (resultCheck){
                    loadExerciseAsyncTaskLoader();

                }else {
                    Message msg = handlerOpenFragment.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.valueOf(false));
                    msg.setData(bundle);
                    handlerOpenFragment.sendMessage(msg);
                }
            }
        };

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbProgressBar.setVisibility(View.VISIBLE);
                llLayoutError.setVisibility(View.INVISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

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
        pbProgressBar.setVisibility(View.INVISIBLE);
        if (data != null && data.size() > 0){
            if(!String.valueOf(data.get(0).get("warmup")).equals("null")){
                tvWarmUp.setText(String.valueOf(data.get(0).get("warmup")));
            }else {
                tvWarmUp.setText("—");
            }
            if(!String.valueOf(data.get(0).get("skill")).equals("null")){
                tvSkill.setText(String.valueOf(data.get(0).get("skill")));
            }else {
                tvSkill.setText("—");
            }
            if(!String.valueOf(data.get(0).get("wod")).equals("null")){
                tvWOD.setText(String.valueOf(data.get(0).get("wod")));
            }else {
                tvWOD.setText("—");
            }
            if(!String.valueOf(data.get(0).get("Sc")).equals("null")){
                tvLevelSc.setText(String.valueOf(data.get(0).get("Sc")));
            }else {
                tvLevelSc.setText("—");
            }
            if(!String.valueOf(data.get(0).get("Rx")).equals("null")){
                tvLevelRx.setText(String.valueOf(data.get(0).get("Rx")));
            }else {
                tvLevelRx.setText("—");
            }
            if(!String.valueOf(data.get(0).get("Rxplus")).equals("null")){
                tvLevelRxPlus.setText(String.valueOf(data.get(0).get("Rxplus")));;
            }else {
                tvLevelRxPlus.setText("—");
            }
            llMain.setVisibility(View.VISIBLE);
        }else {
            llEmptyData.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onLoaderReset(Loader<List<Map>> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (tvWarmUp.length() < 1){
            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

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
