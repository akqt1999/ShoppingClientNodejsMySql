package etn.app.danghoc.shoppingclient.ui.view_order_seller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.ViewOrderAdapter;
import etn.app.danghoc.shoppingclient.Adapter.ViewOrderBySellerClick;
import etn.app.danghoc.shoppingclient.Adapter.ViewOrderSellerAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.UpdateStatusOrder;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.R;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.databinding.FragmentGalleryBinding;
import etn.app.danghoc.shoppingclient.databinding.FragmentViewOrderBinding;
import etn.app.danghoc.shoppingclient.databinding.FragmentViewOrderSellerBinding;
import etn.app.danghoc.shoppingclient.sendNotificationPack.APIService;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Client;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Data;
import etn.app.danghoc.shoppingclient.sendNotificationPack.MyResponse;
import etn.app.danghoc.shoppingclient.sendNotificationPack.NotificationSender;
import etn.app.danghoc.shoppingclient.ui.cart.ConfirmOrderDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderSellerFragment extends Fragment {

    private ViewOrderSellerModel viewOrderSellerModel;
    private FragmentViewOrderSellerBinding binding;


    Unbinder unbinder;
    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;
    ViewOrderSellerAdapter adapter;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    List<Order> orderList;

    private APIService apiService;
    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrderSellerModel =
                new ViewModelProvider(this).get(ViewOrderSellerModel.class);

        binding = FragmentViewOrderSellerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        unbinder = ButterKnife.bind(this, root);
        compositeDisposable = new CompositeDisposable();
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        apiService= Client.getInstance().create(APIService.class);
        viewOrderSellerModel.getOrderList().observe(this, orders -> {
            if (orders.size() > 0) {
                Common.orderSellerList = orders;
                orderList = orders;
                displayOrders();
                progress_bar.setVisibility(View.GONE);
            } else {
                progress_bar.setVisibility(View.GONE);
                viewOrderSellerModel.getMessageError().observe(this, s -> {
                    Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
                });
            }
        });

        return root;
    }

    private void displayOrders() {
        orderList.get(0).getTenSP();
        adapter = new ViewOrderSellerAdapter(getContext(), orderList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(linearLayoutManager);
        recycler_orders.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        EventBus.getDefault().postSticky(new UpdateStatusOrder(-99, -1));
        super.onStart();
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(toString()))
            EventBus.getDefault().unregister(this);
        EventBus.getDefault().postSticky(new UpdateStatusOrder(-99, -1));
        EventBus.getDefault().postSticky(new ViewOrderBySellerClick(false));
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onClickOrder(ViewOrderBySellerClick event) {
        if(event.isSuccess())
        {
            OrderDetailDialog dialog = OrderDetailDialog.newInstance();
            dialog.show(getParentFragmentManager(), "tag122");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void cancelOrder(UpdateStatusOrder event) {//-2 huy don hang boi nguoi ban
        if (event.getStatus()==-2) {
            int position = event.getPosition();

            compositeDisposable.
                    add(myRestaurantAPI.updateStatusOrder(
                            Common.API_KEY,
                            Common.orderSellerList.get(position).getIdDonHang(),
                            -2
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(updateStatusModel -> {
                                        if (updateStatusModel.isSuccess()) {

                                            String message="nguoi ban da huy don hang cua ban";
                                            sendNotificationChangeStatus("huy bo",message,event.getIdUser());


                                            Common.orderSellerList.get(position).setTrangThai(-2);
                                            orderList.get(position).setTrangThai(-2);

                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), "cancel order success", Toast.LENGTH_SHORT).show();

                                        } else
                                            Toast.makeText(getContext(), "update status fail", Toast.LENGTH_SHORT).show();

                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[update status]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d("zxc",throwable.getMessage());
                                    })
                    );
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void confirmOrder(UpdateStatusOrder event) {//1 nguoi ban xac don hang
        if (event.getStatus()==1) {
            int position = event.getPosition();

            compositeDisposable.
                    add(myRestaurantAPI.updateStatusOrder(
                            Common.API_KEY,
                            Common.orderSellerList.get(position).getIdDonHang(),
                            1
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(updateStatusModel -> {
                                        if (updateStatusModel.isSuccess()) {

                                            String message="nguoi ban da xac nhan don hang cua ban";
                                            sendNotificationChangeStatus("xac nhan",message,event.getIdUser());

                                            Common.orderSellerList.get(position).setTrangThai(1);
                                            orderList.get(position).setTrangThai(1);

                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), "cancel order success", Toast.LENGTH_SHORT).show();

                                        } else
                                            Toast.makeText(getContext(), "update status fail", Toast.LENGTH_SHORT).show();

                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[update status]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
        }
    }

    private void sendNotificationChangeStatus(String title, String message, String idSeller) {


        Data data = new Data(title, message);


        FirebaseDatabase.getInstance()
                .getReference().child("Tokens").child(idSeller).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String usertoken=snapshot.getValue(String.class);
                        NotificationSender sender = new NotificationSender(data, usertoken);

                        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.code()==200)
                                {
                                    if(response.body().success!=1){
                                        Toast.makeText(getContext(), "send Notification Fail", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "send notifi success", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}