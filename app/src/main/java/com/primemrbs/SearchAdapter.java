package com.primemrbs;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Created by csa on 3/1/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Myholder> {
    List<DataModel> dataModelArrayList;
    DatabaseHelper database;

    public SearchAdapter(List<DataModel> dataModelArrayList) {
        this.dataModelArrayList = dataModelArrayList;
    }

    class Myholder extends RecyclerView.ViewHolder {
        TextView name, mtrno, acctno, oldacctno, readstat, pageno, id;
        String _LoadAdapter;
        public View view;

        public Myholder(View itemView) {
            super(itemView);
            //readstat = itemView.findViewById(R.id.readstat1);
            name = itemView.findViewById(R.id.name1);
            mtrno = itemView.findViewById(R.id.mtrno1);
            acctno = itemView.findViewById(R.id.acctno1);
            oldacctno = itemView.findViewById(R.id.oldacctno1);
            id = itemView.findViewById(R.id.id1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String SearchCustAcctNo = acctno.getText().toString();

                    Intent i = new Intent(v.getContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("SearchFlag", SearchCustAcctNo);
                    v.getContext().startActivity(i);
                }
            });
        }
    }

    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview,null);
        return new Myholder(view);

    }

    @Override
    public void onBindViewHolder(Myholder holder, int position) {
        DataModel dataModel=dataModelArrayList.get(position);
        //holder.readstat.setText(dataModel.getReadstat());
        holder.name.setText(dataModel.getName());
        holder.mtrno.setText(dataModel.getMtrno());
        holder.acctno.setText(dataModel.getAcctno());
        holder.oldacctno.setText(dataModel.getOldacctno());
        holder.id.setText(dataModel.getID());

    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public void updateList(List<DataModel> list){
        dataModelArrayList = list;
        notifyDataSetChanged();
    }
}