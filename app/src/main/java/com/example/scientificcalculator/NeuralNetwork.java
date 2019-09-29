package com.example.scientificcalculator;

import android.util.Log;

import java.util.Arrays;

class Layer {
    public Neuron[] neurons;

    // Constructor for the hidden and output layer
    public Layer(int inNeurons,int numberNeurons) {

        this.neurons = new Neuron[numberNeurons];

        for(int i = 0; i < numberNeurons; i++) {
            float[] weights = new float[inNeurons];
            for(int j = 0; j < inNeurons; j++) {
                weights[j] = StatUtil.RandomFloat(Neuron.minWeightValue, Neuron.maxWeightValue);
            }
            neurons[i] = new Neuron(weights,StatUtil.RandomFloat(0, 1));
        }
    }


    // Constructor for the input layer
    public Layer(float input[]) {
        this.neurons = new Neuron[input.length];
        for(int i = 0; i < input.length; i++) {
            this.neurons[i] = new Neuron(input[i]);
        }
    }
}


class Neuron {

    // Static variables
    static float minWeightValue;
    static float maxWeightValue;

    // Non-Static Variables
    float[] weights;
    float[] cache_weights;
    float gradient;
    float bias;
    float value = 0;


    // Constructor for the hidden / output neurons
    public Neuron(float[] weights, float bias){
        this.weights = weights;
        this.bias = bias;
        this.cache_weights = this.weights;
        this.gradient = 0;
    }

    // Constructor for the input neurons
    public Neuron(float value){
        this.weights = null;
        this.bias = -1;
        this.cache_weights = this.weights;
        this.gradient = -1;
        this.value = value;
    }

    // Static function to set min and max weight for all variables
    public static void setRangeWeight(float min,float max) {
        minWeightValue = min;
        maxWeightValue = max;
    }

    // Function used at the end of the backprop to switch the calculated value in the
    // cache weight in the weights
    public void update_weight() {
        this.weights = this.cache_weights;
    }


}

class StatUtil {

    // Get a random numbers between min and max
    public static float RandomFloat(float min, float max) {
        float a = (float) Math.random();
        float num = min + (float) Math.random() * (max - min);
        if(a < 0.5)
            return num;
        else
            return -num;
    }

    // Sigmoid function
    public static float Sigmoid(float x) {
        return (float) (1/(1+Math.pow(Math.E, -x)));
        //return (float) Math.log(1 + Math.pow(Math.E, (double) x));
    }

    // Derivative of the sigmoid function
    public static float SigmoidDerivative(float x) {
        return Sigmoid(x)*(1-Sigmoid(x));
        //return (float) (1/(1 + Math.pow(Math.E, -x)));
    }

    // Used for the backpropagation
    public static float squaredError(float output,float target) {
        return (float) (0.5*Math.pow(2,(target-output)));
    }

    // Used to calculate the overall error rate (not yet used)
    public static float sumSquaredError(float[] outputs,float[] targets) {
        float sum = 0;
        for(int i=0;i<outputs.length;i++) {
            sum += squaredError(outputs[i],targets[i]);
        }
        return sum;
    }
}


class TrainingData {

    float[] data;
    float[] expectedOutput;

    public TrainingData(float[] data, float[] expectedOutput) {
        this.data = data;
        this.expectedOutput = expectedOutput;
    }

}


public class NeuralNetwork {

    // Notes:
    // The first thing I did is to make the code a bit more dynamic, there was a clear problem with the backpropagation part,
    // but I wasn't sure if this was the only bug in the code. So to make sure I refactored the whole code to make it a bit more readable.
    // I made two other class:
    // 1) StatUtil, which is a static class containing stats functions.
    // 2) Layer, which represent a layer with array on neurons inside them.
    // The last class was useful so to modularize the layers and not hardcode them.
    // The first class was useful to remove some code from the main method and make it less bulky.
    // Next thing I did is to remove some variable from the code and put them in the neuron class(min/maxWeight).
    // And to add some variables in the neurons class.
    // When the code was more readable I went to rewrite the forward propagation (which you got right).
    // I then rewrote the back propagation algorithm and this is where you messed up some stuff.
    // Here is a very good step by step explanation of every part of the backprop algorithm : https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/

