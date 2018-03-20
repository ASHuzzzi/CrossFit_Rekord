package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.draft.wod_result_fragment.Caption_Wod;
import ru.lizzzi.crossfit_rekord.fragments.Listener_Result;
import ru.lizzzi.crossfit_rekord.R;


public class RecyclerAdapter_Result extends RecyclerView.Adapter<RecyclerAdapter_Result.ViewHolder>{

    private ArrayList<Caption_Wod> caption_wod;


    public RecyclerAdapter_Result(ArrayList<Caption_Wod> caption_wod, Listener_Result listener){
        this.caption_wod = caption_wod;
        Listener_Result mlistener = listener;
    }

    @Override
    public RecyclerAdapter_Result.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lv_result, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter_Result.ViewHolder holder, int position) {
        holder.mTV_Day.setText(caption_wod.get(position).day);
        holder.mTV_Month.setText(caption_wod.get(position).month);
        holder.mTV_Caption.setText(caption_wod.get(position).caption_wod);
        holder.mTV_Level.setText(caption_wod.get(position).level);
        holder.mTV_Time.setText(caption_wod.get(position).result);
    }

    @Override
    public int getItemCount() {
        return caption_wod.size();
    }

    static class  ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTV_Day;
        private TextView mTV_Month;
        private TextView mTV_Caption;
        private TextView mTV_Level;
        private TextView mTV_Time;


        ViewHolder(View v){
            super(v);
            mTV_Day = v.findViewById(R.id.tv_day);
            mTV_Month = v.findViewById(R.id.tv_month);
            mTV_Caption = v.findViewById(R.id.tv_caption_wod);
            mTV_Level = v.findViewById(R.id.tv_level);
            mTV_Time = v.findViewById(R.id.tv_time);


        }
    }
}
