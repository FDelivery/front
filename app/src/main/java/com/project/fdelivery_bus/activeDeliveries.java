package com.project.fdelivery_bus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// in here the business can see a list of his on going delivers
public class activeDeliveries extends AppCompatActivity {
    private ListView listView;
    private RetrofitInterface  rtfBase = RetrofitBase.getRetrofitInterface();
    String USER,ID,TOKEN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_deliveries);
        listView=(ListView) findViewById(R.id.listDelivery);

        Business businessUser;


        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        {
            USER = extras.getString("businessUserInGson");
            ID =extras.getString("id");
            TOKEN =extras.getString("token");

            //businessUser = new Gson().fromJson(USER, Business.class);

            GetDeliveries(ID);
        }



    }
    //this give us all deliveries that this id-user added
    private void GetDeliveries(String id)
    {
        Call<List<String>> call = rtfBase.getDeliveries(id);
        ArrayList<String> arrayList=new ArrayList<>();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response)
            {

                if(response.code() == 200)
                {
                  //  Log.i("TEST1", String.valueOf(response.body()));
                    for(int i=0;i<response.body().size();i++) {
                        Delivery delivery = new Gson().fromJson(response.body().get(i), Delivery.class);
                        if(!delivery.getStatus().equals("DELIVERED")){
                            String deliveryID=response.body().get(i).substring(18,42);
                            arrayList.add(delivery.getClientName()+" "+delivery.getClientPhone()+"\nid="+deliveryID);
                        }
                    }
                    help(arrayList);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(activeDeliveries.this, t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }
//we put the arraylist in adapter
    private void help(ArrayList<String> arrayList){
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetDelivery(arrayList.get(position).split("id=")[1]);



            }
        });
    }
    //put delivery id and this return you the delivery info
    private void GetDelivery(String idDelivery)
    {
        Call<String> call = rtfBase.getDelivery(idDelivery);
        Intent intent=new Intent(this,showCoosenActivity.class);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {

                if(response.code() == 400)
                {
                    Toast.makeText(activeDeliveries.this, "this ID do not exist",Toast.LENGTH_LONG).show();

                }
                if(response.code() == 200)
                {


                        intent.putExtra("delivery",response.body());
                        intent.putExtra("idDelivery",idDelivery);
                        intent.putExtra("token",TOKEN);
                        intent.putExtra("id",ID);
                        intent.putExtra("businessUserInGson", USER);

                        startActivity(intent);




                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(activeDeliveries.this, t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }


}