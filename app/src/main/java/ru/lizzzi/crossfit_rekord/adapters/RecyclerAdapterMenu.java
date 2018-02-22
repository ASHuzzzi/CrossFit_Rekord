package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.lizzzi.crossfit_rekord.DocumentFields;
import ru.lizzzi.crossfit_rekord.R;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

/*public class RecyclerAdapterMenu extends RecyclerView.Adapter<RecyclerAdapterMenu.ViewHolder> {
//public class RecyclerAdapterMenu extends RecyclerView.Adapter<RecyclerAdapterMenu.ViewHolder> { //implements FastScrollRecyclerView.SectionedAdapter {

    private Context context;
    private List<DocumentInfo> mDataset;
    private DocumentFields fields;
    private ArrayList<String> mPriceset;

    @NonNull
    @Override
    public long getSectionName(int position) {
        return (position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private TextView mPrice;
        private ImageView mImageView;


        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.label);
            mImageView = (ImageView) v.findViewById(R.id.icon);
            mPrice = (TextView) v.findViewById(R.id.price);
        }
    }

    public RecyclerAdapterMenu(Context context, @NonNull List<DocumentInfo> subscriptionItems) {

        this.context = context;
        mDataset = subscriptionItems;
        fields = new DocumentFields(context, null);
        //mPriceset = priceset;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public RecyclerAdapterMenu.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, final DocumentInfo documentInfo) {
        String nname = (String) documentInfo.getFields().get(fields.getDeviceName());

        holder.mTextView.setText(nname);
        /*holder.mPrice.setText(mPriceset.get(position));
        String s = mDataset.get(position);*/
//if (s.startsWith("Капкейк")) {
//holder.mImageView.setImageResource(R.drawable.cupcake_big);
/*
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    */

public class RecyclerAdapterMenu extends BaseAdapter {
    private Context context;
    private List<DocumentInfo> storedItems;
    private int layoutId;
    private LayoutInflater inflater;
    private DocumentFields fields;

    public RecyclerAdapterMenu(Context context, @NonNull List<DocumentInfo> storedItems, int layoutId) {
        this.context = context;
        this.storedItems = storedItems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields = new DocumentFields(context, null);
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

    private void customizeView(View view, ViewHolder holder, final DocumentInfo documentInfo) {

        String period = (String) documentInfo.getFields().get(fields.getPeriodField());
        String typesoftraining = (String) documentInfo.getFields().get(fields.getTypesOfTrainingFields());
        Double price = (Double) documentInfo.getFields().get(fields.getPriceField());

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
        //@BindView(R.id.label) TextView tvStoredItemName;;

        public ViewHolder(View view) {
            PeriodItem = (TextView) view.findViewById(R.id.period);
            TypesOfTrainingItem = (TextView) view.findViewById(R.id.types_of_training);
            PriceItem = (TextView) view.findViewById(R.id.price);
        }
    }
}