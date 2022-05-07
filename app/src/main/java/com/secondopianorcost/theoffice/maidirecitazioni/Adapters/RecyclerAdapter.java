package com.secondopianorcost.theoffice.maidirecitazioni.Adapters;


import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.secondopianorcost.theoffice.maidirecitazioni.Entities.Character;
import com.secondopianorcost.theoffice.maidirecitazioni.Listeners.CharacterClickListener;
import com.theoffice.maidirecitazioni.R;

import java.util.ArrayList;

/**
 * Created by giuse on 23/12/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    ArrayList<Character> list = new ArrayList<>();
    private CharacterClickListener characterClickListener;

    public RecyclerAdapter(ArrayList<Character> list, CharacterClickListener characterClickListener) {
        this.list = list;
        this.characterClickListener = characterClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.o_flag.setImageResource(list.get(position).getFlag());
        holder.o_name.setText(list.get(position).getName());

        final Character character = list.get(position);

        ViewCompat.setTransitionName(holder.o_flag, character.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                characterClickListener.onCharacterItemClick(holder.getAdapterPosition(), character, holder.o_flag);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView o_flag;
        TextView o_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            o_flag = (ImageView) itemView.findViewById(R.id.flag);
            o_name = (TextView) itemView.findViewById(R.id.name);
        }

    }

    public void setFilter(ArrayList<Character> newList) {

        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();

    }



}
