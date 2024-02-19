package com.example.trackhealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
RecyclerView recyclerView;
    HomeDoctorAdapter adapter;
FloatingActionButton fab;
   List arr=new ArrayList<>();
    View view;
SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);
  fab=view.findViewById(R.id.fab);
   sp=view.getContext().getSharedPreferences("user",Context.MODE_PRIVATE);

        searchPatient(sp.getString("phone",""));
        recyclerView = (RecyclerView) view.findViewById(R.id.pending_rec);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"add person",Toast.LENGTH_SHORT).show();
                Intent fab=new Intent(getActivity(), AddPatientPage.class);
                startActivity(fab);
            }
        });
        return view;
    }



    public void searchPatient(String ph){
        String temp = "https://demo-uw46.onrender.com/api/doctor/getDetails";
        try {
            HashMap<String, String> jsonobj = new HashMap<>();
            jsonobj.put("phone", ph);
            JsonObjectRequest j = new JsonObjectRequest(Request.Method.POST, temp, new JSONObject(jsonobj), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //pb.setVisibility(View.GONE);
                    try {
                        if (Boolean.parseBoolean(response.getString("success"))) {
                            JSONArray ja = response.getJSONArray("patientadd");
                            if (ja.length()>0) {
                                arr=filterArray(response.getJSONArray("patientadd"));
                                adapter = new HomeDoctorAdapter(arr);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setHasFixedSize(true);
                            }
                        }
                        else{
                            //  Toast.makeText(getApplicationContext(),"patient doesn't exists",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        //Toast.makeText(getApplicationContext(), "error"+e, Toast.LENGTH_SHORT).show();
                        //pb.setVisibility(View.GONE);

                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //pb.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue q = Volley.newRequestQueue(getActivity());
            RetryPolicy retryPolicy = new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            );
            j.setRetryPolicy(retryPolicy);
            q.add(j);


        } catch (
                Exception e) {
            //   pb.setVisibility(View.GONE);
            //       pb.setVisibility(View.GONE);

        }

    }



    public List filterArray(JSONArray jsonArray) throws JSONException {
        List<List> outer=new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++){

            JSONObject j=jsonArray.getJSONObject(i);
            List<String> inner=new ArrayList<>();
            if(j.getString("pending").equals("false")){
                inner.add(j.getString("patient_name"));
                inner.add(j.getString("dob"));
                inner.add(j.getString("gender"));
                inner.add(j.getString("address"));
                inner.add(j.getString("issue"));
                inner.add(j.getString("phone"));
                outer.add(inner);

            }

        }

        return outer;
    }




}