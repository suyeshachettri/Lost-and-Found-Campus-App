package com.example.campuslostfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ViewHolder> {

    Context context;
    List<Claim> list;
    String itemId;
    FirebaseFirestore db;

    public ClaimAdapter(Context context, List<Claim> list, String itemId) {
        this.context = context;
        this.list = list;
        this.itemId = itemId;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.claim_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Claim claim = list.get(position);

        holder.name.setText(claim.name);
        holder.phone.setText(claim.phone);
        holder.status.setText(claim.status);

        holder.btnAccept.setOnClickListener(v -> updateStatus(claim.id, "accepted"));
        holder.btnReject.setOnClickListener(v -> updateStatus(claim.id, "rejected"));
    }

    private void updateStatus(String claimId, String status) {
        db.collection("items")
                .document(itemId)
                .collection("claims")
                .document(claimId)
                .update("status", status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, status;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            phone = itemView.findViewById(R.id.tvPhone);
            status = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}