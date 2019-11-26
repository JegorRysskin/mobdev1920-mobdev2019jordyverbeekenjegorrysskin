package com.example.pxlparking;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private final ParkingAdapterOnClickHandler mClickHandler;

    public ParkingAdapter(Context context, Cursor cursor, ParkingAdapterOnClickHandler clickHandler) {
        mCursor = cursor;
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public ParkingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.parking_list_item, parent, false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkingViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        String parkingName = mCursor.getString(mCursor.getColumnIndex("name"));

        holder.mParkingNameTextView.setText(parkingName);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;

        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;

        if (newCursor != null){
            this.notifyDataSetChanged();
        }
    }

    public class ParkingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mParkingNameTextView;

        public ParkingViewHolder(View itemView) {
            super(itemView);
            mParkingNameTextView = itemView.findViewById(R.id.tv_parking_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.onClick(adapterPosition);
        }
    }
}
