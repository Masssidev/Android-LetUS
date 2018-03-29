package org.androidtown.wantyou;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateNoticeActivity extends AppCompatActivity implements Runnable {
    private ArrayAdapter adapter;
    private Spinner spinner;
    static final int REQUEST_CODE = 1;
    private int TAKE_CAMERA = 1; // 카메라 리턴 코드값 설정
    private int TAKE_GALLERY = 2; // 앨범선택에 대한 리턴 코드값 설정
    private Button createFairButton;
    private EditText titleText, contentText, startDate, endDate;
    private ImageView fileImage;
    private String message, success;
    private Uri capturedUri;

    private void updateLabel1() {
        String myFormat = "yy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        startDate.setText(sdf.format(myCalendar.getTime()));// TextView 에 현재 시간 문자열 할당
    }

    private void updateLabel2() {
        String myFormat = "yy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        endDate.setText(sdf.format(myCalendar.getTime()));
    }

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        titleText=(EditText)findViewById(R.id.titleText);
        contentText=(EditText)findViewById(R.id.contentText);
        startDate=(EditText)findViewById(R.id.startdate);
        endDate = (EditText)findViewById(R.id.enddate);
        createFairButton = (Button)findViewById(R.id.createFairButton);
        fileImage = (ImageView)findViewById(R.id.imgView);

        createFairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread th = new Thread(CreateNoticeActivity.this);
                th.start();
            }
        });

        fileImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedUri);
                        setResult(RESULT_OK, intent);
                        startActivityForResult(intent, TAKE_CAMERA);
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, TAKE_GALLERY);
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(CreateNoticeActivity.this)
                        .setTitle("업로드 할 이미지 선택")
                        .setPositiveButton("앨범선택", albumListener)
                        .setNeutralButton("취소", cancelListener)
                        .setNegativeButton("사진촬영", cameraListener)
                        .show();
            }
        });

//        카테고리 스피너
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

//        시작날짜 datepicker
        startDate = (EditText) findViewById(R.id.startdate);
        updateLabel1();
        startDate.setFocusable(false);
        startDate.setClickable(false);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel1();
            }

        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CreateNoticeActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        끝날짜 datepicker
        endDate = (EditText) findViewById(R.id.enddate);
        updateLabel2();
        endDate.setFocusable(false);
        endDate.setClickable(false);

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
            }

        };

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CreateNoticeActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        내용입력칸 스크롤
        EditText content = (EditText) findViewById(R.id.contentText);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
//        checkPermission();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_CAMERA) {
                if (data != null) {
                    Bitmap thumbnail1 = (Bitmap) data.getExtras().get("data");
                    capturedUri = getImageUri(getApplicationContext(), thumbnail1);
                    if (thumbnail1 != null) {
                        fileImage.setImageBitmap(thumbnail1);
                    }
                }

            } else if (requestCode == TAKE_GALLERY) {
                if (data != null) {

                    Uri thumbnail2 = data.getData();
                    capturedUri = thumbnail2;
                    if (thumbnail2 != null) {
                        fileImage.setImageURI(thumbnail2);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        String url = "http://172.30.5.56:8080/android/post";
        String category = spinner.getSelectedItem().toString();
        String title = titleText.getText().toString();
        String start = startDate.getText().toString();
        String end = endDate.getText().toString();
        String board="2";
        File file=null;

        if(capturedUri!=null) {
            file = new File(getRealPathFromURI(capturedUri));
        }
        String text = contentText.getText().toString();

        try {
            HttpPost httpPost = new HttpPost(url);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            entity.addPart("title", new StringBody(title, Charset.forName("UTF-8")));
            entity.addPart("category", new StringBody(category, Charset.forName("UTF-8")));
            entity.addPart("start", new StringBody(start, Charset.forName("UTF-8")));
            entity.addPart("end", new StringBody(end, Charset.forName("UTF-8")));
            entity.addPart("text", new StringBody(text, Charset.forName("UTF-8")));
            entity.addPart("board", new StringBody(board, Charset.forName("UTF-8")));

            if(file!=null) {
                entity.addPart("file", new FileBody(file));
            }
            httpPost.setEntity(entity);

            HttpResponse response = org.androidtown.wantyou.HttpClient.getInstance().execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);

            message = obj.getString("message");
            success = obj.getString("success");

            if (success.equals("yes")) {
                handler.sendEmptyMessage(1);
            } else {
                handler.sendEmptyMessage(2);
            }

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(CreateNoticeActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2:
                    Toast.makeText(CreateNoticeActivity.this, message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

//    private void checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            List<String> permissoinList = new ArrayList<>();
//            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissoinList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//
//            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissoinList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//
//            if (permissoinList.size() != 0) {
//                String[] permissioinArray = new String[permissoinList.size()];
//                permissioinArray = permissoinList.toArray(permissioinArray);
//                requestPermissions(permissioinArray,
//                        REQUEST_CODE);
//            }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length < 0
//                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                (new Handler()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "퍼미션이안됐어", Toast.LENGTH_LONG).show();
//                    }
//                }, 200);
//            }
//        }
//    }
}





