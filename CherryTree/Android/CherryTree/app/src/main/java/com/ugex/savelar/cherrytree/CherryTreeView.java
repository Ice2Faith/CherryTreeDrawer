package com.ugex.savelar.cherrytree;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CherryTreeView extends View {
    private CherryTree cherryTree=new CherryTree();
    private Bitmap map;
    public CherryTreeView(Context context) {
        super(context);
    }

    public CherryTreeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isFirstDraw=true;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isFirstDraw){
            map= cherryTree
                    .setSize(getWidth(),getHeight())
                    .setLevel(15)
                    .drawCherryTree(new Point(this.getWidth()/2,this.getHeight()),new Point(this.getWidth()/2,this.getHeight()*4/5));
            isFirstDraw=false;
        }
        canvas.drawBitmap(map,0,0,null);
    }
    public void repaintCherryTree(){
        map= cherryTree
                .setSize(getWidth(),getHeight())
                .setLevel(15)
                .drawCherryTree(new Point(this.getWidth()/2,this.getHeight()),new Point(this.getWidth()/2,this.getHeight()*4/5));
        this.invalidate();
    }

    public Bitmap getBitmap(){
        if(needRepaint)
            return null;
        return map;
    }

    private boolean needRepaint=true;
    public void startAutoPaintAnimate(final int sleepMillSecond){
        needRepaint=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(needRepaint){
                    try {
                        Thread.sleep(sleepMillSecond);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    repaintCherryTree();
                }
            }
        }).start();
    }
    public void finishAutoPaintAnimate(){
        needRepaint=false;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        needRepaint=false;
    }
}
