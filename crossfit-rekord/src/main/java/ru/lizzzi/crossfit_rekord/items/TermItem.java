package ru.lizzzi.crossfit_rekord.items;

public class TermItem {
    private String term;
    private String definition;

    public TermItem(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }
}
