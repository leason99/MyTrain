package leason.mytraintime;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by leason on 2016/11/2.
 */
public class RecyclerAdapter extends RecyclerView.Adapter{
    List<Map<String, String>> listData;
    Context context;
    LayoutInflater layoutInflater;
    public RecyclerAdapter(Context context, List<Map<String, String>> listData){



        this.listData=listData;
        this.context=context;
        this.layoutInflater=LayoutInflater.from(context);
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(listData.size()!=0) {
            view = layoutInflater.inflate(R.layout.search_result_listview, parent, false);

        }
        else
        {view=layoutInflater.inflate(R.layout.recycler_no_data,parent,false);
        }


        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;



    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
    if(listData.size()!=0)
    {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            Map<String, String> map = listData.get(position);
            recyclerViewHolder.TrainNo.setText(map.get("TrainNo"));
            recyclerViewHolder.DepartureTime.setText(map.get("DepartureTime"));
            recyclerViewHolder.Arrivaltime.setText(map.get("ArrivalTime"));
            recyclerViewHolder.note.setText(map.get("Note"));
        }



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                setRemind setremind = new setRemind();

                TextView textView = (TextView) v.findViewById(R.id.Arrivaltime);

                setremind.setTime(textView.getText().toString());


                setremind.show(SearchResult.instance.getSupportFragmentManager(), "remindTime");


                return false;
            }
        });



    }

    @Override
    public int getItemCount() {


    if(listData.size()!=0) {
        Log.i("ListDataSize", String.valueOf(listData.size()));
        return listData.size();

    }
        else{
            Log.i("ListDataSize", String.valueOf(listData.size()));
            return 1;
        }

    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView TrainNo,DepartureTime,Arrivaltime,note;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            TrainNo=(TextView) itemView.findViewById(R.id.TrainNo);
            DepartureTime=(TextView) itemView.findViewById(R.id.DepartureTime);
                    Arrivaltime=(TextView) itemView.findViewById(R.id.Arrivaltime);
            note=(TextView) itemView.findViewById(R.id.note);
        }
    }







    }






