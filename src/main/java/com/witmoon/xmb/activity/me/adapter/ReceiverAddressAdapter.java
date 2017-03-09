package com.witmoon.xmb.activity.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.model.ReceiverAddress;

import java.util.List;


/**
 * 收货地址适配器
 * Created by zhyh on 2015/6/2.
 */
public class ReceiverAddressAdapter extends RecyclerView.Adapter<ReceiverAddressAdapter
        .AddressHolder> {
    private LayoutInflater mLayoutInflater;
    private List<ReceiverAddress> mAddressList;

    private OnSetDefaultClickListener mOnSetDefaultClickListener;
    private OnDeleteClickListener mOnDeleteClickListener;
    private OnEditClickListener mOnEditClickListener;
    private OnConfirmClickListener mOnConfirmClickListener;

    public ReceiverAddressAdapter(Context context, List<ReceiverAddress> addressList) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mAddressList = addressList;
    }

    @Override
    public AddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_receiver_address, parent, false);
        return new AddressHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressHolder holder, final int position) {
        final ReceiverAddress address = mAddressList.get(position);
        holder.mNameText.setText(address.getName());
        holder.mTelephoneText.setText(address.getTelephone());
        holder.mAddressText.setText(address.getProvinceName() + address.getCityName() + address
                .getDistrictName() + address.getAddress());
        holder.mSetDefaultText.setChecked(address.isDefault());
        if (address.isDefault()){
            holder.mSetDefaultText.setText("默认");
        }else {
            holder.mSetDefaultText.setText("设为默认");
        }
        holder.mSetDefaultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!address.isDefault() && mOnSetDefaultClickListener != null) {
                    mOnSetDefaultClickListener.onSetDefaultClick(address.getId());
                }
            }
        });

        holder.mDeleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDeleteClickListener.onDeleteClick(address.getId(), position);
            }
        });

        holder.mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEditClickListener != null) {
                    mOnEditClickListener.onEditClick(address.getId());
                }
            }
        });
        holder.mAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnConfirmClickListener != null){
                    mOnConfirmClickListener.onConfirmClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAddressList.size();
    }

    // --------------------------------- 接口定义 --------------------------------
    public interface OnSetDefaultClickListener {
        void onSetDefaultClick(String addrId);
    }

    public void setOnSetDefaultClickListener(OnSetDefaultClickListener listener) {
        mOnSetDefaultClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String id, int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        mOnDeleteClickListener = listener;
    }

    public interface OnEditClickListener {
        void onEditClick(String id);
    }

    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        mOnEditClickListener = onEditClickListener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(int position);
    }
    public void setOnConfirmListener(OnConfirmClickListener onConfirmListener){
        mOnConfirmClickListener = onConfirmListener;
    }

    // ------------------------------- 接口定义 END ------------------------------

    // ViewHolder
    class AddressHolder extends RecyclerView.ViewHolder {

        TextView mNameText;
        TextView mAddressText;
        TextView mTelephoneText;

        CheckedTextView mSetDefaultText;
        TextView mDeleteText;
        TextView mEditText;
        View mAddressLayout;

        public AddressHolder(View itemView) {
            super(itemView);

            mNameText = (TextView) itemView.findViewById(R.id.name);
            mAddressText = (TextView) itemView.findViewById(R.id.address);
            mTelephoneText = (TextView) itemView.findViewById(R.id.telephone);

            mSetDefaultText = (CheckedTextView) itemView.findViewById(R.id.set_default);
            mDeleteText = (TextView) itemView.findViewById(R.id.delete);
            mEditText = (TextView) itemView.findViewById(R.id.edit);
            mAddressLayout = itemView.findViewById(R.id.address_parent);
        }
    }
}
