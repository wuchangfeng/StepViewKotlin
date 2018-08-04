package com.itscoder.allenwu.stepviewkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.itscoder.allenwu.library.StepViewKotlin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mStepView = findViewById(R.id.step_view) as StepViewKotlin
        val steps: MutableList<String> = mutableListOf("输入手机", "验证手机", "设置密码", "注册成功")
        mStepView.setSteps(steps)

        findViewById<Button>(R.id.next).setOnClickListener(){
            var nextStep: Int = mStepView.getCurrentStep() + 1
            if (nextStep > mStepView.getStepCount()) {
                nextStep = 1
            }
            mStepView.selectedStep(nextStep)
        }
    }
}
