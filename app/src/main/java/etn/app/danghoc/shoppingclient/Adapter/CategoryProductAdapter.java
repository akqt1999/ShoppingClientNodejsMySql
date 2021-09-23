package etn.app.danghoc.shoppingclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.R;

public class CategoryProductAdapter extends ArrayAdapter<CategoryProduct> {
    public CategoryProductAdapter(@NonNull Context context, int resource, @NonNull List<CategoryProduct> categoryProducts) {
        super(context, resource, categoryProducts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_product, parent, false);

        TextView txt_name = convertView.findViewById(R.id.txt_name);
        CategoryProduct categoryProduct = this.getItem(position);
        txt_name.setText(categoryProduct.getTenDM());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_product, parent, false);

        TextView txt_name = convertView.findViewById(R.id.txt_name);
        CategoryProduct categoryProduct = this.getItem(position);
        txt_name.setText(categoryProduct.getTenDM());

        return convertView;
    }
}
