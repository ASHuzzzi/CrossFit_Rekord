package ru.lizzzi.crossfit_rekord.items;

public class NotificationItem {
    private String codeNote;
    private long date;
    private String header;
    private String text;
    private boolean view;

    public NotificationItem(String codeNote,
                            long date,
                            String header,
                            String text,
                            boolean view) {
        this.codeNote = codeNote;
        this.date = date;
        this.header = header;
        this.text = text;
        this.view = view;
    }

    public String getCodeNote() {
        return codeNote;
    }

    public long getDate() {
        return date;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }

    public boolean isView() {
        return view;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
