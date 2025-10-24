package com.example.fryserapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fryserapp.data.FreezerItem;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FreezerAdapter extends ListAdapter<FreezerItem, FreezerAdapter.VH> {
    interface OnItemClick { void onClick(FreezerItem item); }
    private final OnItemClick click;
    public FreezerAdapter(OnItemClick click) { super(DIFF); this.click = click; }

    public static final DiffUtil.ItemCallback<FreezerItem> DIFF = new DiffUtil.ItemCallback<FreezerItem>() {
        @Override public boolean areItemsTheSame(@NonNull FreezerItem o, @NonNull FreezerItem n) { return o.id==n.id; }
        @Override public boolean areContentsTheSame(@NonNull FreezerItem o, @NonNull FreezerItem n) { 
            return o.id==n.id &&
                   str(o.name).equals(str(n.name)) &&
                   str(o.quantity).equals(str(n.quantity)) &&
                   str(o.addedDate).equals(str(n.addedDate)) &&
                   str(o.expiryDate).equals(str(n.expiryDate)) &&
                   o.drawer==n.drawer;
        }
        private String str(Object o) { return o==null?"":o.toString(); }
    };

    static class VH extends RecyclerView.ViewHolder {
        TextView name, details;
        VH(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.name);
            details = v.findViewById(R.id.details);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        FreezerItem it = getItem(pos);
        h.name.setText(it.name);
        String d = "Skuffe " + it.drawer + " • " + getDate(it.addedDate) + (it.quantity!=null && !it.quantity.isEmpty()? " • " + it.quantity : "");
        h.details.setText(d);
        h.itemView.setOnClickListener(v -> click.onClick(it));
    }

    private String getDate(long dateLong) {
        Date date = new Date(dateLong);
        Format format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(date);
    }


}
