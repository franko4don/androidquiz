package breadboy.com.tribe.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Login extends AppCompatActivity {
    private String username,password;
    private EditText usernameEdit, passwordEdit;
    private TextView errors;
    private Button loginButton,register_link;
    private Intent question,register;
    private int closeCount = 0;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEdit = (EditText)findViewById(R.id.login_username);
        passwordEdit = (EditText)findViewById(R.id.login_password);
        loginButton = (Button)findViewById(R.id.login_button);
        register_link = (Button)findViewById(R.id.register_link);
        errors = (TextView)findViewById(R.id.error1);
        question = new Intent(this,Questions.class);
        register = new Intent(this,Register.class);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Logging in Please Wait ...");

        if(retrieve()){
            toQuestions();
        }

    }

    public void authenticate(){
        username = usernameEdit.getText().toString().trim();
        password = passwordEdit.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            usernameEdit.setError("Username Cannot be empty");
            return;
        }

        if(TextUtils.isEmpty(password)){
            passwordEdit.setError("Password Must not be empty");
            return;
        }

        disableFields();
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://goldshare.org/quiz/public/login";
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                validateMessage(response);

                enableFields();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                enableFields();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",username);
                params.put("password",password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(request);
    }


    public void validateMessage(String message){

        String allErrors = "";
        try {
           JSONObject json = new JSONObject(message);
            if(json.has("id")){
                store(json.getString("username"),json.getInt("id"));
            }else{

                allErrors+=json.getString("auth");

                errors.setText(allErrors);

            }

        }catch (JSONException exjson){
            System.out.println(exjson);
        }
        dialog.dismiss();
    }



    public void submitLogin(View view){
        errors.setText("");
        authenticate();
    }

    public void disableFields(){
        usernameEdit.setEnabled(false);
        passwordEdit.setEnabled(false);
        loginButton.setEnabled(false);
        register_link.setEnabled(false);
    }

    public void enableFields(){
        usernameEdit.setEnabled(true);
        loginButton.setEnabled(true);
        passwordEdit.setEnabled(true);
        register_link.setEnabled(true);
    }

    public void toQuestions(){
        startActivity(question);
        finish();
    }

    public void store(String username, int id) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.quiz", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("id",id+"");
        editor.apply();
        editor.commit();
        toQuestions();
    }

    public boolean retrieve(){
        SharedPreferences sharedPreferences = getSharedPreferences("com.quiz", MODE_PRIVATE);
        if(sharedPreferences.getString("username",null) != null){
            return true;
        }else{
            return false;
        }


    }

    public void toRegister(View view){
        startActivity(register);
    }

    @Override
    public void onBackPressed(){
        if(closeCount < 1){
            Toast.makeText(this,"Press Back Again To Exit",Toast.LENGTH_SHORT).show();
            closeCount++;
        }else{
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

}
