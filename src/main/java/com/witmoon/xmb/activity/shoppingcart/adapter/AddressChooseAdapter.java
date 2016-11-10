package com.witmoon.xmb.activity.shoppingcart.adapter;

import android.content.Context;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mabao.adapter.AddordableAdapter;
import com.witmoon.xmb.model.ReceiverAddress;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by de on 2016/10/21.
 */
public class AddressChooseAdapter extends RecyclerView.Adapter<AddressChooseAdapter.AddressViewHolder> {

    private ArrayList<ReceiverAddress> mAddressArrayList;
    private OnItemClickListener mListener;
    private Context mContext;

    public AddressChooseAdapter(Context context, ArrayList<ReceiverAddress> data) {
        mContext = context;
        mAddressArrayList = data;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_address_selector, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressChooseAdapter.AddressViewHolder holder, int position) {
        ReceiverAddress address = mAddressArrayList.get(position);
        holder.name.setText(address.getName());
        holder.telephone.setText(address.getTelephone());
        holder.address.setText(address.getProvinceName() +
                address.getCityName() + address.getDistrictName() + address.getAddress());
        if (address.isDefault()){
            holder.is_default.setVisibility(View.VISIBLE);
        }else{
            holder.is_default.setVisibility(View.INVISIBLE);
        }
        if (mListener != null){
            holder.address_parent.setOnClickListener(v -> {
                mListener.onItemClick(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mAddressArrayList.size();
    }


    public class AddressViewHolder extends RecyclerView.ViewHolder {
        public TextView name, telephone, address;
        public CheckBox is_default;
        public LinearLayout address_parent;

        public AddressViewHolder(View itemView) {
            super(itemView);
            address_parent = (LinearLayout) itemView.findViewById(R.id.address_parent);
            name = (TextView) itemView.findViewById(R.id.name);
            telephone = (TextView) itemView.findViewById(R.id.telephone);
            address = (TextView) itemView.findViewById(R.id.address);
            is_default = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}
