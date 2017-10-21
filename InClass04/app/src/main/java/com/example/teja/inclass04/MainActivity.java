package com.example.teja.inclass04;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    ExecutorService threadPool;
    String[] passwords = new String[5];
    Handler handler;
    ProgressDialog progressDialog,progressDialog2;
    TextView textView;
    String[] items;
    String name;
    String dept;
    int age;
    int zip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Generating Passwords");
        progressDialog2.setMax(100);
        progressDialog2.setCancelable(false);
        progressDialog2.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        items = new String[5];
        textView = (TextView) findViewById(R.id.password);
        Button asyncButton = (Button) findViewById(R.id.async);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch(message.what) {
                    case PasswordGen.STATUS_START:
                        progressDialog2.show();
                        break;
                    case PasswordGen.STATUS_STEP:
                        progressDialog2.setProgress((Integer)message.obj);
                        break;
                    case PasswordGen.STATUS_DONE:
                        progressDialog2.dismiss();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Choose a password")
                                .setItems(passwords,new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TextView textView = (TextView)findViewById(R.id.password);
                                        textView.setText(passwords[i]);
                                    }
                                });
                        builder.show();
                        break;
                }
                return false;
            }
        });
        threadPool = Executors.newFixedThreadPool(2);
        findViewById(R.id.thread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name =((EditText) findViewById(R.id.nameEditText)).getText().toString();
                dept =((EditText) findViewById(R.id.deptEditText)).getText().toString();
                age =Integer.parseInt(((EditText) findViewById(R.id.ageEditText)).getText().toString());
                zip =Integer.parseInt(((EditText) findViewById(R.id.zipEditText)).getText().toString());
                if (!name.equals("") && !dept.equals("") && !((EditText) findViewById(R.id.ageEditText)).getText().toString().equals("") && !((EditText) findViewById(R.id.zipEditText)).getText().toString().equals("")) {
                    threadPool.execute(new PasswordGen());
                }
            }
        });
        asyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameText = (EditText) findViewById(R.id.nameEditText);
                EditText deptText = (EditText) findViewById(R.id.deptEditText);
                EditText ageText = (EditText) findViewById(R.id.ageEditText);
                EditText zipText = (EditText) findViewById(R.id.zipEditText);
                String name = nameText.getText().toString();
                String dept = deptText.getText().toString();
                String age = ageText.getText().toString();
                String zip = zipText.getText().toString();
                String[] stringArray;
                stringArray = new String[]{name, dept, age, zip};
                // System.out.print("The string array is ", stringArray);
                System.out.println("Hello");
                new DoAsyncTask().execute(stringArray);
            }
        });
    }

    class PasswordGen implements Runnable{

        static final int STATUS_START = 0x00;
        static final int STATUS_STEP = 0x01;
        static final int STATUS_DONE = 0x02;
        @Override
        public void run() {
            Message message = new Message();
            message.what = STATUS_START;
            handler.sendMessage(message);

            for(int i =0;i<5;i++){
                message = new Message();
                message.what = STATUS_STEP;
                message.obj = i*20;
                handler.sendMessage(message);
                passwords[i] = Util.getPassword(name,dept,age,zip);
            }
            message = new Message();
            message.what = STATUS_DONE;
            handler.sendMessage(message);
        }
    }

    public void clear(View view){
        ((TextView)findViewById(R.id.password)).setText("");
        ((EditText)findViewById(R.id.nameEditText)).setText("");
        ((EditText)findViewById(R.id.deptEditText)).setText("");
        ((EditText)findViewById(R.id.ageEditText)).setText("");
        ((EditText)findViewById(R.id.zipEditText)).setText("");
    }
    public void close(View view){
        finish();
    }
    public class DoAsyncTask extends AsyncTask<String[], Integer, Void>{

        @Override
        protected Void doInBackground(String[]... strings) {
            EditText nameText = (EditText) findViewById(R.id.nameEditText);
            EditText deptText = (EditText) findViewById(R.id.deptEditText);
            EditText ageText = (EditText) findViewById(R.id.ageEditText);
            EditText zipText = (EditText) findViewById(R.id.zipEditText);
            String name1 = nameText.getText().toString();
            String dept1 = deptText.getText().toString();
            String age1 = ageText.getText().toString();
            String zip1 = zipText.getText().toString();
            int result = Integer.parseInt(String.valueOf(age));
            int result2 = Integer.parseInt(String.valueOf(zip));
            if (!name1.equals("") && !dept1.equals("")) {
                for (int j = 0; j < 5; j++) {
                    items[j] = Util.getPassword(name1, dept1, result, result2);
                    publishProgress(j * 20);
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setMessage("Generating Passwords");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose a password")
                    .setItems(items,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Log.d("demo","Selected "+ items[i]);
                            textView.setText(items[i]);

                        }
                    });
            builder.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }
    }
}
