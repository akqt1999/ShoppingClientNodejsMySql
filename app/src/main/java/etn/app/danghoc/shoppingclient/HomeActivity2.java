package etn.app.danghoc.shoppingclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.CategoryAdapter;
import etn.app.danghoc.shoppingclient.Adapter.MySanPhamAdapter;
import etn.app.danghoc.shoppingclient.Adapter.SanPhamSliderAdapter;
import etn.app.danghoc.shoppingclient.Callback.IClickItemSanPham;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Model.Tinh;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClientAddress;
import etn.app.danghoc.shoppingclient.Sevices.PicassoImageLoadingService;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Token;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class HomeActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {




    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_restaurant)
    RecyclerView recycler_sanpham;

    MySanPhamAdapter adapter, searchSanPhamAdapter;
    List<SanPham> sanPhamList=new ArrayList<>();

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyShoppingAPI shoppingAPI;
    IMyShoppingAPI addressAPI;

    List<Tinh> provinceList = new ArrayList<>();
    CategoryAdapter adapterCategory;

    //draw
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        initMenu();
        initView();
        displayProvince();
        showDialogLockUser();
        UpdateToken();

    }

    private void initMenu() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this
                ,drawerLayout,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //header
        TextView txt_user_name, txt_user_phone;

        View headerView = navigationView.getHeaderView(0);
        txt_user_name = headerView.findViewById(R.id.txt_name);
        txt_user_phone = headerView.findViewById(R.id.txt_phone);

        txt_user_phone.setText(Common.currentUser.getPhoneUser());
        txt_user_name.setText(Common.currentUser.getNameUser());

        //
    }

    private void displayProvince() {
        compositeDisposable.add(addressAPI.getProvince("8ce54678-f9b7-11eb-bfef-86bbb1a09031",
                "application/json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    try {
                        if (provinceList.size() > 0)
                            provinceList.clear();


                        provinceList = s.getResult();


                        Collections.reverse(provinceList);
                        provinceList.add(0,new Tinh(99998,"Toàn quốc"));

                        adapterCategory = new CategoryAdapter(this, R.layout.item_selected_province, provinceList);
                        spinner.setAdapter(adapterCategory);


                    } catch (Exception e) {
                        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("assas", "loi"+e.getMessage());
                    }

                }, throwable -> {
                    Toast.makeText(this, "loi" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("assas", throwable.getMessage());
                }));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                compositeDisposable.add(shoppingAPI.getSanPhamByProvinceId(Common.API_KEY,
                        Common.currentUser.getIdUser(),
                        provinceList.get(position).getProvinceID() )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sanPhamModel -> {
                            if(sanPhamModel.isSuccess()){

                                sanPhamList.clear();
                                sanPhamList=sanPhamModel.getResult();

                                adapter = new MySanPhamAdapter(HomeActivity2.this, sanPhamList, new IClickItemSanPham() {
                                    @Override
                                    public void onClickItemUser() {
                                            Intent intent=new Intent(HomeActivity2.this,ChiTietSP.class);
                                            startActivity(intent);
                                    }
                                });
                                recycler_sanpham.setAdapter(adapter);
                                displayBanner(sanPhamList);

                            }
                            else {
                                if(sanPhamModel.getMessage().equals("empty")){
                                    Toast.makeText( HomeActivity2.this, "không có sản phẩm ở địa chỉ được chọn", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(HomeActivity2.this, "[HOME]"+sanPhamModel.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }

                        },throwable -> {
                            Toast.makeText(HomeActivity2.this, "[HOME]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





    }


    private void displayBanner(List<SanPham> restaurants) {
        banner_slider.setAdapter(new SanPhamSliderAdapter(restaurants));
    }

    private void initView() {
        addressAPI = RetrofitClientAddress.getInstance("https://dev-online-gateway.ghn.vn/").create(IMyShoppingAPI.class);
        shoppingAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);

          ButterKnife.bind(this);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity2.this, 2);
        recycler_sanpham.setLayoutManager(gridLayoutManager);
        recycler_sanpham.addItemDecoration(new DividerItemDecoration(HomeActivity2.this,gridLayoutManager.getOrientation()));
        // DividerItemDecoration : dung de tao ra cac dau ____ ngan cach


        Slider.init(new PicassoImageLoadingService());

      //  setHasOptionsMenu(true);//enable menu in fragment

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull @NotNull Menu menu) {


        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //event cart item
        MenuItem itemCart=menu.findItem(R.id.cart);
        itemCart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        Intent intent=new Intent(HomeActivity2.this,CartActivity.class);
                        startActivity(intent);
                        return true;

                    }
                }
        );

        //event search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearchFood(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                banner_slider.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                // khi cai action  view nay ket thuc se phuc hoi lai
                banner_slider.setVisibility(View.VISIBLE);
                recycler_sanpham.setAdapter(adapter);

                return true;
            }
        });
        return true;

    }

    private void startSearchFood(String query) {
        compositeDisposable.add(shoppingAPI.searchSanPham(
                Common.API_KEY,
                query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sanPhamModel -> {
                    if (sanPhamModel.isSuccess()) {

                        searchSanPhamAdapter = new MySanPhamAdapter(HomeActivity2.this, sanPhamModel.getResult(), new IClickItemSanPham() {
                            @Override
                            public void onClickItemUser() {
                                Intent intent=new Intent(HomeActivity2.this,ChiTietSP.class);
                                startActivity(intent);
                            }
                        });
                        recycler_sanpham.setAdapter(searchSanPhamAdapter);
                    } else {
                        if (sanPhamModel.getMessage().contains("empty")) {
                            Toast.makeText(HomeActivity2.this, "not found", Toast.LENGTH_SHORT).show();
                            recycler_sanpham.setAdapter(null);
                        }
                    }
                }, throwable -> {
                    Toast.makeText(HomeActivity2.this, "[search]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
         int id=item.getItemId();



        if (id == R.id.nav_sign_out) {
            xacNhanDangXuat();
        }
        else if (id == R.id.nav_home) {

        } else if (id == R.id.nav_view_order) {
            Intent intent=new Intent(HomeActivity2.this,ViewOrder.class);
            startActivity(intent);

        } else if (id == R.id.nav_view_order_seller) {
            Intent intent=new Intent(HomeActivity2.this,ViewOrderSeller.class);
            startActivity(intent);

        } else if (id == R.id.nav_view_add_new_product) {
            Intent intent=new Intent(HomeActivity2.this,AddNewProduct.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_view_my_product) {
            Intent intent=new Intent(HomeActivity2.this,MyProductActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_category)
        {
            Intent intent=new Intent(HomeActivity2.this,CategoryProductActivity.class);
            startActivity(intent);
        }

       drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void xacNhanDangXuat() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signOut();
            }
        });
        b.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        b.setTitle("ban co muon dang xuat khong??");
        AlertDialog dialog = b.create();
        dialog.show();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Common.currentUser = null;
        startActivity(new Intent(HomeActivity2.this, MainActivity.class));
        finish();

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();

        }
    }



    private void showDialogLockUser() {
        if(Common.currentUser.getTrangThai()==1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("tai khoan bi khoa");
            builder.setMessage("lien he mail: bexiu.1964@gmail.com de giai quyet");
            builder.setCancelable(false);
            builder.setNegativeButton("thoat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }

    }
    private void UpdateToken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful())
                        Toast.makeText(this, "Fetching FCM registration token failed"+task.getException(), Toast.LENGTH_SHORT).show();

                    String refreshToken=task.getResult();
                    Token token= new Token(refreshToken);
                    FirebaseDatabase.getInstance().getReference("Tokens")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
                });
    }

    @Override
    protected void onStop() {
        Log.d("huydd","stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("huydd","destriy");
        super.onDestroy();
    }
}