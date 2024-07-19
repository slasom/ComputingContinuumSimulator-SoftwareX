package es.unex.infinitetime.ui.login;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import es.unex.infinitetime.AppExecutors;
import es.unex.infinitetime.DrawerLocker;
import es.unex.infinitetime.databinding.FragmentLoginBinding;

import es.unex.infinitetime.R;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.persistence.User;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private Button registerButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputEditText usernameEditText = binding.textEditUsernameLogin;
        final TextInputEditText passwordEditText = binding.textEditPasswordLogin;
        final Button loginButton = binding.btnLogin;

        if(getArguments() != null){
            usernameEditText.setText(getArguments().getString("username"));
            passwordEditText.setText(getArguments().getString("password"));
        }

        PersistenceUser persistenceUser = PersistenceUser.getInstance();
        persistenceUser.deleteUserId();

        InfiniteDatabase db = InfiniteDatabase.getDatabase(getContext());

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if(!username.equals("") && !password.equals("")){
                AppExecutors.getInstance().diskIO().execute(() -> {
                    User user = db.userDAO().getUser(username);
                    if(user == null || !user.getPassword().equals(password)) {
                        Snackbar.make(v, "Usuario no encontrado o la contraseña es incorrecta", Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        persistenceUser.setUserId(user.getId());
                        persistenceUser.saveUserId();
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            ((DrawerLocker) getActivity()).setDrawerEnabled(true);
                            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_favorite);
                        });
                    }
                });
            }
            else{
                Snackbar.make(v, "Rellena todos los campos", Snackbar.LENGTH_LONG).show();
            }
        });

        registerButton = binding.btnRegister;
        registerButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("username", usernameEditText.getText().toString());
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment, bundle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}