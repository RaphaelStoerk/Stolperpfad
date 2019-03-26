package de.uni_ulm.ismm.stolperpfad.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;

public class PersListAdapter extends RecyclerView.Adapter<PersListAdapter.PersViewHolder> {

    private final LayoutInflater mInflater;
    private List<Person> mPersList; //cached copy of persons
    private Context mContext;
    private OnPersItemListener mOnPersItemListener;


    PersListAdapter(Context context, OnPersItemListener onPersItemListener) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOnPersItemListener = onPersItemListener;

    }

    @Override
    public PersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new PersViewHolder(itemView, mOnPersItemListener);
    }

    @Override
    public void onBindViewHolder(PersViewHolder holder, int position) {
        if (mPersList != null) {
            Person current = mPersList.get(position);
            holder.persItemView.setText(current.getFstName() + " " + current.getFamName());

        } else {
            // if the data is not ready yet
            holder.persItemView.setText("Loading...");
        }

    }

    void setPersons(List<Person> persons) {
        this.mPersList = persons;
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPersList != null)
            return mPersList.size();
        else return 0;
    }


    /**
     * This is the ViewHolder Class
     */
    class PersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView persItemView;
        OnPersItemListener onPersItemListener;

        private PersViewHolder(View itemView, OnPersItemListener onPersItemListener) {
            super(itemView);
            persItemView = itemView.findViewById(R.id.textView);
            this.onPersItemListener = onPersItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPersItemListener.onPersClick(getAdapterPosition());
        }
    }

    /**
     * interface to detect clicks on the person items
     */
    public interface OnPersItemListener {
        void onPersClick(int position);
    }

}