    // One thing to note is that the biases are still not implemented as I didn't have enough time to include them. It isn't a problem
    // for the XOR function, but it might be if you are trying to learn a function requiring some degrees of translation.

    // Variable Declaration

    // Layers
    static Layer[] layers; // My changes

    // Training data
    static TrainingData[] tDataSet; // My changes

    public static int totalOutputs = 4;
    public static int totalLayers = 2;
    public static float norm = 1000;

    public float[][] applyNeuralNetwork(double x1[], double y1[], int labels[]) {
        // My changes
        // Set the Min and Max weight value for all Neurons
        Neuron.setRangeWeight(-1,1);


        int n = x1.length;
        float x[] = new float[n];
        float y[] = new float[n];

        for(int i=0; i<n; i++) {

            x[i] = (float)x1[i]/norm;
            y[i] = (float)y1[i]/norm;

        }

        // Create the layers
        // Notes: One thing you didn't code right is that neurons in a layer
        // need to have number of weights corresponding to the previous layer
        // which means that the first hidden layer need to have 2 weights per neuron and 6 neurons
        layers = new Layer[totalLayers+1];
        layers[0] = null; // Input Layer 0,2
        layers[1] = new Layer(2,20); // Hidden Layer 2,6
        //layers[2] = new Layer(10,10); // Hidden Layer 2,6
        layers[2] = new Layer(20, totalOutputs); // Output Layer 6,1

        // Create the training data
        CreateTrainingData(x, y, labels);

        /*
        System.out.println("============");
        System.out.println("Output before training");
        System.out.println("============");
        for(int i = 0; i < tDataSet.length; i++) {
            forward(tDataSet[i].data);
            System.out.println(layers[2].neurons[0].value);
        }
        */
        train(50, 0.05f);


        //System.out.println("============");
        //System.out.println("Output after training");
        //System.out.println("============");
        float ans[][] = new float[tDataSet.length][totalOutputs];

        for(int i = 0; i < tDataSet.length; i++) {
            forward(tDataSet[i].data);
            //Log.d("Dataset", Arrays.toString(tDataSet[i].data)+"");
            //System.out.println((i%2)+" "+layers[totalLayers].neurons[0].value+" "+layers[3].neurons[1].value);
            for(int j=0; j<totalOutputs; j++) {
                ans[i][j] = layers[totalLayers].neurons[j].value;
                Log.d("Dataset", layers[totalLayers].neurons[j].value+"");
            }


        }

        return ans;

    }

    public static void CreateTrainingData(float x[], float y[], int labels[]) {
        /*
        float[] input1 = new float[] {0, 0}; //Expect 0 here
        float[] input2 = new float[] {0, 1}; //Expect 1 here
        float[] input3 = new float[] {0, 2}; //Expect 1 here
        float[] input4 = new float[] {0, 3}; //Expect 0 here
        float[] input5 = new float[] {1, 0}; //Expect 0 here
        float[] input6 = new float[] {1, 1}; //Expect 1 here
        float[] input7 = new float[] {1, 2}; //Expect 1 here
        float[] input8 = new float[] {1, 3}; //Expect 0 here
        float[] input9 = new float[] {2, 0}; //Expect 0 here
        float[] input10 = new float[] {2, 1}; //Expect 1 here
        float[] input11 = new float[] {2, 2}; //Expect 1 here
        float[] input12 = new float[] {2, 3}; //Expect 0 here
        float[] input13 = new float[] {3, 0}; //Expect 0 here
        float[] input14 = new float[] {3, 1}; //Expect 1 here
        float[] input15 = new float[] {3, 2}; //Expect 1 here
        float[] input16 = new float[] {3, 3}; //Expect 0 here

        float[] expectedOutput1 = new float[] {0};
        float[] expectedOutput2 = new float[] {1};
        float[] expectedOutput3 = new float[] {1};
        float[] expectedOutput4 = new float[] {0};

        // My changes (using an array for the data sets)
        tDataSet = new TrainingData[4];
        tDataSet[0] = new TrainingData(input1, expectedOutput1);
        tDataSet[1] = new TrainingData(input2, expectedOutput2);
        tDataSet[2] = new TrainingData(input3, expectedOutput3);
        tDataSet[3] = new TrainingData(input4, expectedOutput4);
        */
        int n = x.length;
        tDataSet = new TrainingData[n];

        for(int i=0; i<n; i++) {

            float input[] = new float[2];
            input[0] = x[i];
            input[1] = y[i];
            float expectedOutput[] = new float[NeuralNetwork.totalOutputs];
            expectedOutput[labels[i]] = 1;
            tDataSet[i] = new TrainingData(input,expectedOutput);

        }

        /*
        for(int i=0;i<4;i++) {

            for(int j=0;j<4;j++) {

                float input[] = new float[2];
                input[0] = i;
                input[1] = j;
                float expectedOutput[] = new float[2];
                expectedOutput[(i*4+j)%2] = 1;
                tDataSet[i*4+j] = new TrainingData(input,expectedOutput);

            }

        }
        */
    }

