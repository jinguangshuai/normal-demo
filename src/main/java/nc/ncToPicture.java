package nc;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import wcontour.Contour;
import wcontour.global.Border;
import wcontour.global.PointD;
import wcontour.global.PolyLine;
import wcontour.global.Polygon;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ncToPicture.*;

/**
 * @Auther：jinguangshuai
 * @Data：2023/3/14 - 03 - 14 - 10:03
 * @Description:nc
 * @version:1.0
 */
@Slf4j
public class ncToPicture {

    //生成等值线主方法
    public static Map<String, Object> isolineProcess() throws IOException {
        Map<String, Object> resMap = new HashMap<String, Object>();
        String filePath = "C:\\Users\\JGS\\Desktop\\coldWave_2023041308_2023041608.nc";//文件路径
        String element = "ColdWaveAlert";//nc变量名
        int depthIndex = 1;//nc文件深度序列
        int timeIndex = 1;//nc文件时间序列
        //获取NC的数据
        Map map = getNcData(filePath);
        if(map == null || map.size()==0){
            return resMap;
        }
        //等值线等值面间隔,间隔越小:过渡越均匀,性能消耗越大,响应越慢
        double[] dataInterval = new double[]{0,1,2,3,4, 5,6,7,8,9, 10,11,12,13,14,15,
                16,17,18,19, 20,21,22,23,24, 25,26,27,28,29, 30, 35};
        String strGeojson = nc2EquiSurface(map, dataInterval);
        resMap.put("geojson", strGeojson);
        resMap.put("dataArr", map.get("eleData"));
        return resMap;
    }

    // 获取nc数据
    public static <T> Map getNcData(String ncpath) throws IOException {
        String lonVarName = "XLONG";//经度变量
        String latVarName = "XLAT";//纬度变量
        //加载nc文件
        NetcdfFile ncfile =  NetcdfDataset.open(ncpath);
        //读取经纬度数据
        //经度
        Variable varLon = ncfile.findVariable(lonVarName);
        //维度
        Variable varLat = ncfile.findVariable(latVarName);
        //具体值
        Variable codeWaveVar= ncfile.findVariable(ConstantUtil.COLDWAVE_COLDWAVEALERT);

        Map map = new HashMap();
        //获取nc文件，经度、维度、具体值
        map = readNCLonLatFloat(varLon,varLat,codeWaveVar);
        return map;
    }

    //处理nc经纬度
    private static Map readNCLonLatFloat(Variable xlongVar, Variable xlatVar,Variable codeWaveVar) {

        // 初始化待会nc数据乘放容器
        // 获取该字段变量范围，有多少值
        float[][] xlonData    = new float[xlongVar.getShape(0)][xlongVar.getShape(1)];
        float[][] xlatData    = new float[xlatVar.getShape(0)][xlatVar.getShape(1)];
        int[][] codeWaveData = new int[codeWaveVar.getShape(0)][codeWaveVar.getShape(1)];

        // 读取NC文件文件内容
        try{
            xlonData   = (float[][]) xlongVar.read().copyToNDJavaArray();
            xlatData   = (float[][]) xlatVar.read().copyToNDJavaArray();
            codeWaveData= (int[][])codeWaveVar.read().copyToNDJavaArray();
        }catch (Exception e){
            log.error("读取寒潮NC文件数据异常", e);
        }

        // 统一数据类型
        // 统一数据类型
//        NCFileSourceData ncsd = new NCFileSourceData();
        double[][] _X = new double[xlonData.length][xlonData[0].length];// 经度
        double[][] _Y = new double[xlatData.length][xlatData[0].length];// 纬度
        double[][] _gridData = new double[codeWaveData.length][codeWaveData[0].length];// 数据

        for (int j = 0; j < codeWaveData.length; j++) {
            for (int k = 0; k < codeWaveData[0].length; k++) {
                _X[j][k] = Double.parseDouble(String.valueOf(xlonData[j][k]));
                _Y[j][k] = Double.parseDouble(String.valueOf(xlatData[j][k]));
                _gridData[j][k] = Double.parseDouble(String.valueOf(codeWaveData[j][k])); // 目标网省数据值
            }
        }

//        ncsd.setXlong(_X);
//        ncsd.setXlat(_Y);
//        ncsd.setData2D(_gridData);

        // 原有数据类型
        Map<String, Object> precDataMap = new HashMap<String, Object>();
        precDataMap.put(ConstantUtil.XLONG, _X);
        precDataMap.put(ConstantUtil.XLAT, _Y);
        precDataMap.put(ConstantUtil.COLDWAVE_COLDWAVEALERT, _gridData);
        return precDataMap;
    }

    //nc数据生成等值线数据
    public static String nc2EquiSurface(Map ncData, double[] dataInterval) {
        String geojsonpogylon = "";

        List<PolyLine> cPolylineList = new ArrayList<PolyLine>();
        List<Polygon> cPolygonList = new ArrayList<Polygon>();

        double[][] _gridData = (double[][]) ncData.get("ColdWaveAlert");
        int[][] S1 = new int[_gridData.length][_gridData[0].length];
        double[] _X = (double[]) ncData.get("XLONG"),
                _Y = (double[]) ncData.get("XLAT");
        ncData.put("invalid","0.0");
        double _undefData = Double.parseDouble((String)ncData.get("invalid"));
        List<Border> _borders = Contour.tracingBorders(_gridData, _X, _Y,
                S1, _undefData);
        int nc = dataInterval.length;
        cPolylineList = Contour.tracingContourLines(_gridData, _X, _Y, nc,
                dataInterval, _undefData, _borders, S1);// 生成等值线

        cPolylineList = Contour.smoothLines(cPolylineList);// 平滑
        cPolygonList = Contour.tracingPolygons(_gridData, cPolylineList,
                _borders, dataInterval);

        geojsonpogylon = getPolygonGeoJson(cPolygonList);

        return geojsonpogylon;
    }

    //拼装geogson
    public static String getPolygonGeoJson(List<Polygon> cPolygonList) {
        String geo = null;
        String geometry = " { \"type\":\"Feature\",\"geometry\":";
        String properties = ",\"properties\":{ \"value\":";

        String head = "{\"type\": \"FeatureCollection\"," + "\"features\": [";
        String end = "  ] }";
        if (cPolygonList == null || cPolygonList.size() == 0) {
            return null;
        }
        try {
            for (Polygon pPolygon : cPolygonList) {
                List<Object> ptsTotal = new ArrayList<Object>();
                for (PointD ptd : pPolygon.OutLine.PointList) {
                    List<Double> pt = new ArrayList<Double>();
                    pt.add(doubleFormat(ptd.X));
                    pt.add(doubleFormat(ptd.Y));
                    ptsTotal.add(pt);
                }
                List<Object> list3D = new ArrayList<Object>();
                list3D.add(ptsTotal);
                JSONObject js = new JSONObject();
                js.put("type", "Polygon");
                js.put("coordinates", list3D);

                geo = geometry + js.toString() + properties  +pPolygon.LowValue + "} }" + "," + geo;
            }
            if (geo.contains(",")) {
                geo = geo.substring(0, geo.lastIndexOf(","));
            }

            geo = head + geo + end;
        } catch (Exception e) {
            e.printStackTrace();
            return geo;
        }
        return geo;
    }

    /**
     * double保留两位小数
     */
    public static double doubleFormat(double d) {
        BigDecimal bg = new BigDecimal(d);
        double f1 = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static void main(String[] args) throws IOException {
        isolineProcess();
    }
}
