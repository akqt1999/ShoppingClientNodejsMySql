package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import etn.app.danghoc.shoppingclient.Adapter.CategorySanPhamAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.DanhMucItemClick;
import etn.app.danghoc.shoppingclient.EventBus.MyProductItemEdit;
import etn.app.danghoc.shoppingclient.EventBus.UpdateStatusOrder;
import etn.app.danghoc.shoppingclient.EventBus.ViewOrderByBuyerClick;
import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryProductActivity extends AppCompatActivity {


    @BindView(R.id.recycler_category)
    RecyclerView recycler_category;

    CategorySanPhamAdapter adapter;
    List<CategoryProduct>categoryProducts=new ArrayList<>();
    
    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        ButterKnife.bind(this);
        myRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        initToolbar();
        displayCategory();
    }

    private void displayCategory() {

        compositeDisposable.
                add(myRestaurantAPI.getDanhMuc(Common.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            if (model.isSuccess()) {

                                categoryProducts=model.getResult();
                                adapter=new CategorySanPhamAdapter(this,categoryProducts);
                                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
                                recycler_category.setLayoutManager(linearLayoutManager);
                                recycler_category.setAdapter(adapter);


                            } else {
                                Toast.makeText(this, model.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }, throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );

        adapter=new CategorySanPhamAdapter(this,categoryProducts);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recycler_category.setLayoutManager(linearLayoutManager);
        recycler_category.setAdapter(adapter);
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        EventBus.getDefault().postSticky(new DanhMucItemClick(false, -19));
        super.onStart();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().postSticky(new DanhMucItemClick(false, -99));
        super.onPause();
    }

    @Override
    public void onStop() {

        if (EventBus.getDefault().isRegistered(toString()))
            EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // button back
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onItemClick(DanhMucItemClick event){
        if(event.isSuccess()){
            startActivity(new Intent(CategoryProductActivity.this,ProductByCategory.class));
        }
    }

}