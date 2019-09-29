package com.example.scientificcalculator;

public class LinearRegression {

    public double intercept, slope;
    public double r2;
    public double svar0, svar1;

    public LinearRegression(double[] x, double[] y) {

        int n = x.length;

        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (int i = 0; i < n; i++) {
            sumx  += x[i];
            sumx2 += x[i]*x[i];
            sumy  += y[i];
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        slope  = xybar / xxbar;
        intercept = ybar - slope * xbar;

        double rss = 0.0;
        double ssr = 0.0;

        for (int i = 0; i < n; i++) {
            double fit = slope*x[i] + intercept;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }

        int degreesOfFreedom = n-2;
        r2    = ssr / yybar;
        double svar  = rss / degreesOfFreedom;
        svar1 = svar / xxbar;
        svar0 = svar/n + xbar*xbar*svar1;
    }

    public double getIntercept() {
        return intercept;
    }


    public double getSlope() {
        return slope;
    }

    public double R2() {
        return r2;
    }

    public double getInterceptStdErr() {
        return Math.sqrt(svar0);
    }

    public double getSlopeStdErr() {
        return Math.sqrt(svar1);
    }

    public double getPrediction(double x) {
        return slope*x + intercept;
    }


}
