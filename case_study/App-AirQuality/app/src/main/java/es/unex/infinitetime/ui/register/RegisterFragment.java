package es.unex.infinitetime.ui.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.R;
import es.unex.infinitetime.databinding.FragmentRegisterBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.User;


public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    public RegisterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("Debug", "onViewCreated RegisterFragment");

        Button btnRegister = binding.btnRegister;

        TextInputEditText tvUsername = binding.textEditUsernameRegister;
        TextInputEditText tvPassword = binding.textEditPasswordRegister;
        TextInputEditText tvEmail = binding.textEditEmailRegister;

        tvUsername.setText(getArguments().getString("username"));

        InfiniteDatabase db = InfiniteDatabase.getDatabase(getContext());

        btnRegister.setOnClickListener(v1 -> {

            User user = new User();

            user.setUsername(tvUsername.getText().toString());
            user.setPassword(tvPassword.getText().toString());
            user.setEmail(tvEmail.getText().toString());

            if(!user.getUsername().equals("") && !user.getPassword().equals("") && !user.getEmail().equals("")) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    if(db.userDAO().getUser(user.getUsername()) != null) {
                        Snackbar.make(v1, "El usuario ya existe", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        db.userDAO().insert(user);
                        Bundle bundle = new Bundle();
                        bundle.putString("username", user.getUsername());
                        bundle.putString("password", user.getPassword());
                        AppExecutors.getInstance().mainThread().execute(() -> Navigation
                                .findNavController(v1)
                                .navigate(R.id.action_registerFragment_to_loginFragment, bundle));
                    }
                });
            }
            else{
                Snackbar.make(v1, "Rellena todos los campos", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}