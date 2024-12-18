package com.eipna.notable.constants;

import android.content.Context;

import com.eipna.notable.R;
import com.eipna.notable.models.NoteModel;
import com.eipna.notable.utils.SharedPrefsUtil;

import java.util.Comparator;

public enum NoteSort {
    TITLE_ASCENDING(1),
    TITLE_DESCENDING(2),
    DATE_CREATED_ASCENDING(3),
    DATE_CREATED_DESCENDING(4),
    LAST_UPDATED_ASCENDING(5),
    LAST_UPDATED_DESCENDING(6);

    private final int value;

    NoteSort(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Comparator<NoteModel> getComparator(Context context) {
        int defSorting = new SharedPrefsUtil(context).getInt("prefs_note_sort", LAST_UPDATED_ASCENDING.getValue());
        if (defSorting == TITLE_ASCENDING.getValue()) {
            return NoteModel.SORT_TITLE_ASCENDING;
        } else if (defSorting == TITLE_DESCENDING.getValue()) {
            return NoteModel.SORT_TITLE_DESCENDING;
        } else if (defSorting == DATE_CREATED_ASCENDING.getValue()) {
            return NoteModel.SORT_DATE_CREATED_ASCENDING;
        } else if (defSorting == DATE_CREATED_DESCENDING.getValue()) {
            return NoteModel.SORT_DATE_CREATED_DESCENDING;
        } else if (defSorting == LAST_UPDATED_ASCENDING.getValue()) {
            return NoteModel.SORT_LAST_UPDATED_ASCENDING;
        } else if (defSorting == LAST_UPDATED_DESCENDING.getValue()) {
            return NoteModel.SORT_LAST_UPDATED_DESCENDING;
        }
        return null;
    }

    public static int getMenuItem(Context context) {
        int prefsNoteSorting = new SharedPrefsUtil(context).getInt("prefs_note_sort", LAST_UPDATED_ASCENDING.getValue());
        if (prefsNoteSorting == TITLE_ASCENDING.getValue()) {
            return R.id.sort_title_asc;
        } else if (prefsNoteSorting == TITLE_DESCENDING.getValue()) {
            return R.id.sort_title_desc;
        } else if (prefsNoteSorting == DATE_CREATED_ASCENDING.getValue()) {
            return R.id.sort_date_created_asc;
        } else if (prefsNoteSorting == DATE_CREATED_DESCENDING.getValue()) {
            return R.id.sort_date_created_desc;
        } else if (prefsNoteSorting == LAST_UPDATED_ASCENDING.getValue()) {
            return R.id.sort_last_updated_asc;
        } else if (prefsNoteSorting == LAST_UPDATED_DESCENDING.getValue()) {
            return R.id.sort_last_updated_desc;
        }
        return -1;
    }
}