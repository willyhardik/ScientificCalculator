package com.example.scientificcalculator;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.vision.L;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class MachineLearning extends AppCompatActivity {

    public static RadioGroup radioGroup;
    public static int color = Color.RED;

    public CrashView crashView;



    public static double slope = 0;
    public static double intercept = 0;

    public static NeuralNetwork neuralNetwork;

    public static List<KMeansClustering.Point2D> centers;

    public static int status = 0;

    public static HashMap <Integer, Integer> colorToIndex = new HashMap<>();

    //public static final int colors[] = {Color.parseColor("#9400D3"), Color.parseColor("#4B0082"), Color.parseColor("#0000FF"), Color.parseColor("#00FF00"), Color.parseColor("#FFFF00"), Color.parseColor("#FF7F00"), Color.parseColor("#FF0000")};

    public static final int colors[] = {Color.parseColor("#9400D3"), Color.parseColor("#0000FF"), Color.parseColor("#00FF00"), Color.parseColor("#FF0000")};

    //public static final int colors1[] = {Color.parseColor("#9F0ADD"), Color.parseColor("#560A8C"), Color.parseColor("#0A0AFF"), Color.parseColor("#0AFF0A"), Color.parseColor("#FEFE0A"), Color.parseColor("#FF8A0A"), Color.parseColor("#FF0A0A")};

    //public static final int colors1[] = {Color.parseColor("#9C0ADC"), Color.parseColor("#2424FF"), Color.parseColor("#24FF24"), Color.parseColor("#FF2424")};


    public HashMap<Integer, Integer> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_learning);

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        crashView = (CrashView) findViewById(R.id.crashView);
        //Toast.makeText(MachineLearning.this, Arrays.toString(colors),Toast.LENGTH_LONG).show();

        colorToIndex.put(Color.parseColor("#9400D3"), 0);
        colorToIndex.put(Color.parseColor("#0000FF"), 1);
        colorToIndex.put(Color.parseColor("#00FF00"), 2);
        colorToIndex.put(Color.parseColor("#FF0000"), 3);

        status = 0;

        for(int i=0; i<colors.length; i++) {

            map.put(colors[i],i);

        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //color = getColorOfTheSelectedButton(radioGroup);
                //color = Color.YELLOW;
                try {

                    color = colors[getColorOfTheSelectedButton(group)];
                    //Toast.makeText(MachineLearning.this, getColorOfTheSelectedButton(group) + " " + checkedId, Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    //Toast.makeText(MachineLearning.this,e.toString(),Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    public int getColorOfTheSelectedButton(RadioGroup radioGroup) {

        return radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));

        //return colorId;
    }

    public void deleteButton(View v) {

        radioGroup.clearCheck();

    }

    public double getDistance(float x1, float y1, float x2, float y2) {

        return Math.pow(Math.pow((x2-x1),2)+Math.pow((y2-y1),2), 0.5);

    }

    public void applyMachineLearning(View v) {

        Button button = (Button) v;
        int id = button.getId();

        int n = CrashView.points.size();

        double x[] = new double[n];
        double y[] = new double[n];
        int labels[] =new int[n];
        Log.d("Colors","aaaaaaaaaaaaaaaaaa");
        for(int i=0; i<n; i++) {

            x[i] = CrashView.points.get(i).x;
            y[i] = CrashView.points.get(i).y;
            labels[i] = map.get(CrashView.points.get(i).color);
            Log.d("Colors",labels[i]+"");

        }


        if(id == R.id.linearRegression) {

            LinearRegression linearRegression = new LinearRegression(x,y);

            slope = linearRegression.getSlope();
            intercept = linearRegression.getIntercept();
            //Toast.makeText(MachineLearning.this, slope*180/3.14+" "+intercept, Toast.LENGTH_LONG).show();
            status = 1;

        }
        else if(id == R.id.neuralNetworks) {

            Log.d("A","Started "+status);
            try {

                int totalLayers = NeuralNetwork.totalLayers;

                if(status == 2) {

                    NeuralNetwork.train(20, 0.08f);

                }
                else {

                    neuralNetwork = new NeuralNetwork();
                    float ans[][] = neuralNetwork.applyNeuralNetwork(x, y, labels);

                    for (int i = 0; i < ans.length; i++) {
                        Log.d("A", x[i] + " " + y[i] + " " + map.get(CrashView.points.get(i).color));
                        Log.d("Array:-", Arrays.toString(ans[i]));
                    }

                }



                int l = NeuralNetwork.totalOutputs;
                float curr[] = new float[l];


                for (int i = 0; i < n; i++) {

                    NeuralNetwork.forward(new float[]{(float) x[i]/NeuralNetwork.norm, (float) y[i]/NeuralNetwork.norm});

                    for (int j = 0; j < l; j++) {

                        curr[j] = NeuralNetwork.layers[totalLayers].neurons[j].value;

                    }

                    float maxx = -10000000;
                    int index = 0;

                    for (int j = 0; j < l; j++) {

                        if (curr[j] > maxx) {

                            maxx = curr[j];
                            index = j;
                        }

                        Log.d("Value at index",i+" "+j+" "+l+" "+curr[j]+" "+maxx);

                    }
                    Log.d("Max at index",index+"");
                    //CrashView.points.get(i).color = colors[index];

                }

                crashView.invalidate();





                Toast.makeText(MachineLearning.this, "Done", Toast.LENGTH_LONG).show();

            }
            catch (Exception e) {

                Toast.makeText(MachineLearning.this, e.toString(), Toast.LENGTH_LONG).show();

            }

            status = 2;

        }
        //else if(id == R.id.decisionTrees) {}
        if(id == R.id.kMeansClustering) {

            int totalCenters = 4;

            KMeansClustering kMeansClustering = new KMeansClustering();
            centers = kMeansClustering.applyKMeansClsuteirng(x, y, totalCenters);
            //StringBuilder sb = new StringBuilder();
            KMeansClustering.Point2D point2D;
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<centers.size(); i++) {
                sb.append(centers.get(i).x+" "+centers.get(i).y+"");
            }

            Toast.makeText(MachineLearning.this, centers.toString(), Toast.LENGTH_LONG).show();

            try {
                    for (int i = 0; i < n; i++) {

                        double minn = 10000000;
                        double distance;
                        int index = 0;

                        for (int j = 0; j < centers.size(); j++) {

                            point2D = centers.get(j);
                            distance = getDistance((float) x[i], (float) y[i], point2D.x, point2D.y);
                            if (distance < minn) {

                                minn = distance;
                                index = j;

                            }

                        }

                        CrashView.points.get(i).color = colors[colors.length - index - 1];
                        status = 3;

                }
            }
            catch (Exception e) {

                Toast.makeText(MachineLearning.this, e.toString(), Toast.LENGTH_LONG).show();

            }

        }

        crashView.invalidate();

    }


    public void refreshButton(View v) {

        if(status >= 1) {

            status = 0;

        }
        else {

            CrashView.points = new Vector<>();

        }

        crashView.invalidate();

    }


}