    public static void forward(float[] inputs) {
        // First bring the inputs into the input layer layers[0]
        layers[0] = new Layer(inputs);

        for(int i = 1; i < layers.length; i++) {
            for(int j = 0; j < layers[i].neurons.length; j++) {
                float sum = 0;
                for(int k = 0; k < layers[i-1].neurons.length; k++) {
                    sum += layers[i-1].neurons[k].value*layers[i].neurons[j].weights[k];
                }
                //sum += layers[i].neurons[j].bias; // TODO add in the bias
                layers[i].neurons[j].value = StatUtil.Sigmoid(sum);
            }
        }
    }

    // This part is heavily inspired from the website in the first note.
    // The idea is that you calculate a gradient and cache the updated weights in the neurons.
    // When ALL the neurons new weight have been calculated we refresh the neurons.
    // Meaning we do the following:
    // Calculate the output layer weights, calculate the hidden layer weight then update all the weights
    public static void backward(float learning_rate,TrainingData tData) {

        int number_layers = layers.length;
        int out_index = number_layers-1;

        // Update the output layers
        // For each output
        for(int i = 0; i < layers[out_index].neurons.length; i++) {
            // and for each of their weights
            float output = layers[out_index].neurons[i].value;
            float target = tData.expectedOutput[i];
            float derivative = output-target;
            float delta = derivative*(output*(1-output));
            layers[out_index].neurons[i].gradient = delta;
            for(int j = 0; j < layers[out_index].neurons[i].weights.length;j++) {
                float previous_output = layers[out_index-1].neurons[j].value;
                float error = delta*previous_output;
                layers[out_index].neurons[i].cache_weights[j] = layers[out_index].neurons[i].weights[j] - learning_rate*error;
            }
        }

        //Update all the subsequent hidden layers
        for(int i = out_index-1; i > 0; i--) {
            // For all neurons in that layers
            for(int j = 0; j < layers[i].neurons.length; j++) {
                float output = layers[i].neurons[j].value;
                float gradient_sum = sumGradient(j,i+1);
                float delta = (gradient_sum)*(output*(1-output));
                layers[i].neurons[j].gradient = delta;
                // And for all their weights
                for(int k = 0; k < layers[i].neurons[j].weights.length; k++) {
                    float previous_output = layers[i-1].neurons[k].value;
                    float error = delta*previous_output;
                    layers[i].neurons[j].cache_weights[k] = layers[i].neurons[j].weights[k] - learning_rate*error;
                }
            }
        }

        // Here we do another pass where we update all the weights
        for(int i = 0; i< layers.length;i++) {
            for(int j = 0; j < layers[i].neurons.length;j++) {
                layers[i].neurons[j].update_weight();
            }
        }

    }

    // This function sums up all the gradient connecting a given neuron in a given layer
    public static float sumGradient(int n_index,int l_index) {
        float gradient_sum = 0;
        Layer current_layer = layers[l_index];
        for(int i = 0; i < current_layer.neurons.length; i++) {
            Neuron current_neuron = current_layer.neurons[i];
            gradient_sum += current_neuron.weights[n_index]*current_neuron.gradient;
        }
        return gradient_sum;
    }


    // This function is used to train being forward and backward.
    public static void train(int training_iterations,float learning_rate) {
        for(int i = 0; i < training_iterations; i++) {
            if(i%5 == 0) {

                Log.d("Iterations done:-",i+"");

            }
            for(int j = 0; j < tDataSet.length; j++) {
                forward(tDataSet[j].data);
                backward(learning_rate,tDataSet[j]);
            }
        }
    }
}