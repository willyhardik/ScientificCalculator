package com.example.scientificcalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class KMeansClustering {

    private static final int REPLICATION_FACTOR = 200;
    private static final int NUM_THREADS = 30;

    public static class Point2D {

        public float x;
        public float y;

        public Point2D(float x, float y) {
            this.x = x;
            this.y = y;
        }

        private double getDistance(Point2D other) {
            return Math.sqrt(Math.pow(this.x - other.x, 2)
                    + Math.pow(this.y - other.y, 2));
        }

        public int getNearestPointIndex(List<Point2D> points) {
            int index = -1;
            double minDist = Double.MAX_VALUE;
            for (int i = 0; i < points.size(); i++) {
                double dist = this.getDistance(points.get(i));
                if (dist < minDist) {
                    minDist = dist;
                    index = i;
                }
            }
            return index;
        }

        public static Point2D getMean(List<Point2D> points) {
            float accumX = 0;
            float accumY = 0;
            if (points.size() == 0) return new Point2D(accumX, accumY);
            for (Point2D point : points) {
                accumX += point.x;
                accumY += point.y;
            }
            return new Point2D(accumX / points.size(), accumY / points.size());
        }

        @Override
        public String toString() {
            return "[" + this.x + "," + this.y + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj.getClass() != Point2D.class)) {
                return false;
            }
            Point2D other = (Point2D) obj;
            return this.x == other.x && this.y == other.y;
        }

    }

    public static List<Point2D> getDataset(double x[], double y[]) {
        List<Point2D> dataset = new ArrayList<>();
        float x1;
        float y1;
        for(int i=0; i<x.length; i++) {
            x1 = (float) x[i];
            y1 = (float) y[i];
            Point2D point = new Point2D(x1, y1);
            for (int j = 0; j < REPLICATION_FACTOR; j++)
                dataset.add(point);
        }
        return dataset;
    }

    public static List<Point2D> initializeRandomCenters(int n, int lowerBound, int upperBound) {
        List<Point2D> centers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            float x = (float)(Math.random() * (upperBound - lowerBound) + lowerBound);
            float y = (float)(Math.random() * (upperBound - lowerBound) + lowerBound);
            Point2D point = new Point2D(x, y);
            centers.add(point);
        }
        return centers;
    }

    private static Callable<Void> createWorker(final List<Point2D> partition, final List<Point2D> centers,
                                               final List<List<Point2D>> clusters) {
        return new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                int indexes[] = new int[partition.size()];
                for (int i = 0; i < partition.size(); i++) {
                    Point2D data = partition.get(i);
                    int index = data.getNearestPointIndex(centers);
                    indexes[i] = index;
                }
                synchronized (clusters) {
                    for (int i = 0; i < indexes.length; i++) {
                        clusters.get(indexes[i]).add(partition.get(i));
                    }
                }
                return null;
            }

        };
    }

    private static <V> List<List<V>> partition(List<V> list, int parts) {
        List<List<V>> lists = new ArrayList<List<V>>(parts);
        for (int i = 0; i < parts; i++) {
            lists.add(new ArrayList<V>());
        }
        for (int i = 0; i < list.size(); i++) {
            lists.get(i % parts).add(list.get(i));
        }
        return lists;
    }

    public static List<Point2D> concurrentGetNewCenters(final List<Point2D> dataset, final List<Point2D> centers) {
        final List<List<Point2D>> clusters = new ArrayList<List<Point2D>>(centers.size());
        for (int i = 0; i < centers.size(); i++) {
            clusters.add(new ArrayList<Point2D>());
        }
        List<List<Point2D>> partitionedDataset = partition(dataset, NUM_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Callable<Void>> workers = new ArrayList<>();
        for (int i = 0; i < NUM_THREADS; i++) {
            workers.add(createWorker(partitionedDataset.get(i), centers, clusters));
        }
        try {
            executor.invokeAll(workers);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        List<Point2D> newCenters = new ArrayList<>(centers.size());
        for (List<Point2D> cluster : clusters) {
            newCenters.add(Point2D.getMean(cluster));
        }
        return newCenters;
    }

    public static List<Point2D> getNewCenters(List<Point2D> dataset, List<Point2D> centers) {
        List<List<Point2D>> clusters = new ArrayList<>(centers.size());
        for (int i = 0; i < centers.size(); i++) {
            clusters.add(new ArrayList<Point2D>());
        }
        for (Point2D data : dataset) {
            int index = data.getNearestPointIndex(centers);
            clusters.get(index).add(data);
        }
        List<Point2D> newCenters = new ArrayList<>(centers.size());
        for (List<Point2D> cluster : clusters) {
            newCenters.add(Point2D.getMean(cluster));
        }
        return newCenters;
    }

    public static double getDistance(List<Point2D> oldCenters, List<Point2D> newCenters) {
        double accumDist = 0;
        for (int i = 0; i < oldCenters.size(); i++) {
            double dist = oldCenters.get(i).getDistance(newCenters.get(i));
            accumDist += dist;
        }
        return accumDist;
    }

    public static List<Point2D> kmeans(List<Point2D> centers, List<Point2D> dataset, int k) {
        boolean converged;
        do {
            List<Point2D> newCenters = getNewCenters(dataset, centers);
            double dist = getDistance(centers, newCenters);
            centers = newCenters;
            converged = dist == 0;
        } while (!converged);
        return centers;
    }

    public static List<Point2D> concurrentKmeans(List<Point2D> centers, List<Point2D> dataset, int k) {
        boolean converged;
        do {
            List<Point2D> newCenters = concurrentGetNewCenters(dataset, centers);
            double dist = getDistance(centers, newCenters);
            centers = newCenters;
            converged = dist == 0;
        } while (!converged);
        return centers;
    }

    public static List<Point2D> applyKMeansClsuteirng(double x[], double y[], int k) {
        List<Point2D> dataset = null;
        dataset = getDataset(x, y);

        List<Point2D> centers = initializeRandomCenters(k, 0, 1000000);
        long start = System.currentTimeMillis();
        //kmeans(centers, dataset, k);


        return concurrentKmeans(centers, dataset, k);

    }

}
