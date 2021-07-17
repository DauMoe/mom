package com.example.mom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mom.Module.Events;
import com.example.mom.R;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.example.mom.DefineVars.listMonth;

public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.ExchangeViewHolder> {
    private Context context;
    private List<Events> data;
    private SimpleDateFormat formatter  = new SimpleDateFormat("kk:mm:ss");
    private Calendar calendar           = Calendar.getInstance();
    String paid_on;

    public ExchangeAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Events> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExchangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exchange_item, parent, false);
        return new ExchangeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeViewHolder holder, int position) {
        Events item = data.get(position);
        if (item == null) return;
        holder.InvoiceID.setText("#"+item.getBillID());
        holder.PaidBy.setText(item.getUniqueID());
        holder.Amount.setText(item.getAmount()+" "+item.getUnit());
        calendar.setTimeInMillis(item.getTime());
        paid_on = listMonth[calendar.get(Calendar.MONTH)] + " "+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR) + " "+formatter.format(Long.valueOf(item.getTime()));
        holder.PaidOn.setText(paid_on);
        if (item.isEarnings()) {
            holder.ExchangeEarning.setTag(R.drawable.ic_up_arrown);
            holder.ExchangeEarning.setImageResource(R.drawable.ic_up_arrown);
        } else {
            holder.ExchangeEarning.setTag(R.drawable.ic_down_arrown);
            holder.ExchangeEarning.setImageResource(R.drawable.ic_down_arrown);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null) return data.size();
        return 0;
    }

    public class ExchangeViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView InvoiceID, PaidBy, PaidOn, Amount;
        private ImageView ExchangeEarning;
        public ExchangeViewHolder(@NonNull View itemView) {
            super(itemView);
            InvoiceID       = itemView.findViewById(R.id.invoice_id);
            PaidBy          = itemView.findViewById(R.id.paid_by);
            PaidOn          = itemView.findViewById(R.id.paid_on);
            Amount          = itemView.findViewById(R.id.paid_amount);
            ExchangeEarning = itemView.findViewById(R.id.exchange_earning);
        }
    }
}
