package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

//Sets the recyclerView adapter
public class recviewAdapter extends FirestoreRecyclerAdapter<Model, recviewAdapter.recviewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private OnItemClickListener listener;

    recviewAdapter(FirestoreRecyclerOptions<Model> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull final recviewHolder holder, final int position, @NonNull final Model model) {
        holder.petitionTitle.setText(model.getTitle());
        holder.signatureText.setText(model.getSignature() + " " + "signed");
        View itemView = holder.itemView;

        // When the user clicks one of the cardview: email + Title has to be passed: not yet implemented on PetitionsList

    }


    @NonNull
    @Override
    public recviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new recviewHolder(v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {

        void handleDeleteItem(DocumentSnapshot snapshot);

        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    class recviewHolder extends RecyclerView.ViewHolder {
        TextView petitionTitle, signatureText;

        public recviewHolder(View itemView) {
            super(itemView);
            petitionTitle = itemView.findViewById(R.id.petitionTitle);
            signatureText = itemView.findViewById(R.id.signatureText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }

        public void deleteItem() {
            listener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

}
