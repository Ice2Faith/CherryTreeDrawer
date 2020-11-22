package com.ugex.savelar.cherrytree;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

public class CherryTree {
    private int m_width;//画布尺寸
    private int m_height;
    private int m_level;//最大树高度层次
    private int m_BoleWidth;//最大数粗度
    private int m_widthSubStep;//每个树干递减的宽度
    private int m_maxBoleBranch;//最大最小树枝数量
    private int m_minBoleBranch;
    private double m_cropRate;//树枝被剪裁的概率
    private int m_bgColor;//画布背景颜色ARGB8888 0xff77ff00 Color.argb(255,255,0,0)
    private Bitmap m_map;//画布
    private Random rand;

    public CherryTree setBoleWidth(int m_BoleWidth) {
        this.m_BoleWidth = m_BoleWidth;
        return this;
    }

    public CherryTree setWidthSubStep(int m_widthSubStep) {
        this.m_widthSubStep = m_widthSubStep;
        return this;
    }

    public CherryTree setMaxBoleBranch(int m_maxBoleBranch) {
        this.m_maxBoleBranch = m_maxBoleBranch;
        return this;
    }

    public CherryTree setMinBoleBranch(int m_minBoleBranch) {
        this.m_minBoleBranch = m_minBoleBranch;
        return this;
    }

    public CherryTree setCropRate(double m_cropRate) {
        this.m_cropRate = m_cropRate;
        return this;
    }

    public CherryTree(){
        InitDefaultArgument();
        setSize(720,1080);
    }
    //产生一个这样大小的画布
    public CherryTree(int width,int height) {
        InitDefaultArgument();
        setSize(width,height);
    }
    public CherryTree setSize(int width,int height){
        m_width=width;
        m_height=height;
        return this;
    }
    public CherryTree setLevel(int level){
        this.m_level=level;
        return this;
    }
    //获取绘制的图片
    public Bitmap getPixmap(){
        return m_map;
    }
    //绘制一张树的图片并返回，需要指定主干的起点和终点
    public Bitmap drawCherryTree(Point startPoint, Point endPoint){
        CreateMap();
        Canvas painter=new Canvas(m_map);
        GCherryTree(painter,startPoint,endPoint,m_BoleWidth,m_level);
        return m_map;
    }
    //计算两点构成的直线的弧度
    public double getLineDirection(Point  startPoint, Point  endPoint){
        return Math.atan2(endPoint.y-startPoint.y*1.0,endPoint.x-startPoint.x*1.0);
    }
    //根据直线长度和方向，计算相对起点的终点
    public Point getEndPoint(Point  startPoint,int length,double direction){
        return new Point((int)(startPoint.x+length*Math.cos(direction)),(int)(startPoint.y+length*Math.sin(direction)));
    }

    private double getDistance(Point pstart, Point pend) {
        return Math.sqrt(Math.pow(pstart.x-pend.x*1.0,2.0)+Math.pow(pstart.y-pend.y*1.0,2.0));
    }
    //弧度角度转换
    public double AngleToRadian(double angle){
        return angle/180*Math.PI;
    }
    public double RadianToAngle(double radian){
        return radian/Math.PI*180;
    }

    //初始化默认参数
    private void InitDefaultArgument(){
        m_level=15;
        m_BoleWidth=8;
        m_bgColor=Color.argb(0,255,255,255);
        m_widthSubStep=1;
        m_minBoleBranch=2;
        m_maxBoleBranch=2;
        m_cropRate=0.05;
        rand=new Random();
    }
    //创建画布
    private void CreateMap(){
        m_map=Bitmap.createBitmap(m_width,m_height,Bitmap.Config.ARGB_8888);
        Paint paint=new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(m_bgColor);
        paint.setStyle(Paint.Style.FILL);
        new Canvas(m_map).drawRect(0,0,m_width,m_height,paint);
    }
    //绘制树入口
    private void GCherryTree(Canvas  painter,Point pstart,Point pend,int width,int level){
        drawBole(painter, pstart, width, pend);
        double direct=getLineDirection(pstart,pend);
        GCherryTreeNext(painter,pend, (int)getDistance(pstart, pend),direct,width-m_widthSubStep,level-1);

    }

    //递归绘制
    private void GCherryTreeNext(Canvas  painter,Point pstart,int length,double direct,int width,int level){
        if(level==0)
        {
            drawLeaf(painter, pstart);
            return;
        }
        if(rand.nextDouble()<m_cropRate){
            drawCropBole(painter, pstart);
            return;
        }
        if(width<=1)
            width=1;

        int count=((m_maxBoleBranch==m_minBoleBranch)? (m_minBoleBranch):(rand.nextInt()%(m_maxBoleBranch-m_minBoleBranch)+m_minBoleBranch));
        for(int i=0;i<count;i++)
        {
            int plen = (int)getNextLen(length);
            double pdirect = getNextDirect(direct);
            Point pend=getEndPoint(pstart,plen,pdirect);;
            drawBole(painter, pstart, width, pend);
            GCherryTreeNext(painter,pend,plen,pdirect,width-m_widthSubStep,level-1);
        }
    }

    private void drawCropBole(Canvas painter, Point pstart) {
        Paint paint=new Paint();
        paint.setColor(Color.rgb(0, 0,255));
        paint.setStrokeWidth(4);
        painter.drawPoint(pstart.x,pstart.y,paint);
    }

    private void drawLeaf(Canvas painter, Point pstart) {
        Paint paint=new Paint();
        if(rand.nextInt(100)<80) {
            paint.setColor(Color.rgb(rand.nextInt(55) + 200, rand.nextInt(55) + 200 - rand.nextInt(120), rand.nextInt(55) + 200));
        }else{
            paint.setColor(Color.rgb(rand.nextInt(55) + 200, 0,0));
        }
        paint.setStrokeWidth(4);
        painter.drawPoint(pstart.x,pstart.y,paint);
    }

    private double getNextLen(int length) {
        return (length*((rand.nextInt(20)+65)/100.0));
    }

    private double getNextDirect(double direct) {
        double pfactor=AngleToRadian(1.0*rand.nextInt(45));
        if(rand.nextInt(2)==1)
        {
            pfactor=0.0-pfactor;
        }
        return direct-pfactor;
    }

    private void drawBole(Canvas painter, Point pstart, int width, Point pend) {
        Paint paint = new Paint();
        paint.setStrokeWidth(width);
        paint.setColor(Color.argb(255, rand.nextInt(120), rand.nextInt(255), rand.nextInt(120)));
        painter.drawLine(pstart.x, pstart.y, pend.x, pend.y, paint);
    }

}
