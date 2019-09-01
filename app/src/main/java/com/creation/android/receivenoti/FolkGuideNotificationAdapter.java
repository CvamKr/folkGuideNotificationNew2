package com.creation.android.receivenoti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FolkGuideNotificationAdapter extends RecyclerView.Adapter<FolkGuideNotificationAdapter.MyViewHolder> {

    Context context;
    ArrayList<FolkBoyData> folkBoyArraylist;

    MyClickListner myClickListner;

    public FolkGuideNotificationAdapter(Context context, ArrayList<FolkBoyData> folkBoyArraylist) {
        this.context = context;
        this.folkBoyArraylist = folkBoyArraylist;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.layout_list_item_fg_notification, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        FolkBoyData currnetFolkBoy = folkBoyArraylist.get(i);
//        myViewHolder.tv_name.setText(currnetFolkBoy.getFb_name());
//        myViewHolder.tv_phone.setText(currnetFolkBoy.getFb_phone());
        myViewHolder.tv_berth.setText("Berth Pref: " + currnetFolkBoy.getFb_berth_pref());
        myViewHolder.tv_email.setText("Email: "+currnetFolkBoy.getFb_email());
        myViewHolder.tv_message.setText("Message: "+currnetFolkBoy.getFb_message());


    }

    @Override
    public int getItemCount() {
        return folkBoyArraylist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_email, tv_phone , tv_berth, tv_message ;
        Button btnAcceptRequest, btnDeclineRequest;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            tv_email = itemView.findViewById(R.id.tv_email);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            btnAcceptRequest = itemView.findViewById(R.id.button_accept_req);
            btnDeclineRequest = itemView.findViewById(R.id.button_decline_req);
            tv_berth = itemView.findViewById(R.id.textViewBerth);
            tv_message = itemView.findViewById(R.id.textViewMessage);



            btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    int viewId = view.getId();
                    if (myClickListner != null) {

                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION)
                            myClickListner.myAcceptReqBtnClick(view, position);

                    }
                }
            });

            btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(myClickListner!= null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            myClickListner.myDeclineReqBtnClick(view, position);
                        }
                    }
                }
            });
        }
    }

    public interface MyClickListner {
        void myAcceptReqBtnClick(View view, int currentPosition);
        void myDeclineReqBtnClick(View view, int currentPosition);
    }

    public void mySetOnClickListner(MyClickListner myClickListner) {
        this.myClickListner = myClickListner;
    }
}
