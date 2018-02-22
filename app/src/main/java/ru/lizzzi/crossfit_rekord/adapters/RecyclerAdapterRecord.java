package ru.lizzzi.crossfit_rekord.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.lizzzi.crossfit_rekord.DocumentFields_Record;
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

public class RecyclerAdapterRecord extends BaseAdapter {
    private Context context;
    private List<DocumentInfo> storedItems;
    private int layoutId;
    private LayoutInflater inflater;
    private DocumentFields_Record fields;

    public static final String APP_PREFERENCES = "audata";
    public static final String APP_PREFERENCES_USERCOUNT = "Usercount";
    SharedPreferences mSettings;

    public RecyclerAdapterRecord(Context context, @NonNull List<DocumentInfo> storedItems, int layoutId) {
        this.context = context;
        this.storedItems = storedItems;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
        fields = new DocumentFields_Record(context, null);
        mSettings = context.getSharedPreferences(APP_PREFERENCES, context.MODE_PRIVATE);
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

        String number = mSettings.getString(APP_PREFERENCES_USERCOUNT, "");

        int i = Integer.parseInt(mSettings.getString(APP_PREFERENCES_USERCOUNT, ""));
        i = i + 1;
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_USERCOUNT, String.valueOf(i));
        editor.apply();

        String username = (String) documentInfo.getFields().get(fields.getUsernameFields());


        holder.UsernameItem.setText(username);
        holder.UserCountItem.setText(number);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    static class ViewHolder {
        private TextView UsernameItem;
        private TextView UserCountItem;

        //@BindView(R.id.label) TextView tvStoredItemName;;

        public ViewHolder(View view) {
            UsernameItem = (TextView) view.findViewById(R.id.username);
            UserCountItem = (TextView) view.findViewById(R.id.number);

        }
    }
}