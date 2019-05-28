package ru.lizzzi.crossfit_rekord.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;

public class SelectDayFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createDialogView());
        return builder.create();
    }

    private View createDialogView() {
        final LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_select_day, null);

        Button butrtonCanel = view.findViewById(R.id.buttonCancel);
        butrtonCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RadioButton radioButton = view.findViewById(R.id.radioButton);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                DialogFragment newFragment = new SelectWeekDayFragment();
                newFragment.show(getFragmentManager(), "missiles");
            }
        });

        RadioButton radioButton2 = view.findViewById(R.id.radioButton2);
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                DialogFragment newFragment = new SelectTimeFragment();
                newFragment.show(getFragmentManager(), "missiles");
            }
        });

        RadioButton radioButton3 = view.findViewById(R.id.radioButton3);
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                DialogFragment newFragment = new SelectWeekDayFragment();
                newFragment.show(getFragmentManager(), "missiles");
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
    }
}



