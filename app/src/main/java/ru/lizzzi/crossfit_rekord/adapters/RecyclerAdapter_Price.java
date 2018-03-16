package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.documentfields.DocumentFields_Price;


public class RecyclerAdapter_Price extends BaseAdapter {
    private List<Map> storedItems;
    private int layoutId;
    private LayoutInflater inflater;
    private DocumentFields_Price fields;

    public RecyclerAdapter_Price(Context context, @NonNull List<Map> storedItems, int layoutId) {
        this.storedItems = storedItems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields = new DocumentFields_Price(context, null);
    }

    @Override
    public int getCount() {
        return storedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return storedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        customizeView(view, holder, storedItems.get(position));

        return view;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    private void customizeView(View view, ViewHolder holder, final Map documentInfo) {

        String period = (String) documentInfo.get(fields.getPeriodField());
        String typesoftraining = (String) documentInfo.get(fields.getTypesOfTrainingFields());
        Double price = (Double) documentInfo.get(fields.getPriceField());

        holder.PeriodItem.setText(period);
        holder.TypesOfTrainingItem.setText(typesoftraining);
        holder.PriceItem.setText(String.valueOf(price));


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    static class ViewHolder {
        private TextView PeriodItem;
        private TextView TypesOfTrainingItem;
        private TextView PriceItem;

        ViewHolder(View view) {
            PeriodItem = view.findViewById(R.id.period);
            TypesOfTrainingItem = view.findViewById(R.id.types_of_training);
            PriceItem = view.findViewById(R.id.price);
        }
    }
}