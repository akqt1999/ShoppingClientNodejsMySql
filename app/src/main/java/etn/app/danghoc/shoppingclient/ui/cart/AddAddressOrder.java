package etn.app.danghoc.shoppingclient.ui.cart;

import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.CategoryAdapter;
import etn.app.danghoc.shoppingclient.Adapter.DistrictAdapter;
import etn.app.danghoc.shoppingclient.Adapter.WardAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.AddNewAddressClick;
import etn.app.danghoc.shoppingclient.Model.District;
import etn.app.danghoc.shoppingclient.Model.Tinh;
import etn.app.danghoc.shoppingclient.Model.Ward;
import etn.app.danghoc.shoppingclient.R;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClientAddress;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AddAddressOrder extends DialogFragment {
    static AddAddressOrder newInstance() {
        return new AddAddressOrder();
    }

    IMyShoppingAPI addressAPI;
    IMyShoppingAPI shoppingAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    List<Tinh> provinceList = new ArrayList<>();
    List<District> districtList = new ArrayList<>();
    List<Ward> wardList = new ArrayList<>();
    CategoryAdapter adapter;
    DistrictAdapter districtAdapter;
    WardAdapter wardAdapter;

    Spinner spinner, spinner_district, spinner_ward;
    String ward = "", district = "", province = "";
    TextView txt_address;
    @BindView(R.id.btn_cancel123)
    Button btn_cancel;
    @BindView(R.id.btn_add_address)
    Button btn_add_address;
    TextView edit_phone_number;
    EditText edt_address_number;

    Unbinder unbinder;



    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_address_order, container, false);
        init1(view);
        unbinder = ButterKnife.bind(this, view);
        Paper.init(getContext());
        displayProvince();
        return view;
    }

    private void init1(View view) {
        addressAPI = RetrofitClientAddress.getInstance("https://dev-online-gateway.ghn.vn/").create(IMyShoppingAPI.class);
        shoppingAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        spinner = view.findViewById(R.id.spinner);
        spinner_district = view.findViewById(R.id.spinner_district);
        spinner_ward = view.findViewById(R.id.spinner_ward);
        spinner_district.setVisibility(View.GONE);
        edit_phone_number = view.findViewById(R.id.edit_phone_number);
        txt_address = view.findViewById(R.id.txt_address);
        btn_cancel = view.findViewById(R.id.btn_cancel123);
        btn_add_address = view.findViewById(R.id.btn_add_address);
        edt_address_number = view.findViewById(R.id.edt_address_number);
        edit_phone_number.setText(Common.currentUser.getPhoneUser());
    }

    private void displayWard(int district_id) {
        compositeDisposable.add(addressAPI.getWord(
                "8ce54678-f9b7-11eb-bfef-86bbb1a09031",
                "application/json",
                district_id
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    try {
                        if (wardList.size() > 0)
                            wardList.clear();
                        wardList = s.getData();


                        wardAdapter = new WardAdapter(getContext(), R.layout.item_selected_province, wardList);

                        spinner_ward.setAdapter(wardAdapter);

                        wardAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }, throwable -> {
                    Toast.makeText(getContext(), "loi" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
        spinner_ward.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ward = wardList.get(i).getWardName();
                displayAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void displayDistrict(int province_id) {
        compositeDisposable.add(addressAPI.getDistrict(
                "8ce54678-f9b7-11eb-bfef-86bbb1a09031",
                "application/json",
                province_id
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    try {
                        if (districtList.size() > 0)
                            districtList.clear();

                        districtList = s.getData();

                        //xoa cac huyen loi
                        deleteErrorDistrict();


                        districtAdapter = new DistrictAdapter(getContext(), R.layout.item_selected_province, districtList);

                        spinner_district.setAdapter(districtAdapter);

                        districtAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }, throwable -> {
                    Toast.makeText(getContext(), "loi" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
        spinner_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int district_id = districtList.get(i).getDistrictID();
                displayWard(district_id);
                district = districtList.get(i).getDistrictName();
                displayAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void deleteErrorDistrict() {
        if (districtList.get(0).getDistrictID() == 3451)
            districtList.remove(0);
        if (districtList.get(0).getDistrictID() == 3450)
            districtList.remove(0);
        if (districtList.get(0).getDistrictID() == 3442)
            districtList.remove(0);
        if (districtList.get(0).getDistrictID() == 3447)
            districtList.remove(0);

        // xoa hcm
        for (int i = 0; i < districtList.size(); i++) {
            if (districtList.get(i).getDistrictID() == 1580)
                districtList.remove(i);
        }
        for (int i = 0; i < districtList.size(); i++) {
            if (districtList.get(i).getDistrictID() == 3448)
                districtList.remove(i);
        }

        for (int i = 0; i < districtList.size(); i++) {
            if (districtList.get(i).getDistrictID() == 3449)
                districtList.remove(i);
        }


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
                        adapter = new CategoryAdapter(getContext(), R.layout.item_selected_province, provinceList);

                        spinner.setAdapter(adapter);


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
                spinner_district.setVisibility(View.VISIBLE);
                int province_id = provinceList.get(position).getProvinceID();
                displayDistrict(province_id);
                province = provinceList.get(position).getProvinceName();
                displayAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinner_district.setVisibility(View.GONE);
            }
        });

    }

    private void displayAddress() {
        txt_address.setText(new StringBuilder().append(ward)
                .append(" ")
                .append(district)
                .append(" ")
                .append(province));
    }

    @OnClick(R.id.btn_cancel123)
    void cancelClick() {
        dismiss();
    }

    @OnClick(R.id.btn_add_address)
    void addAddressClick() {

        if (edit_phone_number.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edt_address_number.getText().toString().trim().length() == 0) {
            Toast.makeText(getContext(), "chưa nhập số nhà ", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullAddress = new StringBuilder("SDT:").append(edit_phone_number.getText().toString())
                .append("  ").append(edt_address_number.getText().toString())
                .append("  ").append(txt_address.getText().toString()).toString();
        ;
        Paper.book().write(Common.ORDER_ADDRESS_KEY, fullAddress);
        EventBus.getDefault().postSticky(new AddNewAddressClick(true));
        dismiss();
    }

}
