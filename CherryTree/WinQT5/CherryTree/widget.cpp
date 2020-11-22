#include "widget.h"
#include "ui_widget.h"
#include<QFileDialog>
#include<QMessageBox>
#include<windows.h>
Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget)
{
    ui->setupUi(this);
    this->setWindowTitle("CherryTree dev Ugex");
    ui->widgetPaintArea->installEventFilter(this);
    ui->spinBoxLevel->setRange(1,100);
    ui->spinBoxLevel->setValue(15);
    ui->spinBoxMainWidth->setRange(1,100);
    ui->spinBoxMainWidth->setValue(8);
    ui->spinBoxMainRate->setRange(2,100);
    ui->spinBoxMainRate->setValue(4);
    ui->spinBoxMinBranch->setRange(1,100);
    ui->spinBoxMinBranch->setValue(2);
    ui->spinBoxMaxBranch->setRange(1,100);
    ui->spinBoxMaxBranch->setValue(2);
    ui->spinBoxWidthSub->setRange(0,100);
    ui->spinBoxWidthSub->setValue(1);
    ui->doubleSpinBoxPruneRate->setRange(0.0,1.0);
    ui->doubleSpinBoxPruneRate->setDecimals(4);
    ui->doubleSpinBoxPruneRate->setSingleStep(0.01);
    ui->doubleSpinBoxPruneRate->setValue(0.0);

    ui->spinBoxTreeCount->setRange(1,50);
    ui->spinBoxTreeCount->setValue(1);

    ui->comboBoxDirect->addItem("上");
    ui->comboBoxDirect->addItem("左");


    /*
        这里函数指针需要强转：
        QSpinBox::valueChanged
        转换类型为：void(QSpinBox::*)(int)
        推荐转换方式:
        static_cast<void(QSpinBox::*)(int)>(&QSpinBox::valueChanged)
    */
    connect(ui->spinBoxMaxBranch,(void(QSpinBox::*)(int))&QSpinBox::valueChanged,this,[=](int val){
        if(val<ui->spinBoxMinBranch->value())
        {
            ui->spinBoxMaxBranch->setValue(ui->spinBoxMinBranch->value());
        }
    });

    connect(ui->spinBoxWidthSub,(void(QSpinBox::*)(int))&QSpinBox::valueChanged,this,[=](int val){
        if(val>ui->spinBoxMainWidth->value())
        {
            ui->spinBoxWidthSub->setValue(ui->spinBoxMainWidth->value());
        }
    });

    connect(ui->pushButtonRePaint,&QPushButton::clicked,this,[=](){
        PaintNewCheeryTree();
        ui->widgetPaintArea->update();
    });
    connect(ui->pushButtonSave,&QPushButton::clicked,this,[=](){
        QString filename=QFileDialog::getSaveFileName(this,"请输入保存图片的文件名");
        if(filename.isEmpty())
        {
            QMessageBox::warning(this,"Warnning","not select file,operate cancel.");
            return;
        }
        cherryTree.getPixmap().save(filename,"PNG");
    });

}

Widget::~Widget()
{
    delete ui;
}
void Widget::getPointStartAndEnd(QPoint & pstart,QPoint & pend,int & len,int wid,int hei,bool isSingleTree)
{
    int directIndex=ui->comboBoxDirect->currentIndex();
    int rootSpace=0;
    switch(directIndex)
    {
        case 0:
        len=hei*1/ui->spinBoxMainRate->value();
        if(isSingleTree)
        {
            pstart.setX(wid/2);
            pstart.setY(hei);

            pend.setX(pstart.x());
            pend.setY(hei-len);
        }
        else
        {
            rootSpace=wid*0.75;
            pstart.setX(wid/2+(rand()%rootSpace-(rootSpace/2)));
            pstart.setY(hei);

            pend.setX(pstart.x());
            pend.setY(hei-len);
        }

        break;
        case 1:
        len=wid*1/ui->spinBoxMainRate->value();

        if(isSingleTree)
        {
            pstart.setX(0);
            pstart.setY(hei*1/3);

            pend.setX(len);
            pend.setY(pstart.y());
        }
        else
        {
            rootSpace=hei*0.75;
            pstart.setX(0);
            pstart.setY(hei/2+(rand()%rootSpace-(rootSpace/2)));

            pend.setX(len);
            pend.setY(pstart.y());
        }
        break;
    }
}
void Widget::PaintNewCheeryTree(){
    int wid=ui->widgetPaintArea->width();
    int hei=ui->widgetPaintArea->height();

    cherryTree
            .setSize(wid,hei)
            .setLevel(ui->spinBoxLevel->value())
            .setMainBoleWidth(ui->spinBoxMainWidth->value())
            .setBoleSubStep(ui->spinBoxWidthSub->value())
            .setMinBoleBranch(ui->spinBoxMinBranch->value())
            .setMaxBoleBranch(ui->spinBoxMaxBranch->value())
            .setPruneRate(ui->doubleSpinBoxPruneRate->value())
            .CreateMap();
    long beftime=GetTickCount();

    int len=0;
    int treeCount=ui->spinBoxTreeCount->value();
    if(treeCount==1)
    {
        QPoint pstart;
        QPoint pend;
        getPointStartAndEnd(pstart,pend,len,wid,hei,true);

        double direct=cherryTree.getLineDirection(pstart,pend);
        direct+=cherryTree.AngleToRadian(rand()%30-15);
        pend=cherryTree.getEndPoint(pstart,len,direct);
       cherryTree.drawCherryTree(pstart,pend);
    }
    else
    {
        for(int i=0;i<treeCount;i++)
        {
            QPoint pstart;
            QPoint pend;
            getPointStartAndEnd(pstart,pend,len,wid,hei,false);

            double direct=cherryTree.getLineDirection(pstart,pend);
            direct+=cherryTree.AngleToRadian(rand()%30-15);
            pend=cherryTree.getEndPoint(pstart,len,direct);
           cherryTree.drawCherryTree(pstart,pend);
        }
    }
    long nowtime=GetTickCount();
    usetime=nowtime-beftime;
}

void Widget::DrawCherryTree()
{
    QPainter rpainter(ui->widgetPaintArea);
    rpainter.drawPixmap(0,0,cherryTree.getPixmap());
    char buffer[1024]={0};
    sprintf(buffer,"树枝：%ld 树叶：%ld 修剪：%ld 用时：%ldms\0",cherryTree.getCountBole(),cherryTree.getCountLeaf(),cherryTree.getCountPrune(),usetime);
    ui->labelDrawInfo->setText(buffer);
}

bool Widget::eventFilter(QObject * obj, QEvent * e)
{
    if(ui->widgetPaintArea==obj && e->type()==QEvent::Show)
    {
        PaintNewCheeryTree();
    }
    if(ui->widgetPaintArea==obj && e->type()==QEvent::Paint)
    {
        DrawCherryTree();
        return true;
    }
    return QWidget::eventFilter(obj,e);
}


