package es.unex.infinitetime.ui.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentUserBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.User;
import es.unex.infinitetime.ui.login.PersistenceUser;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private long user_id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextInputEditText textView = binding.textEditUsernameUser;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InfiniteDatabase db = InfiniteDatabase.getDatabase(getContext());

        PersistenceUser persistenceUser = PersistenceUser.getInstance();
        user_id = persistenceUser.getUserId();


        AppExecutors.getInstance().diskIO().execute(() -> {
            User user = db.userDAO().getUser(user_id);

            AppExecutors.getInstance().mainThread().execute(() -> {
                binding.textEditUsernameUser.setText(user.getUsername());
                binding.textEditEmailUser.setText(user.getEmail());
                binding.textEditPasswordUser.setText(user.getPassword());
            });
        });

        binding.btnUser.setOnClickListener(v -> {

            if(binding.textEditUsernameUser.getText().toString() != null
                    && binding.textEditEmailUser.getText().toString() != null
                    && binding.textEditPasswordUser.getText().toString() != null
                    && !binding.textEditUsernameUser.getText().toString().equals("")
                    && !binding.textEditEmailUser.getText().toString().equals("")
                    && !binding.textEditPasswordUser.getText().toString().equals("")){
                AppExecutors.getInstance().diskIO().execute(() -> {

                    User user = db.userDAO().getUser(user_id);
                    user.setUsername(binding.textEditUsernameUser.getText().toString());
                    user.setEmail(binding.textEditEmailUser.getText().toString());
                    user.setPassword(binding.textEditPasswordUser.getText().toString());
                    db.userDAO().update(user);

                });
            }
        });

        binding.btnDeleteUser.setOnClickListener(v -> {


            AppExecutors.getInstance().diskIO().execute(() -> {
                User user = new User();
                user.setId(PersistenceUser.getInstance().getUserId());
                db.userDAO().delete(user);
            });
            Navigation.findNavController(v).navigate(R.id.loginFragment);
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}