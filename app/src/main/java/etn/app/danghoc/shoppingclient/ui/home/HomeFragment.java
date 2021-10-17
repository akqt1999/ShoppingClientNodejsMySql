package etn.app.danghoc.shoppingclient.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
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
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Model.Tinh;
import etn.app.danghoc.shoppingclient.R;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClientAddress;
import etn.app.danghoc.shoppingclient.Sevices.PicassoImageLoadingService;
import etn.app.danghoc.shoppingclient.databinding.FragmentHomeBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    Unbinder unbinder;

    IMyShoppingAPI addressAPI;
 //   @BindView(R.id.spinner)

    Spinner spinner;

    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_restaurant)
    RecyclerView recycler_sanpham;

    MySanPhamAdapter adapter, searchSanPhamAdapter;
    List<SanPham> sanPhamList=new ArrayList<>();

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyShoppingAPI shoppingAPI;
    List<Tinh> provinceList = new ArrayList<>();
    CategoryAdapter adapterCategory;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //load recyclerview
        homeViewModel.getListSanPham().observe(this, sanPhams -> {
            if (sanPhams.size() != 0) {
                sanPhamList=sanPhams;
                displayBanner(sanPhamList);
                displayRestaurant(sanPhamList);
            } else
                homeViewModel.getMessageError().observe(this, error -> {
                    Toast.makeText(getContext(), "[Load  restaurant ]" + error, Toast.LENGTH_SHORT).show();
                });

        });
        homeViewModel.getMessageError().observe(this,s -> {
        });

        addressAPI = RetrofitClientAddress.getInstance("https://dev-online-gateway.ghn.vn/").create(IMyShoppingAPI.class);

        initView(root);
        displayProvince();

        Log.d("testacti","create");

        return root;


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

                        adapterCategory = new CategoryAdapter(getContext(), R.layout.item_selected_province, provinceList);

                        spinner.setAdapter(adapterCategory);


                    } catch (Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }, throwable -> {
                    Toast.makeText(getContext(), "loi" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
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

                                adapter = new MySanPhamAdapter(getContext(), sanPhamList);
                                recycler_sanpham.setAdapter(adapter);
                            displayBanner(sanPhamList);


                          //  adapter.notifyDataSetChanged();

                        }
                        else {
                            if(sanPhamModel.getMessage().equals("empty")){
                                Toast.makeText(getContext(), "không có sản phẩm ở địa chỉ được chọn", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(getContext(), "[HOME]"+sanPhamModel.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }

                    },throwable -> {
                        Toast.makeText(getContext(), "[HOME]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





    }

    private void displayRestaurant(List<SanPham> sanPhams) {
        if(sanPhams.size()>0)
        {
            adapter = new MySanPhamAdapter(getContext(), sanPhams);
            recycler_sanpham.setAdapter(adapter);
        }

    }

    private void displayBanner(List<SanPham> restaurants) {
        banner_slider.setAdapter(new SanPhamSliderAdapter(restaurants));
    }

    private void initView(View root) {
        unbinder = ButterKnife.bind(this, root);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recycler_sanpham.setLayoutManager(gridLayoutManager);
        recycler_sanpham.addItemDecoration(new DividerItemDecoration(getContext(), gridLayoutManager.getOrientation()));
        // DividerItemDecoration : dung de tao ra cac dau ____ ngan cach

        spinner=root.findViewById(R.id.spinner);

        Slider.init(new PicassoImageLoadingService());

        setHasOptionsMenu(true);//enable menu in fragment

        shoppingAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //event
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

    }

    private void startSearchFood(String query) {
        compositeDisposable.add(shoppingAPI.searchSanPham(
                Common.API_KEY,
                query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sanPhamModel -> {
                    if (sanPhamModel.isSuccess()) {

                        searchSanPhamAdapter = new MySanPhamAdapter(getContext(), sanPhamModel.getResult());
                        recycler_sanpham.setAdapter(searchSanPhamAdapter);
                    } else {
                        if (sanPhamModel.getMessage().contains("empty")) {
                            Toast.makeText(getContext(), "not found", Toast.LENGTH_SHORT).show();
                            recycler_sanpham.setAdapter(null);
                        }
                    }
                }, throwable -> {
                    Toast.makeText(getContext(), "[search]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("testacti","destroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("testacti","stop");

    }
}