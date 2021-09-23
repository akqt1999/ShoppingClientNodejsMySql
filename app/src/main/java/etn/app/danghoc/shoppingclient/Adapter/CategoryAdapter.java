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

import etn.app.danghoc.shoppingclient.Model.Tinh;
import etn.app.danghoc.shoppingclient.R;


public class CategoryAdapter extends ArrayAdapter<Tinh> {
    public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<Tinh> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_province, parent, false);
        TextView txt_select = convertView.findViewById(R.id.txt_select);

        Tinh tinh = this.getItem(position);
        txt_select.setText(tinh.getProvinceName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catefory_province, parent, false);
        TextView txt_name = convertView.findViewById(R.id.txt_name);

        Tinh tinh = this.getItem(position);
        txt_name.setText(tinh.getProvinceName());

        return convertView;
    }
}
