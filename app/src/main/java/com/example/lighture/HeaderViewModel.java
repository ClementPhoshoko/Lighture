package com.example.lighture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HeaderViewModel extends ViewModel {

    public static class HeaderState {
        public final String title;
        public final String subtitle;
        public final boolean showAction;
        public final Integer actionIconRes;
        public final String actionText;
        public final boolean showBadge;

        public HeaderState(String title, String subtitle, boolean showAction, Integer actionIconRes, String actionText, boolean showBadge) {
            this.title = title;
            this.subtitle = subtitle;
            this.showAction = showAction;
            this.actionIconRes = actionIconRes;
            this.actionText = actionText;
            this.showBadge = showBadge;
        }

        public static HeaderState hidden() {
            return new HeaderState(null, null, false, null, null, false);
        }
    }

    private final MutableLiveData<HeaderState> _state = new MutableLiveData<>(HeaderState.hidden());
    public LiveData<HeaderState> state = _state;

    private final MutableLiveData<Boolean> _actionClicked = new MutableLiveData<>();
    public LiveData<Boolean> actionClicked = _actionClicked;

    public void updateState(HeaderState newState) {
        _state.setValue(newState);
    }

    public void onActionClick() {
        _actionClicked.setValue(true);
    }

    public void consumeActionClick() {
        _actionClicked.setValue(false);
    }
}
