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
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.fragment_select_day, null);

        Button butrtonCanel = contentView.findViewById(R.id.buttonCancel);
        butrtonCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().setCanceledOnTouchOutside(false);
    }
}



