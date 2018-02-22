package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.Caption_Wod;
import ru.lizzzi.crossfit_rekord.Listener_Result;
import ru.lizzzi.crossfit_rekord.R;

/**
 * Created by Liza on 24.11.2017.
 */

public class RecyclerAdapter_Result extends RecyclerView.Adapter<RecyclerAdapter_Result.ViewHolder>{

    private ArrayList<Caption_Wod> caption_wod;
    private Listener_Result mlistener;
    private Caption_Wod cw;

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTV_Day;
        private TextView mTV_Month;
        private TextView mTV_Caption;
        private TextView mTV_Level;
        private TextView mTV_Time;


        public ViewHolder(View v){
            super(v);
            mTV_Day = (TextView) v.findViewById(R.id.tv_day);
            mTV_Month = (TextView) v.findViewById(R.id.tv_month);
            mTV_Caption = (TextView) v.findViewById(R.id.tv_caption_wod);
            mTV_Level = (TextView) v.findViewById(R.id.tv_level);
            mTV_Time = (TextView) v.findViewById(R.id.tv_time);


        }
    }

    public RecyclerAdapter_Result(ArrayList<Caption_Wod> caption_wod, Listener_Result listener){
        this.caption_wod = caption_wod;
        mlistener = listener;
    }

    @Override
    public RecyclerAdapter_Result.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lv_result, parent, false);

        RecyclerAdapter_Result.ViewHolder vh = new RecyclerAdapter_Result.ViewHolder(v);
        return vh;
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
}
