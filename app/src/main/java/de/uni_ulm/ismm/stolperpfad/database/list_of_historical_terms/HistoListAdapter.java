package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;

public class HistoListAdapter extends RecyclerView.Adapter<HistoListAdapter.HistoViewHolder> {

    private final LayoutInflater mInflater;
    private List<HistoricalTerm> mHistoTerms; // Cached copy of terms
    private Context mContext;
    private OnHistoItemListener mOnHistoItemListener;


    HistoListAdapter(Context context, OnHistoItemListener onHistoItemListener) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOnHistoItemListener = onHistoItemListener;
    }

    @Override
    public HistoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_history, parent, false);
        return new HistoViewHolder(itemView, mOnHistoItemListener);
    }


    @Override
    public void onBindViewHolder(HistoViewHolder holder, int position) {
        if (mHistoTerms != null) {
            HistoricalTerm current = mHistoTerms.get(position);
            holder.textViewHistory.setText(current.getName());
        } else {
            // Covers the case of data not being ready yet.
            holder.textViewHistory.setText("Loading...");
        }
    }

    void setTerms(List<HistoricalTerm> histoTerms){
        mHistoTerms = histoTerms;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mHistoTerms has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mHistoTerms != null)
            return mHistoTerms.size();
        else return 0;
    }

    /**
     * This is the ViewHolder Class
     */
    class HistoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textViewHistory;
        OnHistoItemListener onHistoItemListener;

        private HistoViewHolder(View itemView, OnHistoItemListener onHistoItemListener) {
            super(itemView);
            textViewHistory = itemView.findViewById(R.id.textViewH);
            this.onHistoItemListener = onHistoItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onHistoItemListener.onHistoClick(getAdapterPosition());
        }
    }

    /**
     * interface to detect clicks on the person items
     */
    public interface OnHistoItemListener {
        void onHistoClick(int position);
    }
}