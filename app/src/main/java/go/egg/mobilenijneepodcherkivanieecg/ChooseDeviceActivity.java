package go.egg.mobilenijneepodcherkivanieecg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ChooseDeviceActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    DeviceMap mDeviceMap;


    Button start_scan;
    Button stop_scan;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        mRecyclerView.findViewById(R.id.recycler_view_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        update();

    }

    private void update(){
        mDeviceMap = DeviceMap.get();
        //
    }
    private class DeviceHolder extends RecyclerView.ViewHolder {
        TextView textView;
        String mString;


        public DeviceHolder(View itemView) {
            super(itemView);
            textView.findViewById(R.id.item_list_text_name);
        }
        public void setter(String string){
            mString = string;
            textView.setText(mString);
        }
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder>{

        @Override
        public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.item_list,parent,false);
            return new DeviceHolder(view);
        }

        @Override
        public void onBindViewHolder(DeviceHolder holder, int position) {
            holder.setter((String) mDeviceMap.get_map().keySet().toArray()[position]);

        }

        @Override
        public int getItemCount() {
            return mDeviceMap.get_map().size();
        }
    }
}
