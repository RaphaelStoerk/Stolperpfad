package de.uni_ulm.ismm.stolperpfad.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;

public class PersListAdapter extends RecyclerView.Adapter<PersListAdapter.PersViewHolder> implements View.OnClickListener{

    private final LayoutInflater mInflater;
    private List<Person> mPersons; //cached copy of persons


    class PersViewHolder extends RecyclerView.ViewHolder {
        private final TextView persItemView;
        //for clickable items
        public TextView txtDescript;
        private OnItemClickListener itemClickListener;

        private PersViewHolder(View itemView) {
            super(itemView);
            persItemView = itemView.findViewById(R.id.textView);
            txtDescript = (TextView) itemView.findViewById(R.id.txtDescript);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(OnItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.setItemClickListener.onClick
        }

    }



    PersListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public PersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new PersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PersViewHolder holder, int position) {
        if (mPersons != null) {
            Person current = mPersons.get(position);
            holder.persItemView.setText(current.getFstName() + " " + current.getFamName());
        } else {
            // if the data is not ready yet
            holder.persItemView.setText("No persons yet");
        }

    }

    void setPersons(List<Person> persons) {
        this.mPersons = persons;
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPersons != null)
            return mPersons.size();
        else return 0;
    }



}
