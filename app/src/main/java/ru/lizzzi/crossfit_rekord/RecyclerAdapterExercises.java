package ru.lizzzi.crossfit_rekord;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Liza on 22.11.2017.
 */

public class RecyclerAdapterExercises extends RecyclerView.Adapter<RecyclerAdapterExercises.ViewHolder>{

    private final ArrayList<Exercises> exercises;
    private ListenerActivity mlistener;
    private Exercises e;

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        private EditText mEditText;
        private EditText mEditText2;
        private EditText mEditText3;
        private Button mButton;
        private EditText autoCompleteTextView;


        public ViewHolder(View v){
            super(v);
            mEditText = (EditText) v.findViewById(R.id.editText);
            mEditText2 = (EditText) v.findViewById(R.id.editText2);
            mEditText3 = (EditText) v.findViewById(R.id.editText3);
            mButton = (Button) v.findViewById(R.id.button3);

        }
    }

    public RecyclerAdapterExercises (ArrayList<Exercises> exercises, ListenerActivity listener){
        this.exercises = exercises;
        mlistener = listener;
    }

    public RecyclerAdapterExercises.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lv_new_record_f2, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
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
}
