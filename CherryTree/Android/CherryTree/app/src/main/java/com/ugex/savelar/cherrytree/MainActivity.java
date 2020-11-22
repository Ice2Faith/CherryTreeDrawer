package com.ugex.savelar.cherrytree;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends Activity {
    private CherryTreeView view;
    private CheckBox ckbAutoPaint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitActivity();
    }

    private void InitActivity() {
        view=findViewById(R.id.cherryView);
        ckbAutoPaint=findViewById(R.id.checkBoxAutoPaint);
        ckbAutoPaint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    view.startAutoPaintAnimate(500);
                }else{
                    view.finishAutoPaintAnimate();
                }
            }
        });
    }

    public void OnClickedRepaint(View v) {
        ckbAutoPaint.setChecked(false);
        view.repaintCherryTree();
    }
}
