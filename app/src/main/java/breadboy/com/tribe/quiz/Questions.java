package breadboy.com.tribe.quiz;

import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Questions extends AppCompatActivity {
    private TextView username;
    private int closeCount = 0;
    private LinearLayout parent;
    private ProgressDialog dialog;
    private HashMap<Integer,Integer> mark = new HashMap<>();
    private int idOffset = 4;
    private int totalScore = 0;
    private Intent history,login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        parent = (LinearLayout)findViewById(R.id.linear);
        username = (TextView)findViewById(R.id.user);
        username.setText("Welcome "+getUsername()+"!");
        history = new Intent(this,Summary.class);
        login = new Intent(this,Login.class);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Questions Please wait...");
        Bundle bd = getIntent().getExtras();


        dialog.show();

        fetchQuestions();

    }


    public void logout(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("com.quiz", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("id");
        editor.apply();
        editor.commit();
        startActivity(login);
    }

    public void viewHistory(View view){
        startActivity(history);
    }


    public Button createButton(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Button submit = new Button(this);
        submit.setLayoutParams(params);
        submit.setText("Submit");
        submit.setPadding(0,50,0,50);
        submit.setBackgroundResource(R.drawable.button_selector);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               processQuiz();
            }
        });
        return submit;
    }


    public TextView createViews(int number, String question){
        TextView hello = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hello.setLayoutParams(params);
        hello.setText("("+(number)+")   "+question);
        hello.setGravity(View.TEXT_ALIGNMENT_CENTER);
        hello.setBackgroundColor(125);
        hello.setPadding(0,50,0,50);
        hello.setTextSize(20);
        return hello;
    }

    public RadioGroup createRadioGroup(int id, String [] options){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        RadioGroup radgrp = new RadioGroup(this);
        radgrp.setLayoutParams(params);
        radgrp.setId(id+idOffset);

        radgrp.setPadding(0,0,0,40);
        for(int i=0; i<options.length; i++){
            radgrp.addView(createRadioButtons(i,options[i]));
        }
        return radgrp;
    }


    public RadioButton createRadioButtons(int id, String option){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        RadioButton rdbtn = new RadioButton(this);
        rdbtn.setLayoutParams(params);
        rdbtn.setText(option);
        rdbtn.setTextSize(15);
        rdbtn.setId(id);
        return rdbtn;
    }

    public void processQuiz(){
        System.out.println(mark);
        Set keys = mark.keySet();
        Iterator<Integer> it = keys.iterator();
        while(it.hasNext()) {
            int key = it.next();
            RadioGroup test = (RadioGroup) findViewById(key);
            int option = test.getCheckedRadioButtonId();
            if(mark.get(key) == option){
                totalScore++;
            }
        }
        postScores(totalScore,mark.size());
        //Toast.makeText(this, getUsername()+" your score is "+totalScore+"/"+mark.size(), Toast.LENGTH_SHORT).show();
        totalScore = 0;
    }


    public void postScores(final int score, final int totalScore){
        dialog.setMessage("Submitting Quiz Please wait...");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://goldshare.org/quiz/public/questions/user/"+getId();
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                startActivity(history);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errors = "Internet Connectivity Failed";
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("score",score+"");
                params.put("total_score",totalScore+"");
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


    public String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences("com.quiz", MODE_PRIVATE);
        return sharedPreferences.getString("username",null);
    }

    public String getId(){
        SharedPreferences sharedPreferences = getSharedPreferences("com.quiz", MODE_PRIVATE);
        return sharedPreferences.getString("id",null);
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

    public void fetchQuestions(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://goldshare.org/quiz/public/questions";
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
               decodeQuestions(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errors = "Internet Connectivity Failed";

            }
        }){

        };
        queue.add(request);
    }

    public void decodeQuestions(String response){

        try{
            JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    String question = jsonObject.getString("question");
                    String a = jsonObject.getString("a");
                    String b = jsonObject.getString("b");
                    String c = jsonObject.getString("c");
                    String d = jsonObject.getString("d");
                    int answer = jsonObject.getInt("answer");
                    int id = jsonObject.getInt("id");
                    String options [] = new String[]{a,b,c,d};
                    render(options,question,id);
                    mark.put(id+idOffset,answer);
                }
        }catch (JSONException jsonexp){

        }
        parent.addView(createButton());
    }

    public void render(String [] options, String question, int id){
        parent.addView(createViews(id,question));
        parent.addView(createRadioGroup(id,options));
    }

}
