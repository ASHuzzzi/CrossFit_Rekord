package ru.lizzzi.crossfit_rekord.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.UriParser;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.Network;
import ru.lizzzi.crossfit_rekord.interfaces.ChangeTitle;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;
import ru.lizzzi.crossfit_rekord.loaders.TableFragmentLoader;

public class RecordForTrainingFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<List<Map>>> {

    private final static int LOADER_ID = 1; //идентефикатор loader'а
    private LinearLayout linLayoutError;
    private LinearLayout linLayoutSchedule;
    private RecyclerView recyclerViewSchedule;
    private ProgressBar progressBar;
    private Button buttonToday;
    private Button buttonTomorrow;
    private Button buttonAfterTomorrow;
    private ImageView imageBackground;

    private RecyclerAdapterRecordForTrainingSelect adapter;

    private Thread threadOpenFragment;
    private Runnable runnableOpenFragment;

    private boolean todayOrNot;
    private List<List<Map>> schedule;
    private int selectedDayOfWeek; // выбранный пользователем день
    private int selectedDayForUri;
    private int selectedGym;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record_for_training_select, container, false);
        Objects.requireNonNull(getActivity()).setTitle(R.string.title_RecordForTraining_Fragment);

        buttonToday = view.findViewById(R.id.btToday);
        buttonTomorrow = view.findViewById(R.id.btTommorow);
        buttonAfterTomorrow = view.findViewById(R.id.btAftertommorow);
        Button buttonError = view.findViewById(R.id.button6);
        recyclerViewSchedule = view.findViewById(R.id.rvTrainingTime);
        linLayoutError = view.findViewById(R.id.llEror_RfTS);
        linLayoutSchedule = view.findViewById(R.id.llListTime);
        progressBar = view.findViewById(R.id.pbRfTS);
        imageBackground = view.findViewById(R.id.iv_RfTS);

        //получаю значения для кнопок
        final Date today = new Date();
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
        final Date tomorrow = gregorianCalendar.getTime();
        gregorianCalendar.add(Calendar.DAY_OF_YEAR, 1);
        final Date afterTomorrow = gregorianCalendar.getTime();

        Bundle bundle = getArguments();
        selectedGym = (bundle != null)
                ? bundle.getInt("gym")
                : Objects.requireNonNull(
                        getContext()).getResources().getInteger(R.integer.selectSheduleParnas);

        final Handler handler = new Handler(RecordForTrainingFragment.this);

        //поток запускаемый при создании экрана (запуск происходит из onStart)
        runnableOpenFragment = new Runnable() {
            @Override
            public void run() {
                Network network = new Network(getContext());
                Bundle bundle = new Bundle();
                boolean checkDone = network.checkConnection();
                if (checkDone) {
                    gregorianCalendar.setTime(today);
                    selectedDayOfWeek = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
                    todayOrNot = true;
                    selectedDayForUri = 0;
                    bundle.putBoolean("checkConnection", true);
                } else {
                    bundle.putBoolean("checkConnection", false);
                }
                Message message = handler.obtainMessage();
                message.setData(bundle);
                handler.sendMessage(message);
            }
        };

        final SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("EEE.\n d MMMM", Locale.getDefault());
        buttonToday.setText(simpleDateFormat.format(today));
        buttonTomorrow.setText(simpleDateFormat.format(tomorrow));
        buttonAfterTomorrow.setText(simpleDateFormat.format(afterTomorrow));

        buttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gregorianCalendar.setTime(today);
                linLayoutError.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                threadOpenFragment = new Thread(runnableOpenFragment);
                threadOpenFragment.setDaemon(true);
                threadOpenFragment.start();
            }
        });

        buttonToday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    gregorianCalendar.setTime(today);
                    selectedDayOfWeek = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
                    todayOrNot = true;
                    selectedDayForUri = 0;
                    drawList(schedule.get(selectedDayOfWeek -1));
                }
                return true ;
            }
        });

        buttonTomorrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    gregorianCalendar.setTime(tomorrow);
                    selectedDayOfWeek = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
                    todayOrNot = false;
                    selectedDayForUri = 1;
                    drawList(schedule.get(selectedDayOfWeek -1));
                }
                return true ;
            }
        });

        buttonAfterTomorrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (adapter != null) {
                    gregorianCalendar.setTime(afterTomorrow);
                    selectedDayOfWeek = gregorianCalendar.get(Calendar.DAY_OF_WEEK);
                    todayOrNot = false;
                    selectedDayForUri = 2;
                    drawList(schedule.get(selectedDayOfWeek -1));
                }
                return true ;
            }
        });
        return view;
    }

    private void startAsyncTaskLoader() {
        Bundle bundle = new Bundle();
        bundle.putString("SelectedGym", String.valueOf(selectedGym));
        getLoaderManager().restartLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<List<List<Map>>> onCreateLoader(int id, Bundle args) {
        Loader<List<List<Map>>> loader;
        loader = new TableFragmentLoader(getContext(), args);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<List<Map>>> loader, List<List<Map>> data) {
        if (data != null) {
            schedule = data;
            drawList(schedule.get(selectedDayOfWeek -1));
            linLayoutError.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            linLayoutSchedule.setVisibility(View.VISIBLE);
        } else {
            linLayoutError.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            linLayoutSchedule.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<List<Map>>> loader) {
    }

    private void drawList(List<Map> dailySchedule){
        adapter = new RecyclerAdapterRecordForTrainingSelect(
                getContext(),
                dailySchedule,
                todayOrNot,
                new ListenerRecordForTrainingSelect() {
            @Override
            public void selectTime(String startTime, String typesItem) {
                if (startTime.equals("outTime") && typesItem.equals("outTime")) {
                    Toast toast = Toast.makeText(
                            getContext(),
                            "Тренировка уже прошла. Выбери более позднее время!",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    UriParser uriParser = new UriParser();
                    Uri uri = uriParser.getURI(selectedGym, selectedDayForUri, startTime, typesItem);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(uri);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).
                            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewSchedule.setLayoutManager(mLayoutManager);
        recyclerViewSchedule.setAdapter(adapter);
        setPressedButtons(selectedDayForUri);
    }

    private void setPressedButtons(int selectedDay) {
        switch (selectedDay) {
            case 0:
                pushButtons(true, false, false);
                break;
            case 1:
                pushButtons(false, true, false);
                break;
            case 2:
                pushButtons(false, false, true);
                break;
        }
    }

    //метод применяющий выбор кнопок
    private void pushButtons(boolean today, boolean tomorrow, boolean afterTomorrow) {
        buttonToday.setPressed(today);
        buttonTomorrow.setPressed(tomorrow);
        buttonAfterTomorrow.setPressed(afterTomorrow);
    }

    //в onStart делаем проверку на наличие данных в адаптаре. При первом запуске адаптер пустой и
    //будет запущен поток.
    //при возврате через кнопку back адаптер будет не пустым поток не запуститься. что сохранит
    //состояние адаптера в положении перед открытием нового фрагмента
    @Override
    public void onStart() {
        super.onStart();
        if (adapter == null) {
            linLayoutSchedule.setVisibility(View.INVISIBLE);
            linLayoutError.setVisibility(View.INVISIBLE);
            threadOpenFragment = new Thread(runnableOpenFragment);
            threadOpenFragment.setDaemon(true);
            threadOpenFragment.start();
        } else {
            setPressedButtons(selectedDayForUri);
            linLayoutSchedule.setVisibility(View.VISIBLE);
        }

        if (getActivity() instanceof ChangeTitle) {
            ChangeTitle listernerChangeTitle = (ChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_RecordForTraining_Fragment, R.string.title_RecordForTraining_Fragment);
        }
        int backgroundImage =
                (selectedGym == getResources().getInteger(R.integer.selectSheduleParnas))
                ? R.drawable.background_foto_1
                : R.drawable.background_foto_2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageBackground.setImageDrawable(getResources().getDrawable(
                    backgroundImage, Objects.requireNonNull(getContext()).getTheme()));
        } else {
            imageBackground.setImageDrawable(getResources().getDrawable(backgroundImage));
        }
    }

    public void onStop() {
        super.onStop();
        if (getLoaderManager().hasRunningLoaders()) {
            getLoaderManager().destroyLoader(LOADER_ID);
        }
    }

    private static class Handler extends android.os.Handler {
        private final WeakReference<RecordForTrainingFragment> fragmentWeakReference;

        Handler(RecordForTrainingFragment fragmentInstance) {
            fragmentWeakReference = new WeakReference<>(fragmentInstance);
        }

        @Override
        public void handleMessage(Message message) {
            RecordForTrainingFragment fragment = fragmentWeakReference.get();
            if (fragment != null) {
                boolean resultCheck = message.getData().getBoolean("checkConnection");
                if (resultCheck) {
                    fragment.linLayoutError.setVisibility(View.INVISIBLE);
                    fragment.progressBar.setVisibility(View.VISIBLE);
                    fragment.startAsyncTaskLoader();
                } else {
                    fragment.linLayoutError.setVisibility(View.VISIBLE);
                    fragment.progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}