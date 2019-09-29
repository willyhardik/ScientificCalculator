package com.example.scientificcalculator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class KNearestNeighbours {

    /*
    public double getDistance(float x1, float y1, float x2, float y2) {

        return Math.pow(Math.pow((x2-x1),2)+Math.pow((y2-y1),2), 0.5);

    }


    public int[][] getColorsOfAllPositions(int knn) {

        double d;

        HashMap<Double, Vector<Integer> map;
        Vector<Integer> v;
        double arr[];
        int counts[];

        for(int i=0; i<CrashView.width; i+=5) {

            for(int j=0; j<CrashView.height; j+=5) {

                map = new HashMap<>();

                for(int k=0; k<CrashView.points.size(); k++) {

                      d = getDistance(CrashView.points.get(k).x, CrashView.points.get(k).y, (float)i, (float)j);

                      if(!map.containsKey(d)) {

                          map.put(d, new Vector<Integer>());

                      }

                      v = map.get(d);
                      v.add(CrashView.points.get(k).color);

                }

                arr = new double[map.size()];

                int c = 0;

                for(Double dist : map.keySet()) {

                    arr[c] = -dist;
                    c++;

                }

                Arrays.sort(arr);

                counts = new int[4];

                if(arr.length >= knn) {

                    for(int k=0; k<knn; k++) {

                        map.get();

                    }

                }
                else {



                }

            }

        }


    }

    */



}
