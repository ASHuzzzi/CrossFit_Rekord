package ru.lizzzi.crossfit_rekord.items;

public class WodItem {
    private long dateSession;
    private String postWorkout;
    private String rx;
    private String rxPlus;
    private String sc;
    private String skill;
    private String warmUp;
    private String wod;

    public WodItem(long dateSession,
                   String postWorkout,
                   String rx,
                   String rxPlus,
                   String sc,
                   String skill,
                   String warmUp,
                   String wod) {
        this.postWorkout = postWorkout;
        this.rx = rx;
        this.rxPlus = rxPlus;
        this.sc = sc;
        this.skill = skill;
        this.warmUp = warmUp;
        this.wod = wod;
    }

    public long getDateSession() {
        return dateSession;
    }

    public String getPostWorkout() {
        return postWorkout;
    }

    public String getRx() {
        return rx;
    }

    public String getRxPlus() {
        return rxPlus;
    }

    public String getSc() {
        return sc;
    }

    public String getSkill() {
        return skill;
    }

    public String getWarmUp() {
        return warmUp;
    }

    public String getWod() {
        return wod;
    }

    public boolean isEmpty() {
        return (postWorkout.isEmpty() &&
                rx.isEmpty() &&
                rxPlus.isEmpty() &&
                sc.isEmpty() &&
                skill.isEmpty() &&
                warmUp.isEmpty() &&
                wod.isEmpty());
    }
}
