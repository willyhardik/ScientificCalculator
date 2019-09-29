package com.example.scientificcalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {









    Button zero;
    Button one;
    Button two;
    Button three;
    Button four;
    Button five;
    Button six;
    Button seven;
    Button eight;
    Button nine;

    Button openingBracket;
    Button closingBracket;

    Button dot;

    Button add;
    Button sub;
    Button mul;
    Button div;

    Button equals;

    Button clear;
    Button clearExpression;





    TextView expression;
    TextView result;


    MathHandler mh = new MathHandler();

    HashSet<String> operators = new HashSet<>();
    HashMap<String, Integer> operation = new HashMap<>();
    HashMap<String, Integer> memory = new HashMap<>();
    long t = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         zero = (Button) findViewById(R.id.zero);

         one = (Button) findViewById(R.id.one);


         two = (Button) findViewById(R.id.two);
         three = (Button) findViewById(R.id.three);
         four = (Button) findViewById(R.id.four);
         five = (Button) findViewById(R.id.five);
         six = (Button) findViewById(R.id.six);
         seven = (Button) findViewById(R.id.seven);
         eight = (Button) findViewById(R.id.eight);
         nine = (Button) findViewById(R.id.nine);

         openingBracket = (Button) findViewById(R.id.openingBracket);
         closingBracket = (Button) findViewById(R.id.closingBracket);

         dot = (Button) findViewById(R.id.dot);

         add = (Button) findViewById(R.id.add);
         sub = (Button) findViewById(R.id.sub);
         mul = (Button) findViewById(R.id.mul);
         div = (Button) findViewById(R.id.div);

         equals = (Button) findViewById(R.id.equals);
         clear = (Button) findViewById(R.id.clear);
         clearExpression = (Button) findViewById(R.id.clearExpression);

         expression = (TextView) findViewById(R.id.expression);
         result = (TextView) findViewById(R.id.result);

         operators.add("+");
         operators.add("-");
         operators.add("*");
         operators.add("/");
         operators.add("(");
         operators.add(")");


         operation.put("root",0);
         operation.put("10^x",1);
         operation.put("log",2);
         operation.put("exp",3);
         operation.put("mod",4);
         operation.put("x^2",5);
         operation.put("x^y",6);
         operation.put("sin",7);
         operation.put("cos",8);
         operation.put("tan",9);
         operation.put("n!",10);


         memory.put("mc",0);
         memory.put("mr",1);
         memory.put("m+",2);
         memory.put("m-",3);
         memory.put("ms",4);

         /*
         Intent intent = new Intent(MainActivity.this, MachineLearning.class);
         startActivity(intent);
         */


    }


    public void addInExpression(View v) {

        if(operators.contains(((Button) v).getText().toString())) {
            setExpression(getExpression()+" "+((Button) v).getText().toString()+" ");
        }
        else {
            setExpression(getExpression()+((Button) v).getText().toString());
        }

    }


    public void clear(View v) {

        setExpression("");
        setResult("");

    }

    public void clearExpression(View v) {

        setExpression("");

    }

    public void backspace(View v) {

        String s = getExpression();
        if(s.length()>0) {
            s = s.substring(0, s.length() - 1);
        }

        if(s.length()>0 && s.charAt(s.length()-1) == ' ') {
            s = s.substring(0, s.length() - 1);
        }


        if(s.length()>0 && s.charAt(s.length()-1) == ' ') {
            s = s.substring(0, s.length() - 1);
        }

        setExpression(s);

    }

    public void evaluate(View v) {

        try {

            double ans = mh.evaluate(getExpression());

            if (ans - (int) ans == 0) {

                setResult(Integer.toString((int) ans));
                setExpression(Integer.toString((int) ans));

            } else {

                setResult(Double.toString(ans));
                setExpression(Double.toString(ans));

            }

        }
        catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Enter valid expression", Toast.LENGTH_LONG).show();

        }

    }


    public void getConstants(View v) {

        Toast.makeText(MainActivity.this, ((Button) v).getText().toString(), Toast.LENGTH_LONG).show();

        if(((Button) v).getText().toString().equals("PI")) {

            setExpression(getExpression()+MathHandler.PI);

        }
        else if(((Button) v).getText().toString().equals("E")) {

            setExpression(getExpression()+MathHandler.E);

        }
        else if(((Button) v).getText().toString().equals("rand")) {

            setExpression(getExpression()+MathHandler.rand());

        }

    }


    public  void compute(View v) {


        try {

            String func = ((Button) v).getText().toString();

            int funcno = operation.get(func);

            double ans = 0;

            if(!result.getText().toString().equals("")) {

                ans = Double.parseDouble(result.getText().toString());
            }

            evaluate(v);

            double y = 0;

            if(funcno ==  2 || funcno == 6) {
                y = Double.parseDouble(expression.getText().toString());
            }
            else {
                ans = Double.parseDouble(result.getText().toString());
            }

            //Toast.makeText(MainActivity.this, funcno+" "+ans+" "+y, Toast.LENGTH_LONG).show();

            double ans1 = 0;

            //Toast.makeText(MainActivity.this, Integer.toString(funcno), Toast.LENGTH_LONG).show();

            switch (funcno) {

                case 0:
                    ans1 = MathHandler.pow(ans, 0.5);
                    break;

                case 1:
                    ans1 = MathHandler.pow(10, ans);
                    break;

                case 2:
                    ans1 = MathHandler.log(ans, y);
                    break;

                case 3:
                    ans1 = MathHandler.pow(MathHandler.E, ans);
                    break;

                case 4:
                    ans1 = MathHandler.div(ans, 100);
                    break;

                case 5:
                    ans1 = MathHandler.pow(ans, 2);
                    break;

                case 6:
                    ans1 = MathHandler.pow(ans, y);
                    break;

                case 7:
                    ans1 = MathHandler.sin(ans);
                    break;

                case 8:
                    ans1 = MathHandler.cos(ans);
                    break;

                case 9:
                    ans1 = MathHandler.tan(ans);
                    break;

                case 10:
                    ans1 = MathHandler.fact((long) ans);
                    break;


                default:
                    break;

            }

            setExpression(Double.toString(ans1));
            setResult(Double.toString(ans1));

        }
        catch(Exception e) {

            //Toast.makeText(MainActivity.this, "Invalid Input "+e.toString(), Toast.LENGTH_LONG).show();

        }



    }


    public void memoryHandler(View v) {

        String m = ((Button) v).getText().toString();

        int k = memory.get(m);

        //Toast.makeText(MainActivity.this,k+"",Toast.LENGTH_LONG).show();

        try {

            switch (k) {

                case 0:
                    t = 0;
                    break;

                case 1:
                    setResult(Long.toString(t));
                    break;

                case 2:
                    t += Long.parseLong(getResult());
                    break;

                case 3:
                    t -= Long.parseLong(getResult());
                    break;

                case 4:
                    t = Long.parseLong(getResult());
                    break;

                default:
                    break;

            }

        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_LONG).show();
        }
    }

    public  void startOCR(View v) {

        Intent intent = new Intent(MainActivity.this, OCRCalculator.class);
        startActivity(intent);

    }

    public void startML(View v) {

        Intent intent = new Intent(MainActivity.this, MachineLearning.class);
        startActivity(intent);

    }


    public void setExpression(String s) {

        expression.setText(s);

    }


    public String getExpression() {

        return expression.getText().toString();

    }


    public void setResult(String s) {

        result.setText(s);

    }


    public String getResult() {

        return result.getText().toString();

    }


}
