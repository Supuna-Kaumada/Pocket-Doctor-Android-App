package com.uwu.cstgroupproject.pocketdoctor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BMIActivity extends AppCompatActivity
{
    private Toolbar mToolbar;

    private EditText Weight;
    private EditText Height;
    private TextView BMI_Result;
    private Button Calc_BMI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        Weight = (EditText)findViewById(R.id.bmi_weight);
        Height = (EditText)findViewById(R.id.bmi_height);
        BMI_Result = (TextView) findViewById(R.id.bmi_result);
        Calc_BMI = (Button)findViewById(R.id.cal_bmi_button);

        mToolbar = (Toolbar)findViewById(R.id.bmi_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Calculate BMI");

        Calc_BMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalculateBMI();
            }
        });

    }

    private void CalculateBMI()
    {
        String Heightst = Height.getText().toString();
        String Weightst = Weight.getText().toString();

        if(Heightst != null && Weightst != null)
        {
            Float HeightVal = Float.parseFloat(Heightst)/100;
            Float WeightVal = Float.parseFloat(Weightst);

            Float BMI = WeightVal/(HeightVal*HeightVal);

            Display_BMI(BMI);

        }
    }

    private void Display_BMI(Float bmi)
    {
        String BMI_result_label = "";

        if(Float.compare(bmi,15f) <=0)
        {
            BMI_result_label = getString(R.string.very_severely_underweight);
        }
        else if(Float.compare(bmi,15f) > 0 && Float.compare(bmi,16f) <= 0)
        {
            BMI_result_label = getString(R.string.severely_underweight);
        }
        else if(Float.compare(bmi,16f) > 0 && Float.compare(bmi,18.5f) <= 0)
        {
            BMI_result_label = getString(R.string.underweight);
        }
        else if(Float.compare(bmi,18.5f) > 0 && Float.compare(bmi,25f) <= 0)
        {
            BMI_result_label = getString(R.string.normal);
        }
        else if(Float.compare(bmi,25f) > 0 && Float.compare(bmi,30f) <= 0)
        {
            BMI_result_label = getString(R.string.overweight);
        }
        else if(Float.compare(bmi,30f) > 0 && Float.compare(bmi,35f) <= 0)
        {
            BMI_result_label = getString(R.string.obese_class_i);
        }
        else if(Float.compare(bmi,35f) > 0 && Float.compare(bmi,40f) <= 0)
        {
            BMI_result_label = getString(R.string.obese_class_ii);
        }
        else
        {
            BMI_result_label = getString(R.string.obese_class_iii);
        }

        BMI_result_label = bmi + "\n\n" + BMI_result_label;
        BMI_Result.setText(BMI_result_label);
    }
}
