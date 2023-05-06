package com.francescomalagrino.go4lunch.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.data.Restaurant;
import com.francescomalagrino.go4lunch.data.nearby.Result;
import com.francescomalagrino.go4lunch.ui.ViewModelFactory;

import java.util.List;

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
            ListAdapter listAdapter = new ListAdapter(getContext(), restaurants, mViewModel.getLocation());
            recyclerView.setAdapter(listAdapter);
        }else{
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}