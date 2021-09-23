package etn.app.danghoc.shoppingclient.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;

import etn.app.danghoc.shoppingclient.EventBus.UpdateStatusOrder;
import etn.app.danghoc.shoppingclient.Interface.IOnRecycleViewClickListener;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.R;

public class ViewOrderSellerAdapter extends RecyclerView.Adapter<ViewOrderSellerAdapter.MyViewHolder>  {

    private Context context;
    private List<Order> orderList;

    public ViewOrderSellerAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewOrderSellerAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewOrderSellerAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_seller_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewOrderSellerAdapter.MyViewHolder holder, int position) {

        holder.txt_name_product.setText(new StringBuilder(orderList.get(position).getTenSP()));
        holder.txt_price.setText(new StringBuilder(Common.formatPrice(orderList.get(position).getGia())));
        holder.txt_status.setText(Common.ConvertOrderStatusToStringSeller(orderList.get(position).getTrangThai()));
        Glide.with(context).load(orderList.get(position).getHinh()).into(holder.img_order);

        holder.setOnRecycleViewClickListener((view, position1) -> {
            Common.selectOrderBySeller=orderList.get(position1);
            EventBus.getDefault().postSticky(new ViewOrderBySellerClick(true));
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
                EventBus.getDefault().postSticky(new UpdateStatusOrder(-2,position,orderList.get(position).getIdUser()));
            });
            builder.setPositiveButton("no", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

            Dialog dialog=builder.create();
            dialog.show();

        });

        holder.btn_confirm_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(orderList.get(position).getTrangThai()==-2||orderList.get(position).getTrangThai()==2)
                {
                    Toast.makeText(context, "don hang da bi huy", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(orderList.get(position).getTrangThai()==1)
                {
                    Toast.makeText(context, "đơn hàng đã được xác nhận rồi,không thể xác nhận nữa", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("bạn có  muốn xác nhan  đơn hàng này không");
                builder.setNegativeButton("yes", (dialogInterface, i) -> {
                    EventBus.getDefault().postSticky(new UpdateStatusOrder(1,position,orderList.get(position).getIdUser()));
                });
                builder.setPositiveButton("no", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });

                Dialog dialog=builder.create();
                dialog.show();
            }
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
        @BindView(R.id.btn_confirm_sell)
        ImageButton btn_confirm_sell;

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



