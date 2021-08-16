package com.amine.knownumbers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import static com.amine.knownumbers.MainActivity.logI;

public class ActivityGame extends AppCompatActivity implements View.OnClickListener {

    private TextView txtNumber1, txtNumber2, txtOp, txtRemainingTime, txtQueryCount, txtUserResultInput;
    public static final int NUMBER_OF_QUERY = 10;
    public static int timePerQuery = 0, digitsOfNumb1 = 0, digitsOfNumb2 = 0;
    public static String typeOfOperation = "+", typeOfCalc = "";
    private int maxNumb1, maxNumb2;
    private boolean isThreadAlive = false, isInnerLoopAlive = false, submitClicked = false;
    public static boolean isRightToLeftCalc = true;
    private Button btnStart, btnSubmit, btnCancel;
    private SeekBar seekQueryProgress;
    public static final String FILE_EXTENSION = ".txt", FILE_SETTING = "settings" + FILE_EXTENSION,
            APP_NAME = "Numbers";
    private File fileSetting, curFile;
    public static final int MAX_MIN_PER_QUERY = 59, MAX_SEC_PER_QUERY = 59, MAX_DIGITS_IN_NUMBERS = 9;
    private int queryCount = 0, seekBarIncrement = 0, currentQueryIndex = -1;
    private double currentResult = 0.0, curNum1, curNum2;
    private ArrayList<Double> num1, num2, timeToSolve, correctResults;
    private ArrayList<String> res;
    private double solvingTime = 0;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initialize();
        readSetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.gameMenu_setting){
            if(item.isEnabled()) {
                gotToSetting();
            }
            else{
                Toast.makeText(this, "Can't go setting while game is running",
                        Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize(){
        txtNumber1 = findViewById(R.id.game_txtNumber1);
        txtNumber2 = findViewById(R.id.game_txtNumber2);
        txtOp = findViewById(R.id.game_txtOperationSign);
        seekQueryProgress = findViewById(R.id.game_seekQueryProgress);
        txtQueryCount = findViewById(R.id.game_txtQueryCount);
        txtRemainingTime = findViewById(R.id.game_txtRemainingTime);
        txtUserResultInput = findViewById(R.id.game_txtUserResultInput);
        btnStart = findViewById(R.id.game_btnStartGame);
        btnStart.setOnClickListener(this);
        btnSubmit = findViewById(R.id.game_btnSubmit);
        btnSubmit.setOnClickListener(this);
        btnCancel = findViewById(R.id.game_btnCancel);
        btnCancel.setOnClickListener(this);
        fileSetting = new File(getExternalFilesDir(APP_NAME), FILE_SETTING);
        isThreadAlive = false;

        setOnClickToButtons();
        setButtonsDisabled();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.game_btnStartGame){
            isThreadAlive = true;
            isInnerLoopAlive = true;
            btnStart.setEnabled(false);
            btnSubmit.setEnabled(true);
            btnCancel.setEnabled(true);
            queryCount = 0;
            //queryInfo = new HashMap<>();
            optionsMenu.findItem(R.id.gameMenu_setting).setEnabled(false);
            setButtonsEnabled();
            initializeArrays();
            seekQueryProgress.setProgress(0);
            startQuery().start();

        }
        else if(id == R.id.game_btnSubmit){
            submitClicked = true;
            String strInput = txtUserResultInput.getText().toString();
            if(strInput.isEmpty()){
                Toast.makeText(this, "Enter Your Result", Toast.LENGTH_LONG).show();
                return;
            }
            double input = Double.parseDouble(strInput);

            if(currentResult == input){
                txtUserResultInput.setText("");
                String s = "Correct";
                TextView tv = findViewById(R.id.txtCorrectness);
                tv.setTextColor(Color.GREEN);
                tv.setText(s);
                seekQueryProgress.setProgress(0);
                isInnerLoopAlive = false;
            }
            else{
                String s = "Incorrect!! try again";
                TextView tv = findViewById(R.id.txtCorrectness);
                tv.setTextColor(Color.RED);
                tv.setText(s);
            }
            addArrayList(input + "");
        }
        else if(id == R.id.game_btnCancel){
            isThreadAlive = false;
            btnStart.setEnabled(true);
            btnSubmit.setEnabled(false);
            btnCancel.setEnabled(false);
            optionsMenu.findItem(R.id.gameMenu_setting).setEnabled(true);

            setButtonsDisabled();
        }
        if(v.getId() == R.id.btn0){
            addDigit("0");
        }
        if(v.getId() == R.id.btn1){
            addDigit("1");
        }
        if(v.getId() == R.id.btn2){
            addDigit("2");
        }
        if(v.getId() == R.id.btn3){
            addDigit("3");
        }
        if(v.getId() == R.id.btn4){
            addDigit("4");
        }
        if(v.getId() == R.id.btn5){
            addDigit("5");
        }
        if(v.getId() == R.id.btn6){
            addDigit("6");
        }
        if(v.getId() == R.id.btn7){
            addDigit("7");
        }
        if(v.getId() == R.id.btn8){
            addDigit("8");
        }
        if(v.getId() == R.id.btn9){
            addDigit("9");
        }
        if(v.getId() == R.id.btnAc){
            txtUserResultInput.setText("");
        }
        if(v.getId() == R.id.btnX){

            String s = txtUserResultInput.getText().toString();
            if(s.length() > 1) s = s.substring(0, s.length() - 1);
            else s = "";
            txtUserResultInput.setText(s);
        }
        if(v.getId() == R.id.btnMin){
            String s = txtUserResultInput.getText().toString();
            if(s.equals("")) txtUserResultInput.setText("-");
        }
        if(v.getId() == R.id.btnPoint){
            String s = txtUserResultInput.getText().toString();
            if(!pointExistInNumber(s)) s = s + ".";
            txtUserResultInput.setText(s);
        }
    }

    private void addArrayList(String input){
        logI(solvingTime + " " + currentQueryIndex);
        num1.set(currentQueryIndex, curNum1);
        num2.set(currentQueryIndex, curNum2);
        res.set(currentQueryIndex, input);
        timeToSolve.set(currentQueryIndex, solvingTime/(double)1000);
        correctResults.set(currentQueryIndex, currentResult);
    }

    private void initializeArrays() {
        num1 = new ArrayList<>();
        num2 = new ArrayList<>();
        res = new ArrayList<>();
        timeToSolve = new ArrayList<>();
        correctResults = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_QUERY; i++){
            num1.add(0.0);
            num2.add(0.0);
            res.add("Not submitted");
            timeToSolve.add(0.0);
            correctResults.add(0.0);
        }
    }

    private void setButtonsEnabled(){
        findViewById(R.id.btn0).setEnabled(true);
        findViewById(R.id.btn1).setEnabled(true);
        findViewById(R.id.btn2).setEnabled(true);
        findViewById(R.id.btn3).setEnabled(true);
        findViewById(R.id.btn4).setEnabled(true);
        findViewById(R.id.btn5).setEnabled(true);
        findViewById(R.id.btn6).setEnabled(true);
        findViewById(R.id.btn7).setEnabled(true);
        findViewById(R.id.btn8).setEnabled(true);
        findViewById(R.id.btn9).setEnabled(true);
        findViewById(R.id.btnAc).setEnabled(true);
        findViewById(R.id.btnMin).setEnabled(true);
        findViewById(R.id.btnPoint).setEnabled(true);
        findViewById(R.id.btnX).setEnabled(true);
        findViewById(R.id.game_btnCancel).setEnabled(true);
    }

    private void setButtonsDisabled(){
        findViewById(R.id.btn0).setEnabled(false);
        findViewById(R.id.btn1).setEnabled(false);
        findViewById(R.id.btn2).setEnabled(false);
        findViewById(R.id.btn3).setEnabled(false);
        findViewById(R.id.btn4).setEnabled(false);
        findViewById(R.id.btn5).setEnabled(false);
        findViewById(R.id.btn6).setEnabled(false);
        findViewById(R.id.btn7).setEnabled(false);
        findViewById(R.id.btn8).setEnabled(false);
        findViewById(R.id.btn9).setEnabled(false);
        findViewById(R.id.btnAc).setEnabled(false);
        findViewById(R.id.btnMin).setEnabled(false);
        findViewById(R.id.btnPoint).setEnabled(false);
        findViewById(R.id.btnX).setEnabled(false);
        findViewById(R.id.game_btnCancel).setEnabled(false);
    }

    private void setOnClickToButtons(){
        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);
        findViewById(R.id.btnAc).setOnClickListener(this);
        findViewById(R.id.btnMin).setOnClickListener(this);
        findViewById(R.id.btnPoint).setOnClickListener(this);
        findViewById(R.id.btnX).setOnClickListener(this);
    }

    private void addDigit(String digit){

        String s;
        String prev = txtUserResultInput.getText().toString();
        if(prev.length() > 0 && prev.charAt(0) == '-'){
            if(isRightToLeftCalc) s = prev.charAt(0) + digit + prev.substring(1);
            else s = prev.charAt(0) + prev.substring(1) + digit;
        }
        else{
            if(isRightToLeftCalc) s = digit + txtUserResultInput.getText().toString();
            else s = txtUserResultInput.getText().toString() + digit;

        }
        txtUserResultInput.setText(s);
    }

    private boolean pointExistInNumber(String number){
        for(int i = 0; i < number.length(); i++){
            if(number.charAt(i) == '.'){
                return true;
            }
        }
        return false;
    }

    private Thread startQuery(){
        return new Thread(() -> {

            for(int i = 1; i <= NUMBER_OF_QUERY && isThreadAlive; i++){

                TimeCountingThread tct = new TimeCountingThread();
                currentQueryIndex = i - 1;
                isInnerLoopAlive = true;
                int tempTime = timePerQuery;
                int currentSeekProgress = 0;

                int finalI = i;
                runOnUiThread(() -> {
                    seekQueryProgress.setProgress(0);
                    txtUserResultInput.setText("");
                    TextView tv = findViewById(R.id.txtCorrectness);
                    tv.setText("");

                    Random random = new Random();
                    int num1 = Math.abs(random.nextInt(maxNumb1)),
                            num2 = Math.abs(random.nextInt(maxNumb2));
                    String strNum1 = num1 + "", strNum2 = num2 + "";
                    curNum1 = num1;
                    curNum2 = num2;
                    txtOp.setText(typeOfOperation);
                    equalizeNumbers(strNum1, strNum2, num1, num2);
                    queryCount = finalI;
                    String count = "Query No: " + queryCount;
                    txtQueryCount.setText(count);
                });

                /*
                runOnUiThread(() -> {

                });

                 */

                while (tempTime > 0 && isThreadAlive && isInnerLoopAlive){
                    int sec = tempTime/1000,
                            min = sec/60;
                    int sec1 = sec%60;
                    String s = min + " : " + sec1;

                    int finalCurrentSeekProgress = currentSeekProgress;
                    runOnUiThread(() -> {
                        txtRemainingTime.setText(s);
                        seekQueryProgress.setProgress(finalCurrentSeekProgress);
                    });

                    currentSeekProgress += seekBarIncrement;

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    tempTime -= 1000;
                }
                if(!submitClicked){
                    addArrayList("Not submitted");
                }
                tct.stop();
            }
            isThreadAlive = false;
            runOnUiThread(() -> {
                btnStart.setEnabled(true);
                btnSubmit.setEnabled(false);
                btnCancel.setEnabled(false);
                txtQueryCount.setText("");
                txtOp.setText("");
                txtNumber1.setText("");
                txtNumber2.setText("");
                txtRemainingTime.setText("0:0");
                optionsMenu.findItem(R.id.gameMenu_setting).setEnabled(true);
                setButtonsDisabled();
            });
            //getCurFile(() -> saveResult(this::gotToShowCurResultActivity));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getCurFile(new Wait() {
                        @Override
                        public void onCallback() {
                            saveResult(new Wait() {
                                @Override
                                public void onCallback() {
                                    CurrentResultD crd = new CurrentResultD(ActivityGame.this);
                                    crd.show();
                                }
                            });
                        }
                    });
                }
            });
        });

    }

    private class TimeCountingThread implements Runnable{
        private volatile boolean isTimeCountingThreadAlive;

        TimeCountingThread() {
            Thread t = new Thread(this);
            isTimeCountingThreadAlive = true;
            t.start();
        }

        @Override
        public void run() {
            solvingTime = 0;
            while (isTimeCountingThreadAlive){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Toast.makeText(ActivityGame.this,
                            e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                solvingTime++;
            }
        }

        public void stop(){
            isTimeCountingThreadAlive = false;
        }
    }

    private void gotToShowCurResultActivity(){

        Intent i = new Intent(this, ShowCurResultActivity.class);
        startActivity(i);
    }

    private void getCurFile(Wait wait){

        Calendar calendar = Calendar.getInstance();
        String time = calendar.getTime().toString(),
                newFile = time.substring(0, 19) + " " + time.substring(time.length() - 4) + FILE_EXTENSION;
        newFile = newFile.substring(0, 13) + "_" + newFile.substring(14, 16) + "_" + newFile.substring(17);

        logI(newFile);

        curFile = new File(getExternalFilesDir(APP_NAME), newFile);

        if(!curFile.exists()){
            try {
                if (!curFile.createNewFile()) {
                    Toast.makeText(this, "Problem of creating file:  " + newFile,
                            Toast.LENGTH_LONG).show();
                }
                wait.onCallback();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "database already exists!\nCheck your mobile date and time",
                    Toast.LENGTH_LONG).show();
            wait.onCallback();
        }
    }

    private interface Wait{
        void onCallback();
    }

    private void saveResult(Wait wait){
        try {
            if(curFile == null || !curFile.exists()){
                Toast.makeText(this, "Current database not created!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            PrintWriter pr = new PrintWriter(curFile);

            for(int i = 0; i < num1.size(); i++){
                pr.print(num1.get(i));
                pr.print(" ");

                pr.print(num2.get(i));
                pr.print(" ");

                pr.print(res.get(i));
                pr.print(" ");

                pr.print(correctResults.get(i));
                pr.print(" ");

                pr.println(timeToSolve.get(i));
            }

            pr.close();

            wait.onCallback();


        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void setCurrentResult(int n1, int n2){
        switch (typeOfOperation) {
            case "+":
                currentResult = n1 + n2;
                break;
            case "-":
                currentResult = n1 - n2;
                break;
            case "x":
                currentResult = n1 * n2;
                break;
            case "/":
                currentResult = (double) n1 / (double) n2;
                break;
        }
    }

    private void equalizeNumbers(String numb1, String numb2, int n1, int n2){
        StringBuilder sb = new StringBuilder();
        if(n1 < n2){
            String temp = numb1;
            int tn = n1;

            n1 = n2;
            n2 = tn;

            numb1 = numb2;
            numb2 = temp;
        }
        setCurrentResult(n1, n2);
        int lenLes = numb1.length() - numb2.length();
        for(int i = 1; i <= lenLes; i++) sb.append("0");
        txtNumber1.setText(numb1);
        txtNumber1.setText(numb1);
        txtNumber2.setText((sb.toString() + numb2));

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

                    if(strDigit1.length() >= MAX_DIGITS_IN_NUMBERS ||
                        strDigit2.length() >= MAX_DIGITS_IN_NUMBERS){
                        gotToSetting();
                    }
                    else{
                        digitsOfNumb1 = Integer.parseInt(strDigit1);
                        digitsOfNumb2 = Integer.parseInt(strDigit2);
                        timePerQuery = Integer.parseInt(strTime);
                        typeOfOperation = strType;
                        seekBarIncrement = seekQueryProgress.getMax()/(timePerQuery/1000);
                        makeMaxNumbers();
                    }
                }
                else{
                    Toast.makeText(this, "Something is wrong with settings" +
                            "\nPlease choose settings again", Toast.LENGTH_LONG).show();
                    gotToSetting();
                }
                sc.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            gotToSetting();
        }
    }

    private void gotToSetting(){
        Intent i = new Intent(this, GameSettingActivity.class);
        startActivity(i);
    }

    private void makeMaxNumbers(){
        StringBuilder sb = new StringBuilder();

        if(digitsOfNumb1 == 0) return;
        for(int i = 1; i <= digitsOfNumb1; i++){
            sb.append("9");
        }
        maxNumb1 = Integer.parseInt(sb.toString()) + 1;

        sb = new StringBuilder();
        for(int i = 1; i <= digitsOfNumb2; i++){
            sb.append("9");
        }
        maxNumb2 = Integer.parseInt(sb.toString()) + 1;
    }

    private class CurrentResultD extends Dialog {

        public CurrentResultD(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.current_result_dialog2);
            initialize();
        }

        private void initialize(){

            TextView txtOverView = findViewById(R.id.txtOverView);
            double[] results = new double[4];
            Arrays.fill(results, 0);

            getAverageTime(results);
            initializeAdapter();

            String s = "Total Query: " + results[0] + "\nCorrect: "
                    + results[1] + "\nWrong: " + results[2]
                    + "\nAverage solving time: " + results[3];


            txtOverView.setText(s);

        }
        private void initializeAdapter(){
            ListView lstCurResItem = findViewById(R.id.lstCurResItem);
            BaseAdapter adapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return res.size();
                }

                @Override
                public Object getItem(int position) {
                    return res.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View view, ViewGroup parent) {

                    if(view == null){
                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.current_result_dialog, null);
                    }

                    TextView txtNum1 = view.findViewById(R.id.txtNum),
                            txtRes = view.findViewById(R.id.txtRes),
                            txtSolvingTime = view.findViewById(R.id.txtSolvingTime),
                            txtIsCorrect = view.findViewById(R.id.txtIsCorrect);

                    String s = "First Number: " + num1.get(position) + "\nSecond Number: " + num2.get(position);
                    txtNum1.setText(s);
                    s = "Your input: " + res.get(position) + "\n" +
                            "Correct result: " + correctResults.get(position);

                    txtRes.setText(s);
                    boolean isNumb = isNumber(res.get(position));
                    if(isNumb){
                        if(Double.parseDouble(res.get(position)) == correctResults.get(position)){
                            s = "Status: Correct";
                        }
                        else s = "Status: Wrong";

                    }
                    else{
                        s = "Status: Wrong";
                    }
                    txtIsCorrect.setText(s);
                    s = "Solving time: " + timeToSolve.get(position);
                    txtSolvingTime.setText(s);
                    return view;
                }
            };
            lstCurResItem.setAdapter(adapter);
        }
    }

    private boolean isNumber(String s) {
        for(int i = 0; i < s.length(); i++){
            if(!(s.charAt(i) >= '0' && s.charAt(i) <= '9')) if(s.charAt(i) != '.') return false;
        }
        return true;
    }

    private void getAverageTime(double[] results){
        double d = 0;

        for(int i = 0; i < timeToSolve.size(); i++){
            results[0]++;
            if(isNumber(res.get(i))){
                if(Double.parseDouble(res.get(i)) == correctResults.get(i)){
                    results[1]++;
                }
                else results[2]++;
            }
            else{
                results[2]++;
            }

            d += timeToSolve.get(i);
        }

        if(timeToSolve.size() != 0) results[3] = d/timeToSolve.size();
        else results[3] = -1;

    }
}