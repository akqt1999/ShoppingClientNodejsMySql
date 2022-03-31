package etn.app.danghoc.shoppingclient.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Interface.IOnRecycleViewClickListener;
import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.Model.HistoryMoney;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.R;

public class HistoryMoneyAdapter extends RecyclerView.Adapter<HistoryMoneyAdapter.MyViewHolder>{

    private Context context;
    private List<HistoryMoney> orderList;

    public HistoryMoneyAdapter(Context context, List<HistoryMoney> orderList) {
        this.context = context;
        this.orderList = orderList;
    }



    @NonNull
    @Override
    public HistoryMoneyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryMoneyAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_view_history_money_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryMoneyAdapter.MyViewHolder holder, int position) {
        HistoryMoney historyMoney=orderList.get(position);
        if(historyMoney.getTrangThai()==1){
            holder.txt_money_update.setText("+"+historyMoney.getTien()+"đ");
            holder.txt_money_update.setTextColor(Color.GREEN);
        }else{
            holder.txt_money_update.setText("-"+historyMoney.getTien()+"đ");
            holder.txt_money_update.setTextColor(Color.RED);
        }
        holder.txt_date.setText(Common.simpleDateFormat.format(historyMoney.getDateUpdateMoney()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class
    MyViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.txt_money_update)
        TextView txt_money_update;
        @BindView(R.id.txt_date)
        TextView txt_date;


        Unbinder unbinder;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }


    }
}
