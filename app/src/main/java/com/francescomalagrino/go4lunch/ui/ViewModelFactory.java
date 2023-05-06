package com.francescomalagrino.go4lunch.ui;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.francescomalagrino.go4lunch.repo.ChatRepository;
import com.francescomalagrino.go4lunch.repo.RestaurantRepository;
import com.francescomalagrino.go4lunch.repo.UserRepository;
import com.francescomalagrino.go4lunch.ui.details.DetailsViewModel;
import com.francescomalagrino.go4lunch.ui.list.ListViewModel;
import com.francescomalagrino.go4lunch.ui.lunch.LunchViewModel;
import com.francescomalagrino.go4lunch.ui.main.MainViewModel;
import com.francescomalagrino.go4lunch.ui.map.MapsViewModel;
import com.francescomalagrino.go4lunch.ui.message.ChatViewModel;
import com.francescomalagrino.go4lunch.ui.settings.SettingsViewModel;
import com.francescomalagrino.go4lunch.ui.workmates.WorkmatesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory{


    private final UserRepository userSource;
    private final RestaurantRepository restaurantSource;
    private final ChatRepository chatSource;
    private final Executor executor;
    private static volatile ViewModelFactory factory;

    public static ViewModelFactory getInstance(Context context) {

        if (factory == null) {

            synchronized (ViewModelFactory.class) {

                if (factory == null) {
                    factory = new ViewModelFactory(context);
                }
            }
        }
        return factory;
    }

    public ViewModelFactory(Context context) {
        this.userSource = new UserRepository().getInstance();
        this.chatSource = new ChatRepository().getInstance();
        this.restaurantSource = new RestaurantRepository().getInstance();
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    @NotNull
    public <T extends ViewModel>  T create(Class<T> modelClass) {

        if (modelClass.isAssignableFrom(ListViewModel.class)) {
            return (T) new ListViewModel(userSource, restaurantSource);
        }

        if (modelClass.isAssignableFrom(WorkmatesViewModel.class)) {
            return (T) new WorkmatesViewModel(userSource);
        }

        if (modelClass.isAssignableFrom(MapsViewModel.class)) {
            return (T) new MapsViewModel(userSource, restaurantSource, executor);
        }

        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(userSource);
        }

        if (modelClass.isAssignableFrom(LunchViewModel.class)) {
            return (T) new LunchViewModel(userSource);
        }

        if (modelClass.isAssignableFrom(DetailsViewModel.class)) {
            return (T) new DetailsViewModel(userSource, restaurantSource, executor);
        }

        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(userSource);
        }

        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(userSource, chatSource);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
