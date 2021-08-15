package com.amine.knownumbers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static String STR_LONG_MAX = "9223372036854775807";
    private long userEnteredNumber;
    private Drawable drawableGray, drawableBlack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();


    }

    private void initialize(){
        findViewById(R.id.main_btnAnalyse).setOnClickListener(this);
        findViewById(R.id.main_btnDivide).setOnClickListener(this);
        findViewById(R.id.main_imgCalcImage).setOnClickListener(this);

        Resources res = getResources();
        try {
            drawableGray = Drawable.createFromXml(res, res.getXml(R.xml.shape_calc_button_gray));
            drawableBlack = Drawable.createFromXml(res, res.getXml(R.xml.shape_calc_button_black));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.mainMenu_goToGame){
            Intent i = new Intent(this, ActivityGame.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.main_btnAnalyse){
            EditText edtInputNumber = findViewById(R.id.edtInputNumber);
            String strNumber = edtInputNumber.getText().toString();
            if(strNumber.isEmpty()){
                edtInputNumber.setError("Enter a number");
                edtInputNumber.requestFocus();
                return;
            }
            if(!isInRangeOfLong(strNumber)){
                edtInputNumber.setError("Number is not in range");
                edtInputNumber.requestFocus();
                return;
            }
            makeViewsVisible();

            TextView tv = findViewById(R.id.main_txtInput);
            tv.setText(strNumber);

            EditText ed = findViewById(R.id.main_edtNumberToDivide);
            TextView tv1 = findViewById(R.id.main_txtUserResidue);
            ed.setText("");
            tv1.setText("");
            tv1.setVisibility(View.GONE);

            // TODO
            //  Start calculating everything

            long numb = Long.parseLong(strNumber);
            userEnteredNumber = numb;
            findViewById(R.id.main_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.main_infoScroll).setVisibility(View.GONE);
            getMainThread(strNumber, numb, () -> {
                findViewById(R.id.main_progress).setVisibility(View.GONE);
                findViewById(R.id.main_infoScroll).setVisibility(View.VISIBLE);

            }).start();

        }
        else if(id == R.id.main_btnDivide){
            EditText ed = findViewById(R.id.main_edtNumberToDivide);
            TextView tv = findViewById(R.id.main_txtUserResidue);
            String numToDivide = ed.getText().toString();
            if(numToDivide.isEmpty()){
                ed.setError("Give a number");
                ed.requestFocus();
                return;
            }

            long numb = Long.parseLong(numToDivide);
            String s = "Quotient = " + userEnteredNumber/numb + "\nResidue = " + userEnteredNumber%numb;

            tv.setVisibility(View.VISIBLE);
            tv.setText(s);
        }
        else if(id == R.id.main_imgCalcImage){
            CalculatorInterface calc = new CalculatorInterface(this, R.id.edtInputNumber,
                    (s, ID) -> {
                        EditText ed = findViewById(ID);
                        ed.setText(s);
                    });

            calc.show();
        }

    }

    private interface Analyse{
        void onCallback();
    }

    private Thread getMainThread(String strNumber, long numb, Analyse analyse){
        Handler handler = new Handler(getApplicationContext().getMainLooper());

        return new Thread(() -> handler.post(() -> {
            threadForPrimality(numb);
            threadForFormsOfTheNumber(strNumber, numb);
            threadForPrimeFactorization(numb);
            threadForResidues(numb);

            analyse.onCallback();
        }));
    }

    private void threadForResidues(long numb){
        LinearLayout residueLayout = findViewById(R.id.main_residueLayout);
        residueLayout.removeAllViews();
        for(int i = 1; i <= 9; i++){
            LinearLayout l = new LinearLayout(this),
                    lF = new LinearLayout(this);

            residueLayout.addView(l);
            residueLayout.addView(lF);
            l.setGravity(Gravity.CENTER);
            l.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            lF.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 7));
            lF.setBackgroundColor(Color.BLACK);
            l.setOrientation(LinearLayout.HORIZONTAL);


            TextView txtDividedBy = new TextView(this),
                    txtFH = new TextView(this),
                    txtResidue = new TextView(this);

            l.addView(txtDividedBy);
            l.addView(txtFH);
            l.addView(txtResidue);

            txtDividedBy.setTextColor(Color.RED);
            txtResidue.setTextColor(Color.RED);
            txtDividedBy.setTextSize(20);
            txtResidue.setTextSize(20);

            txtFH.setBackgroundColor(Color.BLACK);

            txtDividedBy.setGravity(Gravity.CENTER);
            txtResidue.setGravity(Gravity.CENTER);
            txtDividedBy.setLayoutParams(new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 49.5f));
            txtFH.setLayoutParams(new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            txtResidue.setLayoutParams(new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 49.5f));
            String numDividedBy, residue;
            if(i == 1){
                numDividedBy = "Divided by";
                residue = "Residue";
            }
            else{
                numDividedBy = i + "";
                residue = (numb%i) + "";
            }

            txtDividedBy.setText(numDividedBy);
            txtResidue.setText(residue);

        }

    }

    private void threadForPrimeFactorization(long numb){
        int numOfDiv = 1;
        Map<Long, Long> fact = primeFactorize(numb);
        String[] mainString = new String[fact.size() + 1],
                powers = new String[fact.size() + 1];
        int i = 0;
        for(Map.Entry<Long, Long> entry : fact.entrySet()){
            mainString[i] = entry.getKey().toString();
            powers[i] = entry.getValue().toString();
            numOfDiv *= (entry.getValue() + 1);
            i++;
        }
        StringBuilder sb = new StringBuilder();
        for(int j = 0; j < i; j++){
            String temp = mainString[j] + powers[j] + "x";
            sb.append(temp);
        }
        String tempNumb = sb.toString(),
                finalNumb = tempNumb.substring(0, tempNumb.length() - 1);
        SpannableStringBuilder cs = new SpannableStringBuilder(finalNumb);
        int start = 0, end;

        for(int j = 0; j < i; j++){
            start += mainString[j].length();
            end = (start + powers[j].length());
            cs.setSpan(new SuperscriptSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            cs.setSpan(new RelativeSizeSpan(0.75f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            start += 2;
        }


        TextView equation = findViewById(R.id.main_txtPrimeFactorization),
                txtNumbOfDiv = findViewById(R.id.txtNumberOfDiv);
        String s = numOfDiv + "";
        equation.setText(cs);
        txtNumbOfDiv.setText(s);


    }

    private Map<Long, Long> primeFactorize(long numb){
        Map<Long, Long> fact = new HashMap<>();

        for(long prime = 2; prime*prime <= numb; prime++){
            while (numb%prime == 0){
                long prev = 0;
                if(fact.containsKey(prime)) prev = fact.get(prime);
                prev++;
                fact.put(prime, prev);
                numb /= prime;

            }
        }

        if(numb >= 2) fact.put(numb, (long)1);

        return fact;
    }

    private void threadForFormsOfTheNumber(String numb, long numbD){
        String powerOfTen = (numb.length() - 1) + "", newNumb, finalNumb;
        if(numb.length() >= 2) newNumb = numb.substring(0, 1) + "." + numb.substring(1);
        else newNumb = numb;

        finalNumb = newNumb + "x" + "10" + powerOfTen;
        TextView equation = findViewById(R.id.main_txtScientificForm),
                numberLength = findViewById(R.id.main_txtNumberLength),
                binaryForm = findViewById(R.id.main_txtBinaryForm),
                evenOrOdd = findViewById(R.id.main_txtEvenOrOdd);

        SpannableStringBuilder cs = new SpannableStringBuilder(finalNumb);
        cs.setSpan(new SuperscriptSpan(), newNumb.length() + 3, finalNumb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        cs.setSpan(new RelativeSizeSpan(0.75f), newNumb.length() + 3, finalNumb.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        equation.setText(cs);

        String s = numb.length() + "";
        numberLength.setText(s);
        binaryForm.setText(decimalToBinary(numbD));
        if(numbD%2 == 0) s = "Even";
        else s = "Odd";
        evenOrOdd.setText(s);

    }

    private String decimalToBinary(long numb){
        StringBuilder bin = new StringBuilder();
        while (numb != 0){
            bin.append(numb % 2);
            numb /= 2;
        }
        return bin.reverse().toString();
    }

    private void makeViewsVisible(){
        findViewById(R.id.main_inputLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.main_infoScroll).setVisibility(View.VISIBLE);
    }

    private void threadForPrimality (long numb){
        TextView tv = findViewById(R.id.main_txtPrimeOrNot);
        boolean isPrime = CheckPrimality.millerRabin(numb);
        tv.setVisibility(View.VISIBLE);
        tv.setTextColor(Color.RED);
        String s;
        if(isPrime) s = "Prime";
        else s = "Composite";
        tv.setText(s);
    }

    private boolean isInRangeOfLong(String num){
        if(num.length() > STR_LONG_MAX.length()) return false;
        else if(num.length() < STR_LONG_MAX.length()) return true;
        else{
            for(int i = 0; i < STR_LONG_MAX.length(); i++){
                if(num.charAt(i) < STR_LONG_MAX.charAt(i)) return true;
            }
            return false;
        }
    }

    private interface CCallback{
        void onCallback(String s, int ID);
    }

    public static void logI(String s){
        Log.i("test", s);
    }
    public static void logI(int s){
        Log.i("test", s + "");
    }
    public static void logI(double s){
        Log.i("test", s + "");
    }

    private class CalculatorInterface extends Dialog implements View.OnClickListener{
        private TextView txtDisplayInputs, txtDisplayResult;
        private final int ID;
        private double result = 0;
        private String  prevOp = "", lastOp = "";
        private boolean equalClicked = false;
        private final CCallback callback;

        public CalculatorInterface(@NonNull Context context, int ID, CCallback callback) {
            super(context);
            this.callback = callback;
            this.ID = ID;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.calculator_interface);
            initialize();
        }

        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.btn0){
                if(equalClicked){
                    Toast.makeText(MainActivity.this, "Click 'AC' to clear", Toast.LENGTH_LONG).show();
                    return;
                }
                String s = txtDisplayResult.getText().toString();
                if(!s.equals("")) s = s + "0";
                txtDisplayResult.setText(s);
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

                result = 0;
                txtDisplayResult.setText("");
                txtDisplayInputs.setText("");
                equalClicked = false;
                lastOp = "";
                prevOp = "";
            }
            if(v.getId() == R.id.btnX){

                String s = txtDisplayResult.getText().toString();
                if(s.length() > 1) s = s.substring(0, s.length() - 1);
                else s = "";
                txtDisplayResult.setText(s);
            }
            if(v.getId() == R.id.btnDiv){

                operation("÷");
            }
            if(v.getId() == R.id.btnMul){

                operation("X");
            }
            if(v.getId() == R.id.btnMin){
                operation("-");
            }
            if(v.getId() == R.id.btnPlus){

                operation("+");
            }
            if(v.getId() == R.id.btnEqual){

                String prev = txtDisplayInputs.getText().toString(),
                        s = txtDisplayResult.getText().toString();
                if(s.equals("")) return;

                if(equalClicked) return;
                equalClicked = true;

                if(!prev.equals("")){
                    prev = "( " + prev + " " + s + " ) )";
                }
                else{
                    prev = "( " + s + " )";
                }
                txtDisplayInputs.setText(prev);


                switch (lastOp) {
                    case "÷":
                        result = result / Double.parseDouble(s);
                        break;
                    case "X":
                        result = result * Double.parseDouble(s);
                        break;
                    case "+":
                        result = result + Double.parseDouble(s);
                        break;
                    case "-":
                        result = result - Double.parseDouble(s);
                        break;
                    case "":
                        result = Double.parseDouble(s);
                        break;
                }

                String val = result + "";
                txtDisplayResult.setText(val);
            }
            if(v.getId() == R.id.btnPoint){
                String s = txtDisplayResult.getText().toString();
                if(!pointExistInNumber(s)) s = s + ".";
                txtDisplayResult.setText(s);
            }

            if(v.getId() == R.id.btnCalcInterfaceOk){

                String s = txtDisplayResult.getText().toString();
                if(s.equals("")) s = "0";
                int res = (int)Double.parseDouble(s);
                if(res < 0){
                    Toast.makeText(MainActivity.this, "Can't include negative numbers\n" +
                                    "Press cancel",
                            Toast.LENGTH_LONG).show();
                }else {
                    callback.onCallback(res + "", ID);
                    dismiss();
                }
            }
            if(v.getId() == R.id.btnCalcInterfaceCancel){
                dismiss();
            }

        }

        private void addDigit(String num){
            if(equalClicked){
                Toast.makeText(MainActivity.this, "Click 'AC' to clear", Toast.LENGTH_LONG).show();
                return;
            }
            String s = txtDisplayResult.getText().toString() + num;
            txtDisplayResult.setText(s);
        }
        private void operation(String op){

            if(equalClicked){
                Toast.makeText(MainActivity.this, "Click 'AC' to clear", Toast.LENGTH_LONG).show();
                return;
            }
            String curNum = txtDisplayResult.getText().toString();

            if(curNum.equals("") || curNum.equals("0")) return;
            lastOp = op;
            operate(prevOp, curNum);
        }
        private void operate(String op, String num){

            String prev = txtDisplayInputs.getText().toString();
            if(!prev.equals("")){
                prev = "( " + prev + " " + num + " ) " + lastOp;
            }
            else{
                prev = "( " + num + " " + lastOp + " ";
            }
            txtDisplayInputs.setText(prev);
            txtDisplayResult.setText("");

            switch (op) {
                case "÷":
                    result = result / Double.parseDouble(num);
                    break;
                case "X":
                    result = result * Double.parseDouble(num);
                    break;
                case "+":
                    result = result + Double.parseDouble(num);
                    break;
                case "-":
                    result = result - Double.parseDouble(num);
                    break;
                case "":
                    result = Double.parseDouble(num);
                    break;
            }
            prevOp = lastOp;

        }
        private boolean pointExistInNumber(String number){
            for(int i = 0; i < number.length(); i++){
                if(number.charAt(i) == '.'){
                    return true;
                }
            }
            return false;
        }
        private void initialize(){

            txtDisplayInputs = findViewById(R.id.txtDisplayInputs);
            txtDisplayResult = findViewById(R.id.txtDisplayResult);

            findViewById(R.id.btn0).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn0).setBackground(drawableBlack);
            findViewById(R.id.btn1).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn1).setBackground(drawableBlack);
            findViewById(R.id.btn2).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn2).setBackground(drawableBlack);
            findViewById(R.id.btn3).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn3).setBackground(drawableBlack);
            findViewById(R.id.btn4).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn4).setBackground(drawableBlack);
            findViewById(R.id.btn5).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn5).setBackground(drawableBlack);
            findViewById(R.id.btn6).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn6).setBackground(drawableBlack);
            findViewById(R.id.btn7).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn7).setBackground(drawableBlack);
            findViewById(R.id.btn8).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn8).setBackground(drawableBlack);
            findViewById(R.id.btn9).setOnClickListener(this);
            if(drawableBlack != null) findViewById(R.id.btn9).setBackground(drawableBlack);
            findViewById(R.id.btnAc).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnAc).setBackground(drawableGray);
            findViewById(R.id.btnPlus).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnPlus).setBackground(drawableGray);
            findViewById(R.id.btnMin).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnMin).setBackground(drawableGray);
            findViewById(R.id.btnMul).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnMul).setBackground(drawableGray);
            findViewById(R.id.btnDiv).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnDiv).setBackground(drawableGray);
            findViewById(R.id.btnEqual).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnEqual).setBackground(drawableGray);
            findViewById(R.id.btnPoint).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnPoint).setBackground(drawableGray);
            findViewById(R.id.btnX).setOnClickListener(this);
            if(drawableGray != null) findViewById(R.id.btnX).setBackground(drawableGray);
            findViewById(R.id.btnCalcInterfaceOk).setOnClickListener(this);
            findViewById(R.id.btnCalcInterfaceCancel).setOnClickListener(this);

        }

    }
}