package ncToPicture;

import lombok.extern.slf4j.Slf4j;
import nc.Contour2D;
import nc.WktData;
import org.apache.commons.compress.utils.Lists;
import org.apache.shiro.util.CollectionUtils;
import wcontour.global.Border;
import wcontour.global.PointD;
import wcontour.global.PolyLine;
import wcontour.global.Polygon;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 等值面处理类
 */

@Slf4j
public class EquiSurfaceHandler {

    private double undefData = -9999.0;

    /**
     * 等值面绘画
     * 1、边界值列表
     * 2、生成轮廓
     * 3、生成等值线
     * 4、平滑处理（3次B样条插值）
     * 5、生成多边形
     * @param _data
     * @param _xlon
     * @param _xlat
     * @param _flag
     * @param _interval
     * @return
     */
    public List<WktData> equiSurface(double[][] _data, double[][] _xlon
            , double[][] _xlat , int[][] _flag, double[] _interval ){
        // 等值线
        List<PolyLine> cPolylineList = Lists.newArrayList();

        // 等值面数量，即数据间隔数
        int nc = _interval.length;

        // 根据边界值列表生成轮廓 List<PointD>
        System.out.println("开始根据边界值列表生成轮廓");
        List<Border> _borders = Contour2D.tracingBorders(_data, _xlon, _xlat, _flag, undefData);

        //生成等值线
        System.out.println("开始生成等值线");
        cPolylineList = Contour2D.tracingContourLines(_data, _xlon, _xlat, nc, _interval, undefData, _borders, _flag);

        // 三次B样插值 平滑处理
        System.out.println("开始平滑处理");
        cPolylineList = Contour2D.smoothLines(cPolylineList);

        // 生成多边形
        System.out.println("开始生成多边形");
        List<Polygon> cPolygonList = Contour2D.tracingPolygons(_data, cPolylineList, _borders, _interval);

        // 等值面Geometry对象转换成wkt
        if(CollectionUtils.isEmpty(cPolygonList)){
            return null;
        }

        List<WktData> wktds = Lists.newArrayList();
        WktData wktd = null;
        for(Polygon polygon : cPolygonList){
            wktd = new WktData();

            // 获取外轮廓坐标点
            List<PointD> outPoint = polygon.OutLine.PointList;
            // 转成字符串
            String outStri = pointD2String(outPoint);
            // 获取内部空洞轮廓线
            List<PolyLine> holeLines = polygon.HoleLines;
            // 待封装内部空洞轮廓字符串容器， (),(),()
            String holeStr = null;
            if(null != holeLines && holeLines.size()>0){
                holeStr = holeLines.stream().filter(t-> !t.PointList.isEmpty())
                        .map(t-> pointD2String(t.PointList)).collect(Collectors.joining(","));
            }
            String wkt = "POLYGON(";
            if(null == holeStr || holeStr.isEmpty()){
                // POLYGON((闭环边界))
                wkt = wkt.concat(outStri).concat(")");
            }else{
                // POLYGON((闭环边界),(闭环孔),(闭环孔)...)
                wkt = wkt.concat(outStri).concat(",").concat(holeStr).concat(")");
            }
            wktd.setWkt(wkt);
            wktd.setValue(polygon.LowValue);
            wktds.add(wktd);
        }

        return wktds;
    }

    /**
     * 等值面绘画
     * 1、边界值列表
     * 2、生成轮廓
     * 3、生成等值线
     * 4、平滑处理（3次B样条插值）
     * 5、生成多边形
     * @param _data
     * @param _xlon
     * @param _xlat
     * @param _flag
     * @param _interval
     * @return
     */
    public List<Polygon> equiSurface2(double[][] _data, double[][] _xlon
            , double[][] _xlat , int[][] _flag, double[] _interval ){
        // 等值线
        List<PolyLine> cPolylineList = Lists.newArrayList();

        // 等值面数量，即数据间隔数
        int nc = _interval.length;

        // 根据边界值列表生成轮廓 List<PointD>
        System.out.println("开始根据边界值列表生成轮廓");
        List<Border> _borders = Contour2D.tracingBorders(_data, _xlon, _xlat, _flag, undefData);

        //生成等值线
        System.out.println("开始生成等值线");
        cPolylineList = Contour2D.tracingContourLines(_data, _xlon, _xlat, nc, _interval, undefData, _borders, _flag);

        // 三次B样插值 平滑处理
        System.out.println("开始平滑处理");
        cPolylineList = Contour2D.smoothLines(cPolylineList);

        // 生成多边形
        System.out.println("开始生成多边形");
        List<Polygon> cPolygonList = Contour2D.tracingPolygons(_data, cPolylineList, _borders, _interval);

        // 等值面Geometry对象转换成wkt
        if(CollectionUtils.isEmpty(cPolygonList)){
            return Collections.emptyList();
        }
        return cPolygonList;
    }

    /**
     * 将pointD转换成字符串
     * @param pointD
     * @return
     */
    private String pointD2String(List<PointD> pointD){
        if(null == pointD || pointD.size() == 0){
            return null;
        }

        String result = pointD.stream().map(t->{
            Double X = t.X;
            Double Y = t.Y;
            return new StringBuffer(X.toString()).append(" ").append(Y.toString());
        }).collect(Collectors.joining(",", "(", ")"));

        return result;
    }

}
