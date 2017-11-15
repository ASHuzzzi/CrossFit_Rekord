package ru.lizzzi.crossfit_rekord.data;

import android.provider.BaseColumns;

/**
 * Created by Liza on 02.11.2017.
 */

public class DefinitionDbContarct {
    private DefinitionDbContarct(){

    }

    public static final class DBdefinition implements BaseColumns{

        public final static String TABLE_NAME = "definition";

        public final static String Column_termin = "termin";
        public final static String Column_description = "description";
        public final static String Column_character = "character";
        public final static String Column_attribute = "attribute";

    }
}
