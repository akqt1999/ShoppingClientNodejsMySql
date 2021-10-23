package etn.app.danghoc.shoppingclient.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.CartAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.CartIsChoose;
import etn.app.danghoc.shoppingclient.EventBus.CartItemDelete;
import etn.app.danghoc.shoppingclient.EventBus.HideFABCart;
import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.R;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.databinding.FragmentCartBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;
    private FragmentCartBinding binding;

    List<Cart> cartList = new ArrayList<>();


    Unbinder unbinder;

    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.txtTotalPrice)
    TextView txtTotalPrice;
    @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;
    @BindView(R.id.btn_place_order)
    Button btn_place_order;
    CartAdapter adapter;

    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        unbinder = ButterKnife.bind(this, root);

        cartViewModel.getMessageError().observe(this, s -> {
            txt_empty_cart.setVisibility(View.VISIBLE);
            // progress_bar.setVisibility(View.GONE);
            if (s != null || s.length() != 0) {
                txt_empty_cart.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "null cart", Toast.LENGTH_SHORT).show();
                //  progress_bar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "[get listr cart]" + s, Toast.LENGTH_SHORT).show();

            }
        });

        cartViewModel.getListCart().observe(this, carts -> {

            //    progress_bar.setVisibility(View.GONE);

            if (carts.size() == 0) {

                txt_empty_cart.setVisibility(View.VISIBLE);
            } else {

                Common.cartList = carts;
                //     progress_bar.setVisibility(View.GONE);
                cartList = carts;
                displayCart();
            }
        });
        //   progress_bar.setVisibility(View.GONE);
        init();
        return root;
    }

    private void init() {
        compositeDisposable = new CompositeDisposable();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
    }

    private void displayCart() {
        if (cartList.size() == 0) {
            recyclerCart.setVisibility(View.GONE);
            group_place_holder.setVisibility(View.GONE);
            txt_empty_cart.setVisibility(View.VISIBLE);
        } else {
            recyclerCart.setVisibility(View.VISIBLE);
            group_place_holder.setVisibility(View.VISIBLE);
            txt_empty_cart.setVisibility(View.GONE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerCart.setLayoutManager(linearLayoutManager);
            adapter = new CartAdapter(getContext(), cartList);
            recyclerCart.setAdapter(adapter);
        }

    }

    @Override
    public void onStart() {


        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        EventBus.getDefault().postSticky(new HideFABCart(true));
    }

    @Override
    public void onStop() {

        EventBus.getDefault().postSticky(new CartItemDelete(false, -1));// khi thoat la no se xoa cai event bus

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        EventBus.getDefault().postSticky(new CartIsChoose(false));
        EventBus.getDefault().postSticky(new HideFABCart(false));

        cartViewModel.onStop();

        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void deleteItemCart(CartItemDelete event) {
        if (event.isSuccess()) {
            Toast.makeText(getContext(), "nhan nut xoa" + event.getPosition(), Toast.LENGTH_SHORT).show();

            int position = event.getPosition();

            // adapter.deleteItem(position);
            //adapter.notifyItemRemoved(position);

            //  progress_bar.setVisibility(View.VISIBLE);
            compositeDisposable.
                    add(myRestaurantAPI.deleteCart(
                            Common.API_KEY,
                            Common.currentUser.getIdUser(),
                            Common.cartList.get(position).getIdSP()//khi xoa thi cai common no cung xoa theo
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(cartModel -> {
                                        if (cartModel.isSuccess()) {
                                            //Common.cartList.remove(position);
                                            cartList.remove(position);
                                            adapter.notifyDataSetChanged();
                                            // progress_bar.setVisibility(View.GONE);
                                            totalPrice();
                                            Toast.makeText(getContext(), "size common" + Common.cartList.size(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getContext(), "delete success", Toast.LENGTH_SHORT).show();
                                        } else {
                                            //   progress_bar.setVisibility(View.GONE);
                                        }

                                    }, throwable -> {
                                        // progress_bar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "[DELETE CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
        }
    }

    //
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void CartIsChoose(CartIsChoose event) {
        if (event.isSuccess()) {
            totalPrice();
        }
    }


    public void totalPrice() {
        float totalPrice = 0;

        for (int i = 0; i < Common.cartList.size(); i++) {
            if (Common.cartList.get(i).isChoose()) {
                totalPrice = totalPrice + Common.cartList.get(i).getGia();
            }
        }

        double totalPriceFinal = Double.parseDouble(totalPrice + ""), displayPrice = 0.0;
        displayPrice = totalPriceFinal;
        displayPrice = Math.round(displayPrice * 100.0 / 100.0);
        txtTotalPrice.setText(new StringBuilder().append(Common.formatPrice(displayPrice)));
        Common.totalPriceFromCart = totalPriceFinal;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @OnClick(R.id.btn_place_order)
    void placeOrderClick() {


        boolean isChoose = true;
        for (int i = 0; i < Common.cartList.size(); i++) {
            if (Common.cartList.get(i).isChoose()) {
                isChoose = false;
                break;
            }
        }

        if (isChoose)
            Toast.makeText(getContext(), "chưa chọn đơn hàng cần mua", Toast.LENGTH_SHORT).show();
        else {
            DialogFragment dialog = ConfirmOrderDialog.newInstance();
            dialog.show(getParentFragmentManager(), "tag");
        }

    }

}