package ru.lizzzi.crossfit_rekord.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFieldsTable;
import ru.lizzzi.crossfit_rekord.inspectionСlasses.TakeBackgroungResourceForAdapter;
import ru.lizzzi.crossfit_rekord.interfaces.ListenerRecordForTrainingSelect;

public class RecyclerAdapterRecordForTrainingSelect extends RecyclerView.Adapter<RecyclerAdapterRecordForTrainingSelect.ViewHolder> {

    private List<Map> shediletems;
    private final ThreadLocal<DocumentFieldsTable> fields = new ThreadLocal<>();
    private ListenerRecordForTrainingSelect mlistener;
    private boolean flagTodayOrNot;

    public RecyclerAdapterRecordForTrainingSelect(Context context, @NonNull List<Map> shediletems,
                                                  boolean flag, ListenerRecordForTrainingSelect listener) {
        this.shediletems = shediletems;
        fields.set(new DocumentFieldsTable(context));
        flagTodayOrNot = flag;
        mlistener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView startTimeItem;
        private TextView typesItem;
        private LinearLayout llItemTable;

        ViewHolder(View view) {
            super(view);
            startTimeItem = view.findViewById(R.id.start_time);
            typesItem = view.findViewById(R.id.type);
            llItemTable = view.findViewById(R.id.ll_item_table);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_table, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map documentInfo = shediletems.get(position);
        final String start_time = (String) documentInfo.get(fields.get().getStartTimeField());
        final String type = (String) documentInfo.get(fields.get().getTypeField());


        holder.startTimeItem.setText(start_time);
        holder.typesItem.setText(type);
        LinearLayout ll_item_table = holder.llItemTable;

        if (flagTodayOrNot){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCheckTime = new SimpleDateFormat("HH:mm");
            GregorianCalendar calendarday = new GregorianCalendar();
            Date today = calendarday.getTime();
            String stTimeNow = sdfCheckTime.format(today);
            try {
                Date dTimeNow = sdfCheckTime.parse(stTimeNow);
                Date dSelectTime = sdfCheckTime.parse(start_time);
                if (dSelectTime.getTime() > dTimeNow.getTime()){ //проверяем чтобы выбранное время было позже чем сейчас

                    TakeBackgroungResourceForAdapter takeBackgroungResourceForAdapter = new TakeBackgroungResourceForAdapter();
                    int iResource = takeBackgroungResourceForAdapter.takeBackgroungResourceForAdapter(type);
                    holder.typesItem.setBackgroundResource(iResource);

                    ll_item_table.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mlistener.selectTime(start_time, type);
                        }
                    });
                }else{
                    holder.typesItem.setBackgroundResource(R.drawable.table_item_out_of_time);
                    holder.startTimeItem.setTextColor(Color.BLACK);
                    ll_item_table.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mlistener.selectTime("outTime",
                                    "outTime");
                        }
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {

            TakeBackgroungResourceForAdapter takeBackgroungResourceForAdapter = new TakeBackgroungResourceForAdapter();
            int iResource = takeBackgroungResourceForAdapter.takeBackgroungResourceForAdapter(type);
            holder.typesItem.setBackgroundResource(iResource);

            ll_item_table.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mlistener.selectTime(start_time, type);
                }
            });
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return shediletems.size();
    }
}
