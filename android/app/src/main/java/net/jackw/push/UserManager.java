package net.jackw.push;

import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {

    private static final UserManager instance = new UserManager();

    private UserManager() {}

    public static UserManager getInstance() {
        return instance;
    }

    @Nullable
    public String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }
}
