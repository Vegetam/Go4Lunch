package com.francescomalagrino.go4lunch.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.francescomalagrino.go4lunch.R;
import com.francescomalagrino.go4lunch.view.RestaurantDetailActivity;
import com.francescomalagrino.go4lunch.data.User;
import com.francescomalagrino.go4lunch.repo.UserRepository;
import com.francescomalagrino.go4lunch.utils.MyDividerItemDecoration;
import com.francescomalagrino.go4lunch.repo.ListOfWorkmatesAdapter;
import io.reactivex.annotations.Nullable;

public class WorkmatesFragment extends Fragment {
    private ListOfWorkmatesAdapter adapter;
    private RecyclerView recyclerView;
    private String PLACEIDRESTO = "resto_place_id";

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_workmates, container, false);
        recyclerView = view.findViewById(R.id.fragment_workmates_recyclerview);

        setupRecyclerView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupRecyclerView() {

        Query allUsers= UserRepository.getAllUsers()
                .orderBy("restoTodayName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(allUsers, User.class)
                .build();

        adapter = new ListOfWorkmatesAdapter(options, Glide.with(recyclerView));
        recyclerView.setHasFixedSize(true); // for performances reasons
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);

        // Add horizontal separators
        MyDividerItemDecoration mDividerItemDecoration = new MyDividerItemDecoration(recyclerView.getContext());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        // on click items actions
        adapter.setOnItemClickListener(new ListOfWorkmatesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    String restoId;
                    if (user != null) {
                        restoId = user.getRestoToday();


                        Intent WVIntent = new Intent(getContext(), RestaurantDetailActivity.class);
                        WVIntent.putExtra(PLACEIDRESTO, restoId);
                        startActivity(WVIntent);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}