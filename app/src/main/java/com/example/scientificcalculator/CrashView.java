package com.example.scientificcalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Vector;

import javax.crypto.Mac;


class Point {

    float x;
    float y;
    int color;

    public Point(float x, float y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

}



public class CrashView extends View {

    Paint paint;
    Path path;

    float xPos = 0;
    float yPos = 0;
    float r = 0;

    public static int width = 0;
    public static int height = 0;

    int rr = 0;

    int color;

    boolean up = false;

    public static Vector<Point> points = new Vector<>();

    public CrashView(Context context, AttributeSet attrs) {

        super(context, attrs);
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        color = Color.RED;
        paint.setColor(color);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        r = 3;
        paint.setStrokeWidth(2*r);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void drawCircles(Vector<Point> points, float r, Canvas canvas, Paint paint) {

        for(int i=0; i<points.size(); i++) {

            paint.setColor(points.get(i).color);
            canvas.drawOval(points.get(i).x - r, points.get(i).y - r, points.get(i).x + r, points.get(i).y + r, paint);

        }

    }


    public double getDistance(float x1, float y1, float x2, float y2) {

        return Math.pow(Math.pow((x2-x1),2)+Math.pow((y2-y1),2), 0.5);

    }


    public Vector<Point> removePoints(Vector<Point> points, float xPos, float yPos, float r) {

        Vector<Point> pointsAfterDeletion = new Vector<>();

        for(int i=0; i<points.size(); i++) {

            if(getDistance(points.get(i).x, points.get(i).y, xPos, yPos) > 8*r) {

                pointsAfterDeletion.add(new Point(points.get(i).x, points.get(i).y, points.get(i).color));

            }

        }

        return  pointsAfterDeletion;

    }


    public void drawRegressionLine(Canvas canvas, double slope, double intercept, Paint paint) {

        canvas.drawLine(0, (float)intercept, canvas.getWidth(), (float) (slope*canvas.getWidth()+intercept), paint);

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void  onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPath(path,paint);
        //canvas.drawPoint(xPos, yPos, paint);

        width = canvas.getWidth();
        height = canvas.getHeight();


        if(up) {
            if(MachineLearning.radioGroup.getCheckedRadioButtonId() == -1) {

                points = removePoints(points, xPos, yPos, r);

            }
            else if(points.size()==0 || (points.get(points.size()-1).x != xPos && points.get(points.size()-1).y != yPos)) {
                color = MachineLearning.color;
                points.add(new Point(xPos, yPos, color));
            }
            up = false;
        }
        drawCircles(points, r, canvas, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0,canvas.getWidth()-3,canvas.getHeight()-3,paint);

        if(MachineLearning.status == 1) {

            int n = points.size();

            double x[] = new double[n];
            double y[] = new double[n];

            for(int i=0; i<n; i++) {

                x[i] = points.get(i).x;
                y[i] = points.get(i).y;

            }

            LinearRegression linearRegression = new LinearRegression(x,y);

            double slope = linearRegression.getSlope();
            double intercept = linearRegression.getIntercept();

            drawRegressionLine(canvas, slope, intercept, paint);

        }
        else if(MachineLearning.status == 2) {

            try {

                int l = NeuralNetwork.totalOutputs;
                float curr[] = new float[l];


                for (int i = 0; i < canvas.getWidth(); i += 5) {

                    for (int j = 0; j < canvas.getHeight(); j += 5) {

                        NeuralNetwork.forward(new float[]{(float) i/1000, (float) j/1000});

                        for (int k = 0; k < l; k++) {

                            curr[k] = NeuralNetwork.layers[NeuralNetwork.totalLayers].neurons[k].value;

                        }

                        float maxx = -10000000;
                        int index = 0;

                        for (int k = 0; k < l; k++) {

                            if (curr[k] > maxx) {

                                maxx = curr[k];
                                index = k;
                            }

                            //Log.d("Value at index",i+" "+j+" "+l+" "+curr[k]+" "+maxx);

                        }
                        //Log.d("Max at index",index+"");
                        paint.setColor(MachineLearning.colors[index]);
                        canvas.drawRect(i, j, i + 5, j + 5, paint);


                    }
                }

            }
            catch (Exception e) {

                Log.d("Error:-",e.toString());

            }

        }
        else if(MachineLearning.status == 3) {


            try {


                KMeansClustering.Point2D point2D;


                int index = 0;


                for (int i = 0; i < canvas.getWidth(); i += 5) {

                    for (int j = 0; j < canvas.getHeight(); j += 5) {

                        for (int k = 0; k < MachineLearning.centers.size(); k++) {

                            double minn = 10000000;
                            double distance;
                            index = 0;


                            for (int l = 0; l < MachineLearning.centers.size(); l++) {

                                point2D = MachineLearning.centers.get(l);
                                distance = getDistance((float) i, (float) j, point2D.x, point2D.y);
                                if (distance < minn) {

                                    minn = distance;
                                    index = l;

                                }

                            }

                            //Log.d("Value at index",i+" "+j+" "+l+" "+curr[k]+" "+maxx);

                        }
                        //Log.d("Max at index",index+"");
                        paint.setColor(MachineLearning.colors[MachineLearning.colors.length-index-1]);
                        canvas.drawRect(i, j, i + 5, j + 5, paint);


                    }
                }

            }
            catch (Exception e) {

                Log.d("Error:-",e.toString());

            }




        }


        drawCircles(points, r, canvas, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0,canvas.getWidth()-3,canvas.getHeight()-3,paint);

        /*
        canvas.drawColor(Color.rgb(rr,rr,rr));
        rr += 50;
        if(rr == 250) {
            rr = 0;
        }
        */
        //canvas.drawOval(xPos-r, yPos-r, xPos+r, yPos+r, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        xPos = event.getX();
        yPos = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                up = true;
                //path.moveTo(xPos, yPos);
                return true;

            case MotionEvent.ACTION_MOVE:
                //path.lineTo(xPos, yPos);
                break;

            case MotionEvent.ACTION_UP:
                path.moveTo(xPos, yPos);
                up = false;
                break;

            default:
                return false;

        }

        invalidate();
        return true;

    }
}