package etn.app.danghoc.shoppingclient.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.UpdateStatusOrder;
import etn.app.danghoc.shoppingclient.Interface.IOnRecycleViewClickListener;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.R;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.MyViewHolder> {

    private Context context;
    private List<Order> orderList;

    public ViewOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        holder.txt_name_product.setText(new StringBuilder(orderList.get(position).getTenSP()));
        holder.txt_price.setText(new StringBuilder(Common.formatPrice(orderList.get(position).getGia())));
        holder.txt_status.setText(Common.ConvertOrderStatusToStringBuyer(orderList.get(position).getTrangThai()));
        Glide.with(context).load(orderList.get(position).getHinh()).into(holder.img_order);

        holder.setOnRecycleViewClickListener((view, position1) -> {
            Toast.makeText(context, orderList.get(position).getTenSP(), Toast.LENGTH_SHORT).show();
        });

        holder.btn_cancel.setOnClickListener(view -> {

            if(orderList.get(position).getTrangThai()==-2||orderList.get(position).getTrangThai()==2)
            {
                Toast.makeText(context, "đơn hàng đã được hủy rồi không thể hủy nữa", Toast.LENGTH_SHORT).show();
                return;
            }

            if(orderList.get(position).getTrangThai()==1)
            {
                Toast.makeText(context, "đơn hàng đã được xác nhận rồi,không thể hủy", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setMessage("bạn có muốn hủy đơn hàng này không");
            builder.setNegativeButton("yes", (dialogInterface, i) -> {
                EventBus.getDefault().postSticky(new UpdateStatusOrder(2,position,orderList.get(position).getIdSeller()));
            });
            builder.setPositiveButton("no", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

            Dialog dialog=builder.create();
            dialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class
    MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_name_product)
        TextView txt_name_product;
        @BindView(R.id.txt_status)
        TextView txt_status;
        @BindView(R.id.txt_price)
        TextView txt_price;
        @BindView(R.id.btn_cancel)
        ImageButton btn_cancel;
        @BindView(R.id.img_order)
        ImageView img_order;

        IOnRecycleViewClickListener onRecycleViewClickListener;

        public void setOnRecycleViewClickListener(IOnRecycleViewClickListener onRecycleViewClickListener) {
            this.onRecycleViewClickListener = onRecycleViewClickListener;
        }

        Unbinder unbinder;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecycleViewClickListener.onClick(v, getAdapterPosition());
        }
    }
}
