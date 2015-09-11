package pt.ipg.mcm.app.activities;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.instances.App;

import static pt.ipg.mcm.app.instances.App.get;

public class HostUrl extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_url);

        get().getSharedPreferences(HostUrl.this, "host").getString("host", "");

        final EditText etHost = (EditText) findViewById(R.id.huEtHost);
        etHost.setText(get().getSharedPreferences(this,"host").getString("host",""));
        Button btGuardar = (Button) findViewById(R.id.huBtGuardar);
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String host = etHost.getText().toString();
                SharedPreferences.Editor editor = get().getSharedPreferences(HostUrl.this,"host").edit();
                editor.putString("host",host);
                editor.commit();
            }
        });
    }

}
