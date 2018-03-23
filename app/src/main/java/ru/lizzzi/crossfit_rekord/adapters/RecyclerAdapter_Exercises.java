package ru.lizzzi.crossfit_rekord.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.draft.wod_result_fragment.Exercises;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerActivity;
import ru.lizzzi.crossfit_rekord.R;


public class RecyclerAdapter_Exercises extends RecyclerView.Adapter<RecyclerAdapter_Exercises.ViewHolder>{

    private final ArrayList<Exercises> exercises;
    private ListenerActivity mlistener;
    private Exercises e;


    public RecyclerAdapter_Exercises(ArrayList<Exercises> exercises, ListenerActivity listener){
        this.exercises = exercises;
        mlistener = listener;
    }

    public RecyclerAdapter_Exercises.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lv_new_record_f2, parent, false);

        return new ViewHolder(v);
    }

    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.mEditText.setText(exercises.get(position).quantity);
        holder.mEditText2.setText(exercises.get(position).exercise);
        holder.mEditText3.setText(exercises.get(position).weight);

        holder.mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                e = exercises.get(position);
                if (mlistener!=null){
                    mlistener.Remove(e.exercise, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class  ViewHolder extends RecyclerView.ViewHolder {
        private EditText mEditText;
        private EditText mEditText2;
        private EditText mEditText3;
        private Button mButton;


        ViewHolder(View v){
            super(v);
            mEditText = v.findViewById(R.id.editText);
            mEditText2 = v.findViewById(R.id.editText2);
            mEditText3 = v.findViewById(R.id.editText3);
            mButton = v.findViewById(R.id.button3);

        }
    }
}
