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

import etn.app.danghoc.shoppingclient.Model.District;
import etn.app.danghoc.shoppingclient.R;


public class DistrictAdapter extends ArrayAdapter<District> {
    public DistrictAdapter(@NonNull Context context, int resource, @NonNull List<District> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_province, parent, false);
        TextView txt_select = convertView.findViewById(R.id.txt_select);

        District district = this.getItem(position);
        txt_select.setText(district.getDistrictName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catefory_province, parent, false);
        TextView txt_name = convertView.findViewById(R.id.txt_name);

        District district = this.getItem(position);
        txt_name.setText(district.getDistrictName());

        return convertView;
    }
}
