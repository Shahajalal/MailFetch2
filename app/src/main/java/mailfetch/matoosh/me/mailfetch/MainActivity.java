package mailfetch.matoosh.me.mailfetch;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private TextView address;
    private TextView port;
    private TextView user;
    private TextView pass;
    private Switch secure;

    private RadioButton imapRadio;
    private RadioButton pop3Radio;

    public static final String CONNECTION_PASS = "mailfetch.matoosh.me.mailfetch.connectionpass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.address = findViewById(R.id.address_field);
        this.port = findViewById(R.id.port_field);
        this.user = findViewById(R.id.email_field);
        this.pass = findViewById(R.id.password_field);
        this.secure = findViewById(R.id.secure_switch);
        this.imapRadio = findViewById(R.id.imap_radio);
        this.pop3Radio = findViewById(R.id.pop3_radio);
    }

    public void onEmailTypeChosen(View v) {
        RadioButton btn = (RadioButton) v;

        if(btn == imapRadio) {
            pop3Radio.setChecked(false);
        } else {
            imapRadio.setChecked(false);
        }
    }

    public void onConnectClicked(View v) {
        //validates every field.
        boolean needsFix = false;
        if(this.address.getText() == null || this.address.getText() == "")  {
            this.address.setError("This field is required!");
            needsFix = true;
        }
        if(this.port.getText() == null || this.port.getText() == "") {
            this.port.setError("This field is required!");
            needsFix = true;
        }
        if(this.user.getText() == null || this.user.getText() == "") {
            this.user.setError("This field is required!");
            needsFix = true;
        }
        if(this.pass.getText() == null || this.pass.getText() == "") {
            this.pass.setError("This field is required!");
            needsFix = true;
        }
        if(needsFix) return;

        String protocol = "";
        if(pop3Radio.isChecked()) {
            protocol = "pop3";
        } else if (imapRadio.isChecked()){
            protocol = "imap";
        }

        if(secure.isChecked()) {
            protocol = protocol + "s";
        }

        //starting the mailslist activity.
        Intent intent = new Intent(this, EmailsList.class);
        String[] message = new String[] {
                address.getText() + "",
                port.getText() + "",
                user.getText() + "",
                pass.getText() + "",
                protocol
        };
        intent.putExtra(CONNECTION_PASS, message);
        startActivity(intent);
    }
}
