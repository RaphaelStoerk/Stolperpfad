package de.uni_ulm.ismm.stolperpfad.database;

import android.view.View;

import de.uni_ulm.ismm.stolperpfad.database.Person;

/*
we need this interface because a recyclerView doesn't have an 'OnClickListener'
 */
public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
