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

import java.util.Objects;

import ru.lizzzi.crossfit_rekord.R;
import ru.lizzzi.crossfit_rekord.interfaces.InterfaceChangeTitle;

public class ContactsFragment extends Fragment {

    private ImageButton imButtonInstagram;
    private ImageButton imButtonVk;
    private TextView textAppVersion;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        textAppVersion = view.findViewById(R.id.textView41);
        imButtonInstagram = view.findViewById(R.id.ibInstagram);
        imButtonInstagram.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                PackageManager packageManager = getContext().getPackageManager();
                if (isPackageInstalled("com.instagram.android", packageManager)) {
                    intent.setPackage("com.instagram.android");
                } else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                }
                intent.setData(Uri.parse(getResources().getString(R.string.Instagram)));
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(
                        R.anim.pull_in_right,
                        R.anim.push_out_left);
            }
        });

        imButtonVk = view.findViewById(R.id.ibVK);
        imButtonVk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                PackageManager packageManager = getContext().getPackageManager();
                if (isPackageInstalled("com.vkontakte.android", packageManager)) {
                    intent.setPackage("com.vkontakte.android");
                } else {
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                }
                intent.setData(Uri.parse(getResources().getString(R.string.groupVK)));
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(
                        R.anim.pull_in_right,
                        R.anim.push_out_left);

            }
        });
        return view;
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

        imButtonInstagram.getLayoutParams().height = reSizeScreenHeight;
        imButtonInstagram.getLayoutParams().width = reSizeScreenHeight;
        imButtonVk.getLayoutParams().height = reSizeScreenHeight;
        imButtonVk.getLayoutParams().width = reSizeScreenHeight;

        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String appVersion = getResources().getString(R.string.appVersion) + " " + packageInfo.versionName;
            textAppVersion.setText(appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
