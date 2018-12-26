package ru.lizzzi.crossfit_rekord.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;

public class ContactsFragment extends Fragment {

    private ImageButton ibInstagram;
    private ImageButton ibVk;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = getResources().getString(R.string.appVersion) + " " + pInfo.versionName;
            TextView tvAppVersion = v.findViewById(R.id.textView41);
            tvAppVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ibInstagram = v.findViewById(R.id.ibInstagram);
        ibInstagram.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);

                PackageManager pm = getContext().getPackageManager();
                if (isPackageInstalled("com.instagram.android", pm)){
                    intent.setPackage("com.instagram.android");
                }else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                }

                intent.setData(Uri.parse(getResources().getString(R.string.Instagram)));
                startActivity(intent);

            }
        });

        ibVk = v.findViewById(R.id.ibVK);
        ibVk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);

                PackageManager pm = getContext().getPackageManager();
                if (isPackageInstalled("com.vkontakte.android", pm)){
                    intent.setPackage("com.vkontakte.android");
                }else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                }

                intent.setData(Uri.parse(getResources().getString(R.string.groupVK)));
                startActivity(intent);

            }
        });

        return v;
    }

    @Override
    public  void onStart() {
        super.onStart();
        if (getActivity() instanceof InterfaceChangeTitle){
            InterfaceChangeTitle listernerChangeTitle = (InterfaceChangeTitle) getActivity();
            listernerChangeTitle.changeTitle(R.string.title_Contacts_Fragment, R.string.title_Contacts_Fragment);
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int reSizeScreenHeight = screenHeight / 10;

        ibInstagram.getLayoutParams().height = reSizeScreenHeight;
        ibInstagram.getLayoutParams().width = reSizeScreenHeight;
        ibVk.getLayoutParams().height = reSizeScreenHeight;
        ibVk.getLayoutParams().width = reSizeScreenHeight;

    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
