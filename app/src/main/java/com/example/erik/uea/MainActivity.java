package com.example.erik.uea;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.Math;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private static Random rnd = new Random();
    private String[] arraySpinner;
    private List<double[]> vseResitve = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.arraySpinner = new String[]{"Sphere", "Griewank", "Rastrigin", "Rosenbrock"};
        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        spinner.setAdapter(adapter);
        final Button button = (Button)findViewById(R.id.button);
        final EditText dimEdit = (EditText)findViewById(R.id.dimEdit);
        final EditText evalEdit = (EditText)findViewById(R.id.evalEdit);
        final TextView tv = (TextView)findViewById(R.id.resultView);
        final CheckBox cb = (CheckBox)findViewById(R.id.checkBox);
        final TextView npText = (TextView)findViewById(R.id.textView2);
        final EditText npEdit = (EditText)findViewById(R.id.npEdit);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                tv.setText("");
                tv.setMovementMethod(new ScrollingMovementMethod());
                int dim = Integer.parseInt(dimEdit.getText().toString());
                double avg = 0.0;
                double[] najboljsaResitev = new double[dim];
                int eval = Integer.parseInt(evalEdit.getText().toString());
                String selectedSpinner = spinner.getSelectedItem().toString();
                int problem = 0;
                if(!cb.isChecked()){
                    RandomSearchOptimization(eval, dim, vseResitve, najboljsaResitev, tv,selectedSpinner);
                }
                else if(cb.isChecked()){
                    if(selectedSpinner == "Sphere")
                        problem = 1;
                    else if(selectedSpinner == "Griewank")
                        problem = 2;
                    else if(selectedSpinner == "Rastrigin")
                        problem = 3;
                    else if(selectedSpinner == "Rosenbrock")
                        problem = 4;
                    for(int i = 0; i < 10; i++) {
                        tv.append(i+1 + "; ");
                        avg += Differential(problem, dim, eval, Integer.parseInt(npEdit.getText().toString()), tv, spinner.getSelectedItem().toString());
                    }
                    tv.append("10-run Avg: "+ avg/10.0);
                }


            }
        });
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked())
                {
                    npText.setVisibility(View.VISIBLE);
                    npEdit.setVisibility(View.VISIBLE);
                }
                else{
                    npText.setVisibility(View.GONE);
                    npEdit.setVisibility(View.GONE);
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public static double sphere(double d[]){
        double r = 0;
        for(int i = 0; i < d.length; i++){
            r += d[i]*d[i];
        }
        return r;
    }
    public static double griewank(double d[]){
        double r = 0, s = 0, p = 1;
        int fr =  4000;
        for(int i = 0; i < d.length; i++){
            s += Math.pow(d[i],2);
        }
        for(int j = 0; j < d.length; j++){
                p *= Math.cos(d[j] / Math.sqrt(j+1));
        }
        r = s / fr - p + 1;
        return r;
    }
    public static double[] vrniRnd(int dim, double min, double max){
        double r[] = new double[dim];
        for(int i = 0; i < r.length; i++){
            r[i] = rnd.nextDouble()*(max-min)+min;
        }
        return r;
    }
    public static double rast(double d[]){
        double r = 0, s = 0;
        for(int i = 0; i < d.length; i++){
            s = s + (Math.pow(d[i],2) - 10 * Math.cos(2 * Math.PI * d[i]));
        }
        r = 10 * d.length + s;
        return r;
    }
    public static double rosen(double d[]){
        double s = 0;
        for(int i = 0; i < d.length-1; i++){
            s += s + 100 * Math.pow(Math.pow(d[i], 2) - d[i+1], 2) + Math.pow(d[i]-1,2);
        }
        return s;
    }
    public static void RandomSearchOptimization(int eval, int dim, List<double[]> vseResitve, double[] najboljsaResitev, TextView tv, String spinnerValue){
        vseResitve = new ArrayList<>();
        if(spinnerValue == "Sphere"){
            double min = -5.12;
            double max = 5.12;
            for (int i = 0; i < eval; i++)
            {
                vseResitve.add(vrniRnd(dim, min, max));
                double izracunTrenutne = sphere(vseResitve.get(i));
                if (i == 0)
                {
                    najboljsaResitev = vseResitve.get(i);
                }
                else
                {
                    double sResitev = sphere(najboljsaResitev);
                    if (izracunTrenutne < sResitev)
                    {
                        DecimalFormat df = new DecimalFormat("#.######");
                        df.setRoundingMode(RoundingMode.CEILING);
                        tv.append( i + "; (");
                        najboljsaResitev = vseResitve.get(i);
                        for(int x = 0; x < najboljsaResitev.length; x++)
                        {
                            if(x < najboljsaResitev.length -1){
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + ", ");
                            }
                            else{
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + "");
                            }
                        }
                        tv.append("): " + String.valueOf(df.format(sResitev))+ "\n\n");
                    }

                }
            }
        }
        else if(spinnerValue == "Griewank"){
            for (int i = 0; i < eval; i++)
            {
                double min = -600;
                double max = 600;
                vseResitve.add(vrniRnd(dim, min, max));
                double izracunTrenutne = griewank(vseResitve.get(i));
                if (i == 0)
                {
                    najboljsaResitev = vseResitve.get(i);
                }
                else
                {
                    double sResitev = griewank(najboljsaResitev);
                    if (izracunTrenutne < sResitev)
                    {
                        DecimalFormat df = new DecimalFormat("#.######");
                        df.setRoundingMode(RoundingMode.CEILING);
                        tv.append( i + "; (");
                        najboljsaResitev = vseResitve.get(i);
                        for(int x = 0; x < najboljsaResitev.length; x++)
                        {
                            if(x < najboljsaResitev.length -1){
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + ", ");
                            }
                            else{
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + "");
                            }
                        }
                        tv.append("): " + String.valueOf(df.format(sResitev))+ "\n\n");
                    }

                }
            }
        }
        else if(spinnerValue == "Rastrigin"){
            for (int i = 0; i < eval; i++)
            {
                double min = -5.12;
                double max = 5.12;
                vseResitve.add(vrniRnd(dim, min, max));
                double izracunTrenutne = rast(vseResitve.get(i));
                if (i == 0)
                {
                    najboljsaResitev = vseResitve.get(i);
                }
                else
                {
                    double sResitev = rast(najboljsaResitev);
                    if (izracunTrenutne < sResitev)
                    {
                        DecimalFormat df = new DecimalFormat("#.######");
                        df.setRoundingMode(RoundingMode.CEILING);
                        tv.append( i + "; (");
                        najboljsaResitev = vseResitve.get(i);
                        for(int x = 0; x < najboljsaResitev.length; x++)
                        {
                            if(x < najboljsaResitev.length -1){
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + ", ");
                            }
                            else{
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + "");
                            }
                        }
                        tv.append("): " + String.valueOf(df.format(sResitev))+ "\n\n");
                    }

                }
            }
        }
        else if(spinnerValue == "Rosenbrock"){
            for (int i = 0; i < eval; i++)
            {
                double min = -5;
                double max = 10;
                vseResitve.add(vrniRnd(dim, min, max));
                double izracunTrenutne = rosen(vseResitve.get(i));
                if (i == 0)
                {
                    najboljsaResitev = vseResitve.get(i);
                }
                else
                {
                    double sResitev = rosen(najboljsaResitev);
                    if (izracunTrenutne < sResitev)
                    {
                        DecimalFormat df = new DecimalFormat("#.######");
                        df.setRoundingMode(RoundingMode.CEILING);
                        tv.append( i + "; (");
                        najboljsaResitev = vseResitve.get(i);
                        for(int x = 0; x < najboljsaResitev.length; x++)
                        {
                            if(x < najboljsaResitev.length -1){
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + ", ");
                            }
                            else{
                                tv.append(String.valueOf(df.format(najboljsaResitev[x])) + "");
                            }
                        }
                        tv.append("): " + String.valueOf(df.format(sResitev))+ "\n\n");
                    }
                }
            }
        }
    }
    public static double Differential(int problem, int dim, int eval, int NP, TextView tv, String spinnerValue) {
        double CR = 0.8;
        double F = 1.3;
        // NP - pop_size
        double x[][]; //pop_size X dimension
        double f[]; //fitness
        double fy;
        double max;
        double min;
        int Di = 10;
        int R;
        int a, b, c;
        int evalCounter = 0;
        x = new double[NP][];
        f = new double[NP];
        if (problem == 1) {
            min = -5.12;
            max = 5.12;
            for (int i = 0; i < NP; i++) {
                x[i] = vrniRnd(dim, min, max);
                f[i] = sphere(x[i]);
                evalCounter++;
            }
        } else if (problem == 2) {
            min = -600;
            max = 600;
            for (int i = 0; i < NP; i++) {
                x[i] = vrniRnd(dim, min, max);
                f[i] = griewank(x[i]);
                evalCounter++;
            }
        } else if (problem == 3) {
            min = -5.12;
            max = 5.12;
            for (int i = 0; i < NP; i++) {
                x[i] = vrniRnd(dim, min, max);
                f[i] = rast(x[i]);
                evalCounter++;
            }
        } else if (problem == 4) {
            min = -5;
            max = 10;
            for (int i = 0; i < NP; i++) {
                x[i] = vrniRnd(dim, min, max);
                f[i] = rosen(x[i]);
                evalCounter++;
            }
        }
        while (evalCounter < eval) {
            for (int i = 0; i < NP; i++) {
                do {
                    a = rnd.nextInt(NP);
                } while (i == a);
                do {
                    b = rnd.nextInt(NP);
                } while ((i == b) || (a == b));
                do {
                    c = rnd.nextInt(NP);
                } while ((i == c) || (a == c) || (b == c));
                R = rnd.nextInt(dim);
                double y[] = new double[dim];
                for (int ii = 0; ii < dim; ii++) {
                    if ((rnd.nextDouble() < CR) || (ii == R)) {
                        y[ii] = x[a][ii] + F * (x[b][ii] - x[c][ii]);
                    } else y[ii] = x[i][ii];
                }
                fy = sphere(y);
                evalCounter++;
                if (f[i] > fy) {
                    f[i] = fy;
                    x[i] = y;
                }
                if (evalCounter >= eval) break;
            }
        }
        double minf = f[0];
        for (int i = 0; i < NP; i++) {
            if (f[i] < minf) {
                minf = f[i];
            }

        }
        //tv.append("X: " + minf + "\n");
        double minF = f[0];
        int index = 0;
        for (int i = 0; i < f.length; i++) {
            if (f[i] < minF) {
                minF = f[i];
                index = i;
                //tv.append("Min" + minF + "\n");
            }
        }
        tv.append("(");
        DecimalFormat df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.CEILING);
        for (int j = 0; j < dim; j++) {
            if(j< dim-1){
                tv.append(x[index][j] + ", ");
            }
            else{
                tv.append(x[index][j]+"");
            }
        }
        tv.append("): " + minF + "\n\n");
        return minF;
    }
}
