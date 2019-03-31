package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

public class PersListAdapter extends RecyclerView.Adapter<PersListAdapter.PersViewHolder> {

    private final LayoutInflater mInflater;
    private List<Person> mPersList; //cached copy of persons
    private List<Stolperstein> mStoneList;
    private Context mContext;
    private OnPersItemListener mOnPersItemListener;


    PersListAdapter(Context context, OnPersItemListener onPersItemListener) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOnPersItemListener = onPersItemListener;

    }

    @Override
    public PersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_persons, parent, false);
        return new PersViewHolder(itemView, mOnPersItemListener);
    }

    //TODO: fix this, probably we have to
    @Override
    public void onBindViewHolder(PersViewHolder holder, int persPosition) {
        if (mPersList != null) {
            Person current = mPersList.get(persPosition);
            holder.textViewPersons.setText(current.getFstName() + " " + current.getFamName() + "\n"
                    + PersRoomDatabase.getDatabase(mContext).getAddress(current.getStolperstein()));
        } else {
            // if the data is not ready yet
            holder.textViewPersons.setText("Loading...");
        }

    }

    void setPersons(List<Person> persons) {
        this.mPersList = persons;
    }

    // TODO: check if we need to do the same with the StoneList
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
        private final TextView textViewPersons;
        OnPersItemListener onPersItemListener;

        private PersViewHolder(View itemView, OnPersItemListener onPersItemListener) {
            super(itemView);
            textViewPersons = itemView.findViewById(R.id.textViewP);
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
