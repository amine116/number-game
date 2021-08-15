package com.amine.knownumbers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class ShowCurResultActivity extends AppCompatActivity {

    private LinearLayout resultLayout;
    private double[] num1 = new double[ActivityGame.NUMBER_OF_QUERY + 1],
            num2 = new double[ActivityGame.NUMBER_OF_QUERY + 1], res = new double[ActivityGame.NUMBER_OF_QUERY + 1];
    private boolean[] isRight = new boolean[ActivityGame.NUMBER_OF_QUERY + 1];
    private String settingPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cur_result);
        initialize();
        readResult();
    }

    private void initialize(){
        resultLayout = findViewById(R.id.show_resultLayout);
        settingPath = new File(getExternalFilesDir(ActivityGame.APP_NAME), ActivityGame.FILE_SETTING).toString();
    }

    private void readResult(){
        File root = getExternalFilesDir(ActivityGame.APP_NAME);
        if(root.exists()){
            
            for(File f : root.listFiles()){
                if(!settingPath.equals(f.getPath())){
                    try {
                        Scanner sc = new Scanner(f);
                        String string = f.toString().substring(root.toString().length() + 1);
                        Log.i("test", string);
                        while (sc.hasNextLine()){

                        }

                        sc.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}