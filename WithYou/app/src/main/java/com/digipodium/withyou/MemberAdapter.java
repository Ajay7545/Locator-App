package com.digipodium.withyou;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberHolder> {

    public final Context context;
    LayoutInflater inflater;

    public MemberAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.member,parent,false);
        MemberHolder holder=new MemberHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MemberHolder extends RecyclerView.ViewHolder {
        TextView data;
        TextView contact;
        public MemberHolder(View itemView) {
            super(itemView);
            data=(itemView).findViewById(R.id.data);
            contact=(itemView).findViewById(R.id.contact);
        }
    }
}
