#ifndef WIDGET_H
#define WIDGET_H

#include <QWidget>
#include"cherrytree.h"
namespace Ui {
class Widget;
}

class Widget : public QWidget
{
    Q_OBJECT

public:
    explicit Widget(QWidget *parent = 0);
    ~Widget();

private:
    Ui::Widget *ui;
    long usetime;
protected:
    CherryTree cherryTree;
    void PaintNewCheeryTree();
    void DrawCherryTree();
    bool eventFilter(QObject *, QEvent *);

    void getPointStartAndEnd(QPoint & pstart,QPoint & pend,int & len,int wid,int hei,bool isSingleTree);

};

#endif // WIDGET_H
