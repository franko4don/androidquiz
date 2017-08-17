package breadboy.com.tribe.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Summary extends AppCompatActivity {
    private LinearLayout parent;
    private ProgressDialog dialog;
    private Intent question;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        parent = (LinearLayout)findViewById(R.id.linear);
        dialog = ProgressDialog.show(this, "","Loading history Please wait...", true);
        question = new Intent(this,Questions.class);
        fetchHistory();

    }


    public TextView createMessageTextView(String message){
        TextView hello = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hello.setLayoutParams(params);
        hello.setText(message);
        hello.setGravity(View.TEXT_ALIGNMENT_CENTER);
        hello.setBackgroundColor(125);
        hello.setTextColor(Color.red(200));
        hello.setTextSize(14);
        return hello;
    }

    public TextView createTextViews(int number, int score, int totalScore, String date){
        TextView hello = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hello.setLayoutParams(params);
        hello.setText("("+(number)+")   score = "+score+"/"+totalScore+"        "+date);
        hello.setGravity(View.TEXT_ALIGNMENT_CENTER);
        hello.setBackgroundColor(125);
        hello.setPadding(0,30,0,20);
        hello.setTextSize(14);
        return hello;
    }


    public void fetchHistory(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://goldshare.org/quiz/public/questions/user/"+getId();
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                decodeHistory(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errors = "Internet Connectivity Failed";
                dialog.dismiss();
                parent.addView(createMessageTextView("Internet Connection Failed Try again"));
            }
        }){

        };
        queue.add(request);
    }


    public void decodeHistory(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                parent.addView(createTextViews(i+1, jsonObject.getInt("score"), jsonObject.getInt("total_score"),jsonObject.getString("created_at")));
            }
        }catch (JSONException jsonexp){

        }
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
        startActivity(question);
    }
}
