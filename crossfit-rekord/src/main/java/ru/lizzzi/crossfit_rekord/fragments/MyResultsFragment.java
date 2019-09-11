package ru.lizzzi.crossfit_rekord.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.adapters.RecyclerAdapterMyResults;
import ru.lizzzi.crossfit_rekord.interfaces.TitleChange;
import ru.lizzzi.crossfit_rekord.model.MyResultsViewModel;

public class MyResultsFragment extends Fragment {

    private RecyclerView recyclerResults;
    private EditText editMyWeightResult;

    private RecyclerAdapterMyResults adapter;

    private MyResultsViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_my_results, container, false);
        viewModel =
                ViewModelProviders.of(MyResultsFragment.this).get(MyResultsViewModel.class);

        initEditMyWeightResult(view);
        initRecyclerResults(view);
        initButtonSaveMyResult(view);

        return view;
    }

    private void initEditMyWeightResult(View rootView) {
        editMyWeightResult = rootView.findViewById(R.id.editMyWeightResult);
        editMyWeightResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setMyWeight(editMyWeightResult.getText().toString());
            }
        });
    }

    private void initRecyclerResults(View rootView) {
        recyclerResults = rootView.findViewById(R.id.recyclerResults);
        adapter = new RecyclerAdapterMyResults(MyResultsFragment.this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerResults = rootView.findViewById(R.id.recyclerResults);
        recyclerResults.setLayoutManager(layoutManager);
        recyclerResults.setAdapter(adapter);
        recyclerResults.setVisibility(View.INVISIBLE);
    }

    private void initButtonSaveMyResult(View rootView) {
        Button buttonSaveMyResult = rootView.findViewById(R.id.btnSaveMyResult);
        buttonSaveMyResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResultInDB();
                Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public  void onStart() {
        super.onStart();

        TitleChange listenerTitleChange = (TitleChange) getActivity();
        if (listenerTitleChange != null) {
            listenerTitleChange.changeTitle(
                    R.string.title_MyResults_Fragment,
                    R.string.title_MyResults_Fragment);
        }

        editMyWeightResult.setText(
                (viewModel.getMyWeight().equals("0"))
                        ? ""
                        : viewModel.getMyWeight());


        if (adapter.isEmpty()) {
            adapter.setExercises(viewModel.getResults());
            adapter.notifyDataSetChanged();
            recyclerResults.setVisibility(View.VISIBLE);
        }
    }

    private void saveResultInDB() {
        viewModel.saveResults();
        viewModel.saveWeight();
    }

    public void setExercise(String exercise, String result) {
        viewModel.setExercises(exercise, result);
    }
}