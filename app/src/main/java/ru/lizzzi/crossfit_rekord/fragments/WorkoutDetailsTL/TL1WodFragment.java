package ru.lizzzi.crossfit_rekord.fragments.WorkoutDetailsTL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.loaders.WorkoutDetailsLoaders;


public class TL1WodFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Map>>{

    private TextView textWarmUp;
    private TextView textSkill;
    private TextView textWOD;
    private TextView textLevelSc;
    private TextView textLevelRx;
    private TextView textLevelRxPlus;
    private LinearLayout linLayMain;
    private LinearLayout linLayError;
    private LinearLayout linLayEmptyData;
    private TextView textEmptyData;
    private ProgressBar progressBar;

    private Handler handlerOpenFragment;
    private Thread threadOpenFragment;

    private final String APP_PREFERENCES = "audata";
    private final String APP_PREFERENCES_SELECTEDDAY = "SelectedDay";

    private Runnable runnableOpenFragment;
    private SharedPreferences sharedPreferences;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tl1wod, container, false);
        textWarmUp = view.findViewById(R.id.tvWarmUp);
        textSkill = view.findViewById(R.id.tvSkill);
        textWOD = view.findViewById(R.id.tvWOD);
        textLevelSc = view.findViewById(R.id.tvLevelSc);
        textLevelRx = view.findViewById(R.id.tvLevelRx);
        textLevelRxPlus = view.findViewById(R.id.tvLevelRxplus);
        textEmptyData = view.findViewById(R.id.tvTL1ED1);

        linLayMain = view.findViewById(R.id.llMain);
        Button buttonError = view.findViewById(R.id.button5);
        linLayError = view.findViewById(R.id.Layout_Error);
        linLayEmptyData = view.findViewById(R.id.llEmptyData);
        progressBar = view.findViewById(R.id.progressBar3);


        //хэндлер для потока runnableOpenFragment
        handlerOpenFragment = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();
                boolean checkDone = bundle.getBoolean("result");
                if (checkDone) {
                    getExercise();
                } else {
                    linLayError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                Network network = new Network(getContext());
                boolean checkDone = network.checkConnection();
                Bundle bundle = new Bundle();
                bundle.putBoolean("result", checkDone);
                Message message = handlerOpenFragment.obtainMessage();
                message.setData(bundle);
                handlerOpenFragment.sendMessage(message);
            }
        };

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                linLayError.setVisibility(View.INVISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

        sharedPreferences = Objects.requireNonNull(
                getContext()).getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return view;
    }

    private void getExercise(){
        String selectedDay =  sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
        Bundle bundle = new Bundle();
        bundle.putString("Selected_day", selectedDay);
        bundle.putString("Table", "exercises");
        int LOADER_GET_EXERCISE = 2;
        getLoaderManager().initLoader(LOADER_GET_EXERCISE, bundle, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<List<Map>> onCreateLoader(int id, Bundle args) {
        return new WorkoutDetailsLoaders(getContext(), args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map>> loader, List<Map> wodOfDay) {
        progressBar.setVisibility(View.INVISIBLE);
        if (wodOfDay != null && wodOfDay.size() > 0){
            if(!String.valueOf(wodOfDay.get(0).get("warmup")).equals("null")){
                textWarmUp.setText(String.valueOf(wodOfDay.get(0).get("warmup")));
            }else {
                textWarmUp.setText("—");
            }
            if(!String.valueOf(wodOfDay.get(0).get("skill")).equals("null")){
                textSkill.setText(String.valueOf(wodOfDay.get(0).get("skill")));
            }else {
                textSkill.setText("—");
            }
            if(!String.valueOf(wodOfDay.get(0).get("wod")).equals("null")){
                textWOD.setText(String.valueOf(wodOfDay.get(0).get("wod")));
            }else {
                textWOD.setText("—");
            }
            if(!String.valueOf(wodOfDay.get(0).get("Sc")).equals("null")){
                textLevelSc.setText(String.valueOf(wodOfDay.get(0).get("Sc")));
            }else {
                textLevelSc.setText("—");
            }
            if(!String.valueOf(wodOfDay.get(0).get("Rx")).equals("null")){
                textLevelRx.setText(String.valueOf(wodOfDay.get(0).get("Rx")));
            }else {
                textLevelRx.setText("—");
            }
            if(!String.valueOf(wodOfDay.get(0).get("Rxplus")).equals("null")){
                textLevelRxPlus.setText(String.valueOf(wodOfDay.get(0).get("Rxplus")));
            }else {
                textLevelRxPlus.setText("—");
            }
            linLayMain.setVisibility(View.VISIBLE);
        }else {
            textEmptyData.setText(getResources().getText(R.string.TL1NoData1));
            linLayEmptyData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Map>> loader) {
    }

    @Override
    public void onStart(){
        super.onStart();

        if (textWarmUp.length() < 1) {
            linLayError.setVisibility(View.INVISIBLE);
            linLayMain.setVisibility(View.INVISIBLE);
            linLayEmptyData.setVisibility(View.INVISIBLE);

            try {
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                SimpleDateFormat dateFormatMonthDay =
                        new SimpleDateFormat("MMdd", Locale.getDefault());
                SimpleDateFormat dateFormatHourToday =
                        new SimpleDateFormat("HH", Locale.getDefault());
                String savedDay =
                        sharedPreferences.getString(APP_PREFERENCES_SELECTEDDAY, "");
                Date savedDayForParse = dateFormat.parse(savedDay);
                int selectedDay = Integer.parseInt(dateFormatMonthDay.format(savedDayForParse));
                int currentDay = Integer.parseInt(dateFormatMonthDay.format(new Date()));
                int currentHour = Integer.parseInt(dateFormatHourToday.format(new Date()));

                //не показваю комплекс только если дата = сегодня, а время до 21 часа
                if (selectedDay != currentDay || currentHour > 20) {
                    progressBar.setVisibility(View.VISIBLE);
                    threadOpenFragment = new Thread(runnableOpenFragment);
                    threadOpenFragment.setDaemon(true);
                    threadOpenFragment.start();
                } else {
                    textEmptyData.setText(getResources().getText(R.string.TL1NoTime1));
                    progressBar.setVisibility(View.INVISIBLE);
                    linLayEmptyData.setVisibility(View.VISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                linLayError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_WorkoutDetails_Fragment, R.string.title_CalendarWod_Fragment);
        }
    }
}
