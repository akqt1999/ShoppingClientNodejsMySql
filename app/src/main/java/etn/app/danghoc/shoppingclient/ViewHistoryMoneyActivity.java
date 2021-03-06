package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import etn.app.danghoc.shoppingclient.Adapter.HistoryMoneyAdapter;
import etn.app.danghoc.shoppingclient.Adapter.ViewOrderAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.HistoryMoney;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.sendNotificationPack.APIService;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Client;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ViewHistoryMoneyActivity extends AppCompatActivity {

    @BindView(R.id.recycler_history_money)
    RecyclerView recycler_history_money;

    List<HistoryMoney> historyMoneyList;
    HistoryMoneyAdapter adapter;

    IMyShoppingAPI myRestaurantAPI;
    APIService apiService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history_money);

        ButterKnife.bind(this);

        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        apiService= Client.getInstance().create(APIService.class);

        displayHistoryMoney();
        initToolbar();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){ // button back
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbar () {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void displayHistoryMoney() {
        compositeDisposable.
                add(myRestaurantAPI.getHistoryMoney(Common.API_KEY,
                        Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(orderModel -> {
                            if(orderModel.isSuccess())
                            {
                                historyMoneyList=orderModel.getResult();
                                Collections.reverse(historyMoneyList);
                                adapter = new HistoryMoneyAdapter(this, historyMoneyList);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                                recycler_history_money.setLayoutManager(linearLayoutManager);
                                recycler_history_money.setAdapter(adapter);

                                Log.d("loix",orderModel.getResult().size()+"");
                            }
                            else {
                                Toast.makeText(this, "[view history]"+orderModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },throwable -> {
                            Log.d("loix",throwable.getMessage());
                        })
                );
    }
}