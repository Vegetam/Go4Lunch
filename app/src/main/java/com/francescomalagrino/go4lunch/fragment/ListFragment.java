package com.francescomalagrino.go4lunch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.ViewModelFactory;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.ui.list.ListAdapter;
import com.francescomalagrino.go4lunch.ui.list.ListViewModel;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {

    private ListViewModel mViewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.list_recyclerview);
        progressBar = view.findViewById(R.id.progressBar);

        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(ListViewModel.class);

        mViewModel.getRestaurants().observe(getViewLifecycleOwner(), this::updateView);


    }

    private void updateView(List<Restaurant> restaurants) {

        if(restaurants != null){
            progressBar.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
          // ListAdapter listAdapter = new ListAdapter(getContext(), mViewModel.getRestaurants(), mViewModel.getLocation());
           // recyclerView.setAdapter(listAdapter);
        }else{
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}