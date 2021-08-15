package com.amine.knownumbers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import static com.amine.knownumbers.ActivityGame.MAX_DIGITS_IN_NUMBERS;
import static com.amine.knownumbers.ActivityGame.digitsOfNumb1;
import static com.amine.knownumbers.ActivityGame.digitsOfNumb2;
import static com.amine.knownumbers.ActivityGame.isRightToLeftCalc;
import static com.amine.knownumbers.ActivityGame.timePerQuery;
import static com.amine.knownumbers.ActivityGame.typeOfCalc;
import static com.amine.knownumbers.ActivityGame.typeOfOperation;

public class GameSettingActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    public static final String[] types = {"Select", "+", "-", "x", "/"};
    public static final String[] calcType = {"Select", "Right to left", "Left to right"};
    private Spinner spinner, spnCalcType;
    private File fileSetting;
    private EditText edtDigits1, edtDigits2, edtMin, edtSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);
        initialize();
        readSetting();
    }

    private void initialize(){
        spinner = findViewById(R.id.setting_spnTypeOfOp);
        spnCalcType = findViewById(R.id.setting_spnTypeOfCalc);
        spinner.setOnItemSelectedListener(this);
        spnCalcType.setOnItemSelectedListener(this);
        edtDigits1 = findViewById(R.id.setting_edtDigitNum1);
        edtDigits2 = findViewById(R.id.setting_edtDigitNum2);
        edtMin = findViewById(R.id.setting_edtRemMin);
        edtSec = findViewById(R.id.setting_edtRemSec);
        findViewById(R.id.setting_btnSave).setOnClickListener(this);
        fileSetting = new File(getExternalFilesDir(ActivityGame.APP_NAME), ActivityGame.FILE_SETTING);
        initializeSpinner();
    }

    private void readSetting(){
        if(fileSetting.exists()){
            try {
                Scanner sc = new Scanner(fileSetting);
                if(sc.hasNextLine()) {

                    String strDigit1 = sc.nextLine(),
                            strDigit2 = sc.nextLine(),
                            strTime = sc.nextLine(),
                            strType = sc.nextLine();
                    typeOfCalc = sc.nextLine();

                    isRightToLeftCalc = typeOfCalc.equals("Right to left");

                    digitsOfNumb1 = Integer.parseInt(strDigit1);
                    digitsOfNumb2 = Integer.parseInt(strDigit2);
                    timePerQuery = Integer.parseInt(strTime);
                    typeOfOperation = strType;

                    edtDigits1.setText(strDigit1);
                    edtDigits2.setText(strDigit2);

                    int sec = timePerQuery/1000,
                            min = sec/60;
                    sec %= 60;
                    String s = min + "";
                    edtMin.setText(s);
                    s = sec + "";
                    edtSec.setText(s);
                }
                sc.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                if(!fileSetting.createNewFile()){
                    Toast.makeText(this, "Problem to create files", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            digitsOfNumb1 = 0;
            digitsOfNumb2 = 0;
            timePerQuery = 0;
            typeOfOperation = "";

            edtDigits1.setText("0");
            edtDigits2.setText("0");
            edtMin.setText(timePerQuery/60);
            edtSec.setText(timePerQuery%60);
        }
    }

    private void initializeSpinner() {
        ArrayAdapter<String> aa =
                new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);


        ArrayAdapter<String> bb =
                new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, calcType);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCalcType.setAdapter(bb);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.setting_btnSave){
            if(typeOfOperation.isEmpty() || typeOfOperation.equals("Select")){
                spinner.requestFocus();
                Toast.makeText(this, "Select operation", Toast.LENGTH_LONG).show();
                return;
            }
            if(typeOfCalc.isEmpty() || typeOfCalc.equals("Select")){
                Toast.makeText(this, "Select calculation type", Toast.LENGTH_LONG).show();
                return;
            }

            String strDigit1 = edtDigits1.getText().toString(),
                    strDigit2 = edtDigits2.getText().toString(),
                    strMin = edtMin.getText().toString(),
                    strSec = edtSec.getText().toString();
            if(strDigit1.isEmpty()){
                edtDigits1.setError("Enter number of digits");
                edtDigits1.requestFocus();
                return;
            }
            if(strDigit2.isEmpty()){
                edtDigits2.setError("Enter number of digits");
                edtDigits2.requestFocus();
                return;
            }

            if(strMin.isEmpty()){
                edtMin.setError("Enter minutes");
                edtMin.requestFocus();
                return;
            }
            if(strSec.isEmpty()){
                edtSec.setError("Enter seconds");
                edtSec.requestFocus();
                return;
            }

            if(strDigit1.length() > MAX_DIGITS_IN_NUMBERS || strDigit2.length() > MAX_DIGITS_IN_NUMBERS){
                edtDigits1.setError("Number of digits can't be greater than 9");
                edtDigits1.requestFocus();
                return;
            }
            if(Integer.parseInt(strMin) > ActivityGame.MAX_MIN_PER_QUERY){
                edtMin.setError("You can choose " + ActivityGame.MAX_MIN_PER_QUERY + " minutes at MAX");
                edtMin.requestFocus();
                return;
            }
            if(Integer.parseInt(strSec) > ActivityGame.MAX_SEC_PER_QUERY){
                edtSec.setError("You can choose " + ActivityGame.MAX_MIN_PER_QUERY + " second at MAX");
                edtSec.requestFocus();
                return;
            }

            digitsOfNumb1 = Integer.parseInt(strDigit1);
            digitsOfNumb2 = Integer.parseInt(strDigit2);
            timePerQuery = (Integer.parseInt(strMin)*60*1000) + (Integer.parseInt(strSec)*1000);

            try {
                writeFile();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void writeFile() throws FileNotFoundException {
        if(fileSetting.exists()){
            PrintWriter pr = new PrintWriter(fileSetting);
            pr.println(digitsOfNumb1);
            pr.println(digitsOfNumb2);
            pr.println(timePerQuery);
            pr.println(typeOfOperation);
            pr.println(typeOfCalc);
            pr.close();
        }
        goToGameActivity();
    }

    private void goToGameActivity(){
        Intent i = new Intent(GameSettingActivity.this, ActivityGame.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId() == R.id.setting_spnTypeOfOp){
            typeOfOperation = types[position];
        }
        if(parent.getId() == R.id.setting_spnTypeOfCalc){
            typeOfCalc = calcType[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}