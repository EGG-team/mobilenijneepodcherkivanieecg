package go.egg.mobilenijneepodcherkivanieecg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
//jj
    private Button login_button;
    private Button registration_button;
    private Button ofline_button;
    private EditText login_text;
    private EditText password_text;


    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    private final String LOGIN_URL= "WWW.LENINGRAD....RU";
    private final int LOGIN_CODE = 1;
    private final int REGISTRATION_CODE = 2;
    private final int ZABYL_PASS = 3;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        login_text = findViewById(R.id.login_edit);
        password_text = findViewById(R.id.password_edit);
        login_button = findViewById(R.id.login_button);
        registration_button = findViewById(R.id.registration_button);
        ofline_button = findViewById(R.id.offline_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestToServer(LOGIN_CODE);
                //okhtttp
            }
        });
        registration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //okhttp
            }
        });

        ofline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start other activity
            }
        });

    }

    private String LoginRequestConstr(){
        //Конструктор отправки логина
        return "";
    }

    private void RequestToServer(int Number_requestion){

        switch (Number_requestion){
            case LOGIN_CODE:
                Request request = new Request.Builder()
                        .url(LoginRequestConstr())
                        .build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        //
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if(!response.isSuccessful()){

                        }
                        //Ответ от сервера и в случае успеха, смена определенной статичной переменной
                        //и переход на следующую активити


                    }
                });



        }





    }
}
