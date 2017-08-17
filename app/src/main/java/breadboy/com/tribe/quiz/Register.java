package breadboy.com.tribe.quiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class Register extends AppCompatActivity {
    private String username,password,password_confirmation;
    private EditText usernameEdit, passwordEdit, password_confirmationEdit;
    private TextView errors;
    private Button submitButton,login_link;
    private Intent login;
    private Intent question;
    private  int closeCount = 0;
    private ProgressDialog dialog;
    private String registeredUsername;
    private int registeredId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameEdit =(EditText)findViewById(R.id.username);
        passwordEdit = (EditText)findViewById(R.id.password);
        login = new Intent(this,Login.class);
        question = new Intent(this,Questions.class);
        password_confirmationEdit = (EditText)findViewById(R.id.password_confirmation);
        errors = (TextView)findViewById(R.id.error);
        submitButton = (Button)findViewById(R.id.submit_button);
        login_link = (Button)findViewById(R.id.login_link);


        if(retrieve()){
            toQuestions();
        }


    }


    public void toQuestions(){
        startActivity(question);
        finish();
    }


    public void register(){
        username = usernameEdit.getText().toString().trim();
        password = passwordEdit.getText().toString().trim();
        password_confirmation = password_confirmationEdit.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            usernameEdit.setError("Username Cannot be empty");
            return;
        }

        if(TextUtils.isEmpty(password)){
            passwordEdit.setError("Password Must not be empty");
            return;
        }

        if(TextUtils.isEmpty(password_confirmation)){
            password_confirmationEdit.setError("Confirm Password Must not be empty");
            return;
        }
        errors.setText("");
        disableFields();
        dialog = ProgressDialog.show(this, "","Logging in Please wait...", true);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://goldshare.org/quiz/public/register";
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String respond = validateMessage(response);
                if(respond.equals("successful")){
                    toQuestions();
                }else{
                    dialog.dismiss();
                    errors.setText(respond);
                    enableFields();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                enableFields();
                errors.setText("Internet Connection Failed Try Again");
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",username);
                params.put("password",password);
                params.put("password_confirmation",password_confirmation);
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


    public void registerSubmit(View view){
        register();
    }

    public void disableFields(){
        submitButton.setEnabled(false);
        usernameEdit.setEnabled(false);
        password_confirmationEdit.setEnabled(false);
        passwordEdit.setEnabled(false);
        login_link.setEnabled(false);
    }

    public void enableFields(){
        submitButton.setEnabled(true);
        usernameEdit.setEnabled(true);
        password_confirmationEdit.setEnabled(true);
        passwordEdit.setEnabled(true);
        login_link.setEnabled(true);
    }

    public void toLogin(View view){
        startActivity(login);
        finish();
    }


    public String validateMessage(String message){
        String allErrors = "";
        try {
            JSONObject json = new JSONObject(message);
            if(json.has("errors")){
                JSONObject next1 = json.getJSONObject("errors");
                if(next1.has("username")){
                    JSONArray username_errors = next1.getJSONArray("username");
                    for(int i = 0; i < username_errors.length(); i++){
                        allErrors+=username_errors.getString(i)+"\n\n";
                    }
                }
                if(next1.has("password")){
                    JSONArray username_errors = next1.getJSONArray("password");
                    for(int i = 0; i < username_errors.length(); i++){
                        allErrors+=username_errors.getString(i)+"\n\n";
                    }
                }

                if(next1.has("password_confirmation")){
                    JSONArray username_errors = next1.getJSONArray("password_confirmation");
                    for(int i = 0; i < username_errors.length(); i++){
                        allErrors+=username_errors.getString(i)+"\n\n";
                    }
                }

            }else{
                JSONObject details = new JSONObject(message);
                registeredUsername = details.getString("username");
                registeredId = details.getInt("id");
                store(registeredUsername,registeredId);
                allErrors = "successful";
            }

        }catch (JSONException exjson){
            System.out.println(exjson);
        }
        return allErrors;
    }

    public boolean retrieve(){
        SharedPreferences sharedPreferences = getSharedPreferences("com.quiz", MODE_PRIVATE);

        if(sharedPreferences.getString("username",null) != null){
            return true;
        }else{
            return false;
        }


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


    public void store(String username, int id) {
        SharedPreferences sharedPreferences = getSharedPreferences("com.quiz", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("id",id+"");
        editor.apply();
        editor.commit();
        toQuestions();
    }
}
