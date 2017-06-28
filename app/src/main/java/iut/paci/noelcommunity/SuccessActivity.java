package iut.paci.noelcommunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();

        String pseudo = extra.getString("pseudo");
        String mdp = extra.getString("mdp");

        String strPseudo = getString(R.string.district2, pseudo);
        String strMdp = getString(R.string.district3, mdp);

        TextView tvPseudo = (TextView) findViewById(R.id.tvPseudo);
        TextView tvMdp = (TextView) findViewById(R.id.tvMdp);

        tvPseudo.setText(strPseudo);
        tvMdp.setText(strMdp);
    }
}
