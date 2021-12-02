package com.phy.app.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.BleEvent;
import com.phy.app.ble.OperateConstant;
import com.phy.app.ble.bean.HeartRateLastData;
import com.phy.app.ble.bean.HeartRateOriginalData;
import com.phy.app.views.CircleView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HearRateActivity
 *
 * @author:zhoululu
 * @date:2018/4/15
 */

public class HearRateActivity extends EventBusBaseActivity {

    CircleView heart;

    @Override
    public void initComponent() {
        setTitle(R.string.label_heart_rate);

        heart = findViewById(R.id.heart_rate);
        heart.startAnim();

        PHYApplication.getBandUtil().startHeartRate();

        initChart("", "",0,xMax,yMin,yMax);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BleEvent event) {
        if(OperateConstant.HEART_RATE_LAST_DATA.equals(event.getOperate())){
            HeartRateLastData data = (HeartRateLastData)event.getObject();
            heart.setData(data.getHeartRate());
        }else if(OperateConstant.HEART_RATE_DATA.equals(event.getOperate())){
            HeartRateOriginalData data = (HeartRateOriginalData) event.getObject();

            updateChart(data.getHeartRate());
        }
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_heart_rate;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        heart.stop();

        PHYApplication.getBandUtil().stopHeartRate();
    }

    //图表相关
    private XYSeries series;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chart;
    private XYMultipleSeriesRenderer renderer;
    private int yMax = 66000;//y轴最大值，根据不同传感器变化
    private int xMax = 80;//一屏显示测量次数
    private int yMin = 0;

    List<Integer> list = new ArrayList();

    /**
     * 初始化图表
     */
    private void initChart(String xTitle,String yTitle,int minX,int maxX,int minY,int maxY){
        //这里获得main界面上的布局，下面会把图表画在这个布局里面
        LinearLayout layout = (LinearLayout)findViewById(R.id.chart);
        //这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
        series = new XYSeries("历史曲线");
        //创建一个数据集的实例，这个数据集将被用来创建图表
        mDataset = new XYMultipleSeriesDataset();
        //将点集添加到这个数据集中
        mDataset.addSeries(series);

        //以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
        int lineColor = Color.RED;
        PointStyle style = PointStyle.CIRCLE;
        renderer = buildRenderer(lineColor, style, true);

        //设置好图表的样式
        setChartSettings(renderer, xTitle,yTitle,
                minX, maxX, //x轴最小最大值
                minY, maxY, //y轴最小最大值
                Color.WHITE, //坐标轴颜色
                Color.WHITE//标签颜色
        );

        //生成图表
        chart = ChartFactory.getLineChartView(this, mDataset, renderer);

        //将图表添加到布局中去
        layout.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        //设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(2);//这是线宽
        renderer.addSeriesRenderer(r);

        return renderer;
    }


    /**
     * 初始化图表
     * @param renderer
     * @param xTitle
     * @param yTitle
     * @param xMin
     * @param xMax
     * @param yMin
     * @param yMax
     * @param axesColor
     * @param labelsColor
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
                                    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        //有关对图表的渲染可参看api文档
        renderer.setChartTitle("");//设置标题
        renderer.setChartTitleTextSize(0);
        renderer.setXAxisMin(xMin);//设置x轴的起始点
        renderer.setXAxisMax(xMax);//设置一屏有多少个点
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setBackgroundColor(getResources().getColor(R.color.color_2b9247));
        renderer.setApplyBackgroundColor(true);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.BLUE);//设置格子的颜色
        renderer.setXLabels(20);//没有什么卵用
        renderer.setYLabels(10);//把y轴刻度平均分成多少个
        renderer.setLabelsTextSize(0);
        renderer.setXTitle(xTitle);//x轴的标题
        renderer.setYTitle(yTitle);//y轴的标题
        renderer.setAxisTitleTextSize(0);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setPointSize((float) 0);
        renderer.setShowLegend(false);//说明文字
        renderer.setLegendTextSize(0);
        renderer.setMargins(new int[]{0,0,0,0});//设置空白区大小

        //不显示刻度
        renderer.setShowLabels(false,false);
        //不允许缩放
        renderer.setZoomEnabled(false,false);
        //不允许拖动
        renderer.setPanEnabled(false);
    }

    private int addX = -1;
    //private double addY = 0;
    /**
     * 更新图表的函数，其实就是重绘
     */
    private void updateChart(int[] data) {

        //移除数据集中旧的点集
        mDataset.removeSeries(series);

        //判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
        int length = series.getItemCount();
        if (length > 5000) {//设置最多5000个数
            length = 5000;
        }

        for (int i=0;i<data.length;i++){
           series.add(addX++, data[i]);//最重要的一句话，以xy对的方式往里放值
           list.add(data[i]);
        }

        if(addX>xMax){
            renderer.setXAxisMin(addX-xMax);
            renderer.setXAxisMax(addX);
        }

        if(list.size() >= 80){
            list.remove(0);
            list.remove(0);
            list.remove(0);
            list.remove(0);
            list.remove(0);
            list.remove(0);
            list.remove(0);
            list.remove(0);
        }

        Integer[] test = (Integer[]) list.toArray(new Integer[list.size()]);
        Arrays.sort(test);

        renderer.setYAxisMax(test[test.length-1]+25);
        renderer.setYAxisMin(test[0]-25);

        //重要：在数据集中添加新的点集
        mDataset.addSeries(series);

        //视图更新，没有这一步，曲线不会呈现动态
        //如果在非UI主线程中，需要调用postInvalidate()，具体参考api
        chart.invalidate();
    }

}
