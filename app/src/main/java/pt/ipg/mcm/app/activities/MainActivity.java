package pt.ipg.mcm.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import pt.ipg.mcm.app.AccoesPrincipaisAdapter;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.grouped.resources.Constants;
import pt.ipg.mcm.app.grouped.resources.SincronizarResources;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView accaoListView = (ListView) findViewById(R.id.amListView);
        accaoListView.setAdapter(new AccoesPrincipaisAdapter(accaoListView.getContext()));

        accaoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, SincronizarActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, CreateEncomendaActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, MinhasEncomendasActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        deleteDatabase("padaria");
                        SharedPreferences.Editor edit = getSharedPreferences(Constants.SHARED_SYNC, MODE_PRIVATE)
                                .edit();
                        edit.putLong(Constants.SYNC_INDEX, 0);
                        edit.commit();
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, HostUrl.class);
                        startActivity(intent);
                        break;
                    default:
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainMenuAcerca) {
            Intent intent = new Intent(this, AcercaActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String login = data.getStringExtra(SincronizarResources.LOGIN_EXTRA_LABEL);
                String password = data.getStringExtra(SincronizarResources.PASSWORD_EXTRA_LABEL);
                boolean guardarPassword = data.getBooleanExtra("guardarPassword", false);
                if (guardarPassword) {
                    SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("login", login);
                    editor.putString("password", password);
                    editor.commit();
                }
                break;
        }
    }
}
