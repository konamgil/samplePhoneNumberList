package com.letscombintest.sql2.contenttest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int REQUEST_CALL_PHONE = 200;
    MainActivity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    } // end of onCreate

    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // 컨텐트 프로바이더 (ContentProvider)
            // 어플리케이션 내에서만 사용할 수 있는 데이터를 공유하기위한
            // 방법으로 안드로이드의 4대 컴포넌트 중 하나이다
            // 폰에 저장되있는 전화번호부를 읽어보기(권한 필요)
            // AndroidManifest.xml
            ArrayList<String> arrayList = new ArrayList<String>();
            final ArrayList<String> phoneNumList = new ArrayList<String>();
            ArrayAdapter<String> arrayAdapter;
//            TextView tv = (TextView) findViewById(R.id.textView2);
            ListView lvCall = (ListView)findViewById(R.id.lvCall);
            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds
                            .Phone.CONTENT_URI,  // 조회할 컬럼명
                    null, // 조회할 컬럼명
                    null, // 조건 절
                    null, // 조건절의 파라미터
                    null);// 정렬 방향

            String str = ""; // 출력할 내용을 저장할 변수
            c.moveToFirst(); // 커서를 처음위치로 이동시킴
            int count = 0;
            do {
                String name = c.getString
                        (c.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = c.getString
                        (c.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER));
                count++;
                str = count + " 이름 : " + name + "폰번호 : " + phoneNumber;
                arrayList.add(str);
                phoneNumList.add(phoneNumber);
            } while (c.moveToNext());//데이터가 없을 때까지반복

            arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
            lvCall.setAdapter(arrayAdapter);

            lvCall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    int checkPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                that,
                                new String[]{Manifest.permission.CALL_PHONE},
                                REQUEST_CALL_PHONE);
                    } else {
                        Uri call = Uri.parse("tel:"+phoneNumList.get(position));
                        Intent i = new Intent(Intent.ACTION_DIAL);
                        i.setData(call);
                        startActivity(i);
                    }
                }
            });
        }

    }
    private void callNum(){

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                init();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


