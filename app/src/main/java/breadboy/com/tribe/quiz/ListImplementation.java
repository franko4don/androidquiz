package breadboy.com.tribe.quiz;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListImplementation extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_implementation);
        listView = (ListView)findViewById(R.id.messages);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        Messages.getMessage());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =""+listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),item,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendNotification(View view){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.fifth);
                mBuilder.setContentTitle("Quiz Note");
                mBuilder.setContentText("You are about to take quiz");
        mBuilder.setSound(alarmSound);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify();
                mNotificationManager.notify(001, mBuilder.build());

        Toast.makeText(getApplicationContext(),"I was here",Toast.LENGTH_SHORT).show();
    }
}
