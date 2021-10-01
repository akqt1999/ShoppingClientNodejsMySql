package etn.app.danghoc.shoppingclient.ui.view_order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.R;

public class OrderDetailDialog extends DialogFragment{
    static OrderDetailDialog newInstance() {
        return new OrderDetailDialog();
    }


    TextView txt_name_product, txt_price, txt_address, txt_phone, txt_name_buyer,txt_date_place,txt_name_seller,txt_phone_seller;
    Button btn_close,btn_call;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_order_detail_for_buyer, container, false);

        initView(view);
        displayDetailOrder();
        return view;
    }

    private void displayDetailOrder() {
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");

        txt_name_product.setText(Common.selectOrderByBuyer.getTenSP());
        txt_price.setText(Common.formatPrice(Common.selectOrderByBuyer.getGia()));
        txt_address.setText(Common.selectOrderByBuyer.getDiaChi());
        txt_phone.setText(Common.selectOrderByBuyer.getSdt());
        txt_name_buyer.setText(Common.selectOrderByBuyer.getTenUser());
        txt_date_place.setText(simpleDateFormat.format(Common.selectOrderByBuyer.getNgayDat()));

        txt_name_seller.setText(Common.selectOrderByBuyer.getNameSeller());
        txt_phone_seller.setText(Common.selectOrderByBuyer.getSdtSeller());

        btn_close.setOnClickListener(view -> {
            dismiss();
        });
    }

    private void initView(View view) {
        btn_close = view.findViewById(R.id.btn_close);

        txt_name_product = view.findViewById(R.id.txt_name_product);
        txt_price = view.findViewById(R.id.txt_price);
        txt_address = view.findViewById(R.id.txt_address);
        txt_phone = view.findViewById(R.id.txt_phone);
        txt_name_buyer = view.findViewById(R.id.txt_name_buyer);
        txt_date_place=view.findViewById(R.id.txt_date_place);
        txt_name_seller=view.findViewById(R.id.txt_name_seller);
        txt_phone_seller=view.findViewById(R.id.txt_phone_seller);
    }
}
