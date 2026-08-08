package com.example.lighture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    public static class ProfileData {
        public final String name;
        public final String email;
        public final boolean isPremium;
        public final String recipesTried;
        public final String itemsSaved;
        public final String moneySaved;
        public final String wasteAvoided;

        public ProfileData(String name, String email, boolean isPremium, String recipesTried, String itemsSaved, String moneySaved, String wasteAvoided) {
            this.name = name;
            this.email = email;
            this.isPremium = isPremium;
            this.recipesTried = recipesTried;
            this.itemsSaved = itemsSaved;
            this.moneySaved = moneySaved;
            this.wasteAvoided = wasteAvoided;
        }
    }

    private final MutableLiveData<ProfileData> profile = new MutableLiveData<>();

    public ProfileViewModel() {
        // Mock data
        profile.setValue(new ProfileData(
                "Clement Phoshoko",
                "clement.pho@gmail.com",
                true,
                "24",
                "12",
                "R356",
                "8.2 kg"
        ));
    }

    public LiveData<ProfileData> getProfile() {
        return profile;
    }
}
