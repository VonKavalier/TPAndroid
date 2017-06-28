package iut.paci.noelcommunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGS_PSEUDO = "login",
            LOGS_MDP = "password";

    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button submit = (Button) findViewById(R.id.butConnexion);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ipPseudo = (EditText) findViewById(R.id.ipPseudo);
                EditText ipMdp = (EditText) findViewById(R.id.ipMdp);

                String pseudo = ipPseudo.getText().toString();
                String mdp = ipMdp.getText().toString();

                isConnected = connecter(pseudo, mdp);
                if (isConnected) {
                    notifier(R.string.notifConnecte, true);
                    Intent intent = new Intent(LoginActivity.this, DistrictActivity.class);

                    Bundle extra = new Bundle();
                    extra.putString("pseudo", pseudo);
                    extra.putString("mdp", mdp);

                    intent.putExtras(extra);

                    startActivity(intent);
                }
            }
        });

    }

    private boolean connecter(String pseudo, String mdp) {
        if (pseudo.isEmpty() || mdp.isEmpty()) {

            return erreur(R.string.notifConErr1);
        }

        if (!pseudo.equals(LOGS_PSEUDO)) {
            return erreur(R.string.notifConErr2);
        }

        if (!mdp.equals(LOGS_MDP)) {
            return erreur(R.string.notifConErr3);
        }

        return true;
    }

    private boolean erreur() {
        return erreur(R.string.notifConErr0);

    }

    private boolean erreur(int text) {
        notifier(text, false);
        return false;
    }

    private void notifier(int text, boolean longDuration) {
        Toast t = Toast.makeText(this, getString(text), longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);

        t.show();
        System.out.println("Notif: " + getString(text));
    }
}
