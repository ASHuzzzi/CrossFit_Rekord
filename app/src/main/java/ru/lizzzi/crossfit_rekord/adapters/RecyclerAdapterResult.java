package ru.lizzzi.crossfit_rekord.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.lizzzi.crossfit_rekord.draft.wod_result_fragment.Caption_Wod;
import ru.lizzzi.crossfit_rekord.fragments.ListenerResult;
import ru.lizzzi.crossfit_rekord.R;


public class RecyclerAdapterResult extends RecyclerView.Adapter<RecyclerAdapterResult.ViewHolder>{

    private ArrayList<Caption_Wod> arCaptionWod;


    public RecyclerAdapterResult(ArrayList<Caption_Wod> captionWod, ListenerResult listener){
        this.arCaptionWod = captionWod;
        ListenerResult mlistener = listener;
    }

    @Override
    public RecyclerAdapterResult.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lv_result, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterResult.ViewHolder holder, int position) {
        holder.tvDay.setText(arCaptionWod.get(position).day);
        holder.tvMonth.setText(arCaptionWod.get(position).month);
        holder.tvCaption.setText(arCaptionWod.get(position).caption_wod);
        holder.tvLevel.setText(arCaptionWod.get(position).level);
        holder.tvTime.setText(arCaptionWod.get(position).result);
    }

    @Override
    public int getItemCount() {
        return arCaptionWod.size();
    }

    static class  ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDay;
        private TextView tvMonth;
        private TextView tvCaption;
        private TextView tvLevel;
        private TextView tvTime;


        ViewHolder(View v){
            super(v);
            tvDay = v.findViewById(R.id.tv_day);
            tvMonth = v.findViewById(R.id.tv_month);
            tvCaption = v.findViewById(R.id.tv_caption_wod);
            tvLevel = v.findViewById(R.id.tv_level);
            tvTime = v.findViewById(R.id.tv_time);


        }
    }
}
