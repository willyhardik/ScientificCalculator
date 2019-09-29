package com.example.scientificcalculator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class OCRCalculator extends AppCompatActivity {


    ImageView imageView;
    TextView textView;
    TextView result1;

    public static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrcalculator);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        result1 = (TextView) findViewById(R.id.result1);

    }

    public void getTextFromImage(View v) {

        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(imageTakeIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Bundle extras = data.getExtras();

        Bitmap bitmap = (Bitmap) extras.get("data");
        imageView.setImageBitmap(bitmap);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()) {

            Toast.makeText(OCRCalculator.this, "Could not get the Text", Toast.LENGTH_LONG).show();

        }
        else {

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            StringBuilder sb = new StringBuilder();

            for(int i=0;i<items.size();i++) {

                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");

            }

            String expression = sb.toString();

            StringBuilder sb1 = new StringBuilder();

            for(int i=0;i<expression.length();i++) {

                if(expression.charAt(i) == '+' || expression.charAt(i) == '-' || expression.charAt(i) == '*' || expression.charAt(i) == '/' || expression.charAt(i) == '(' || expression.charAt(i) == ')') {

                    sb1.append(" "+expression.charAt(i)+" ");

                }
                else {

                    sb1.append(Character.toString(expression.charAt(i)));
                }

            }


            Toast.makeText(OCRCalculator.this, sb1.toString(), Toast.LENGTH_LONG).show();

            textView.setText(sb1.toString());

        }



    }



    public void evaluate(View v) {

        String expression = textView.getText().toString();


        try {

            double ans = MathHandler.evaluate(expression);

            result1.setText("Answer is :- "+ans);

        }
        catch (Exception e) {

            Toast.makeText(OCRCalculator.this, "Invalid Input", Toast.LENGTH_LONG).show();

        }

    }


}
