package es.unex.infinitetime;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import es.unex.infinitetime.databinding.ActivityMainBinding;
import es.unex.infinitetime.persistence.InfiniteDatabase;
import es.unex.infinitetime.ui.login.PersistenceUser;

public class MainActivity extends AppCompatActivity implements DrawerLocker {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;

    private NavController navController;

    private Toolbar toolbar;
    private SharedPreferences mPrefs;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        drawer = binding.drawerLayout;
        setContentView(binding.getRoot());

        toolbar = binding.appBarMain.toolbar;
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        setSupportActionBar(toolbar);

        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.favorite, R.id.projects, R.id.stats,
                R.id.user, R.id.cloudFragment,
                R.id.loginFragment)
                .setOpenableLayout(drawer)
                .build();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Se obtiene la instancia de la base de datos al lanzar la aplicacion
        //Se hace al lanzar la aplicacion porque es una operacion costosa
        InfiniteDatabase.getDatabase(this);

        //Las operaciones de la BD se hacen en otro hilo y se llaman con
        //luego con la instancia de la base de datos se llama a los metodos de los DAO
        PersistenceUser persistenceUser = PersistenceUser.getInstance();
        persistenceUser.setPreferences(mPrefs);
        persistenceUser.loadUserId();

        setTheme();

        mPrefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("theme")) {
                setTheme();
            }
        });

        Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                .addOnDestinationChangedListener((controller, destination, arguments) -> {
                    if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                        setDrawerEnabled(false);
                    }
                    if(destination.getId() == R.id.settingsFragment && !PersistenceUser.getInstance().hasValidUserId()){
                        setDrawerEnabled(false);
                    }
                });

        if(savedInstanceState == null && !persistenceUser.hasValidUserId()){
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.favorite, true)
                    .build();
            navController.navigate(R.id.loginFragment, null, navOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                navController.navigate(R.id.settingsFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        //Se cierra la base de datos al cerrar la aplicacion
        InfiniteDatabase.getDatabase(this).close();
        super.onDestroy();
        binding = null;
    }

    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ?
                DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;

        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);
        if(!enabled){
            toolbar.setNavigationIcon(null);
        }
    }

    public void setTheme(){
        String theme = mPrefs.getString("theme", "Claro");
        if (theme.equals("Oscuro")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}