package ru.lizzzi.crossfit_rekord.documentfields;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.lizzzi.crossfit_rekord.R;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

//import static prof_itgroup.ru.storehouseapp.Helpers.Helper.getColorsListFrom;

/**
 * Created by Peter Staranchuk on 10/30/16
 */

public class DocumentFields_Price {
    private Context context;
    private DocumentInfo documentInfo;

    public DocumentFields_Price(Context context) {
        this(context, null);
    }

    public DocumentFields_Price(Context context, DocumentInfo documentInfo) {
        this.context = context;
        this.documentInfo = documentInfo;
    }

    public void setDocumentInfo(DocumentInfo documentInfo) {
        this.documentInfo = documentInfo;
    }

    public enum Fields {

        period(R.string.period),
        types_of_training(R.string.types_of_training),
        price(R.string.price);

        /*
        PLATFORM(R.string.fieldPlatformName),
        CAMERA_INFO(R.string.fieldCameraInfo),
        COLORS_AVAILABLE(R.string.fieldColorsAvailable),
        DEVICE_PRICE(R.string.fieldDevicePrice),
        LAST_SEND(R.string.fieldLastSend),
        BUYERS(R.string.fieldBuyers),
        SEND_INFO(R.string.fieldSendInfo);*/

        private int fieldNameId;

        Fields(int fieldNameId) {
            this.fieldNameId = fieldNameId;
        }

        public int getNameId() {
            return fieldNameId;
        }

    }
    public String getTypesOfTrainingFields() {
        return context.getString(Fields.types_of_training.getNameId());
    }


    public String getPeriodField() {
        return context.getString(Fields.period.getNameId());

    }

    public String getPriceField() {
        return context.getString(Fields.price.getNameId());
    }
/*
    public String getCameraInfoField() {
        return context.getString(Fields.CAMERA_INFO.getNameId());
    }

    public String getColorsAvailableField() {
        return context.getString(Fields.COLORS_AVAILABLE.getNameId());
    }

    public String getDevicePriceField() {
        return context.getString(Fields.DEVICE_PRICE.getNameId());
    }

    public String getLastSendField() {
        return context.getString(Fields.LAST_SEND.getNameId());
    }

    public String getBuyersField() {
        return context.getString(Fields.BUYERS.getNameId());
    }

    public String getSendInfoField() {
        return context.getString(Fields.SEND_INFO.getNameId());
    }*/

    private String getFieldValue(DocumentInfo documentInfo, Fields field) {
        return String.valueOf(documentInfo.getFields().get(context.getString(field.getNameId())));
    }

    @NonNull
    public String getTypesOfTraining() {
        if(documentInfo != null) {
            return getFieldValue(documentInfo, Fields.types_of_training);
        }
        return "";
    }

    @NonNull
    public String getPeriod() {
        if(documentInfo != null) {
            return getFieldValue(documentInfo, Fields.period);
        }
        return "";
    }

    @NonNull
    public String getPrice() {
        if(documentInfo != null) {
            return getFieldValue(documentInfo, Fields.price);
        }
        return "";
    }
/*
    @NonNull
    public String getColors() {
        if(documentInfo != null) {
            return getFieldValue(documentInfo, Fields.COLORS_AVAILABLE)
                    .replace("[", "")
                    .replace("]", "");
        }
        return "";
    }

    @NonNull
    public Double getDevicePrice() {
        if(documentInfo != null) {
            return Double.valueOf(getFieldValue(documentInfo, Fields.DEVICE_PRICE));
        }
        return new Double(0);
    }

    @NonNull
    public String getLastSendTime() {
        String date = getFieldValue(documentInfo, Fields.LAST_SEND);
        if (date != null && !date.isEmpty() && !date.equals("null")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            return formatter.format(Date.parse(date));
        }

        return "";
    }

    @NonNull
    public String getBuyers() {
        if (documentInfo != null) {
            String buyers = getFieldValue(documentInfo, Fields.BUYERS);
            if(buyers != null) {
                buyers = buyers.replace("[", "").replace("]", "");
                if(buyers.isEmpty() || buyers.equals("null")) {
                    return "";
                } else {
                    return buyers;
                }
            }
        }
        return "";
    }

    @NonNull
    public List<String> getColorsAsList() {
        return getColorsListFrom(getColors());
    }*/
}
