package nc;


import lombok.extern.slf4j.Slf4j;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.locationtech.jts.geom.*;
import org.opengis.feature.Feature;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import wcontour.global.PointD;
import wcontour.global.PolyLine;
import wcontour.global.Polygon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Auther：jinguangshuai
 * @Data：2023/3/14 - 03 - 14 - 10:03
 * @Description:nc
 * @version:1.0
 */
@Slf4j
public class ncToWkt {

    private static GeometryFactory geometryFactory = new GeometryFactory();

    //生成等值线主方法
    public List<NCFileProcessData> isolineProcess() throws IOException {
        String filePath = "C:/Users/JGS/Desktop/coldWave_2023030720_2023031020.nc";//文件路径
        //获取NC的数据
        Map map = getNcData(filePath);
        if(map == null || map.size()==0){
            return Collections.emptyList();
        }
        // 生成等值面
        List<Polygon> cPolygonList = parser(map);

        //绘制图形
        //等值面结果转换
        //将wcontour的poly转化为geojson
        System.out.println("将wcontour的poly转化为geojson");
        long surfaceStartTimeCPolygonList = System.currentTimeMillis();
        String polygonGeoJson = FeatureUtil.getPolygonGeoJson(cPolygonList);
        long surfaceEndTimeCPolygonList = System.currentTimeMillis();
        System.out.println("将wcontour的poly转化为geojson耗时{}"+DateUtil.getCostTime(surfaceStartTimeCPolygonList, surfaceEndTimeCPolygonList));

        //将geojson转化为shape
        System.out.println("将geojson转化为shape");
        long surfaceStartTimeShape = System.currentTimeMillis();
        String shapePath = "C:\\Users\\JGS\\Desktop\\coldWave_2023030720_2023031020.shp";
        Map<String, Object> stringObjectMap = Shp2GeojsonUtils.geoJson2Shape(polygonGeoJson, shapePath);
        long surfaceEndTimeShape = System.currentTimeMillis();
        System.out.println("将geojson转化为shape耗时"+DateUtil.getCostTime(surfaceStartTimeShape, surfaceEndTimeShape));

        if(stringObjectMap.get("status").equals("200")){
            System.out.println("开始绘图");
            long surfaceStartTimePicture = System.currentTimeMillis();


            File file = new File(shapePath);
            ShapefileDataStore shpDataStore = null;
            shpDataStore = new ShapefileDataStore(file.toURI().toURL());
            //设置编码，根据shp文件设置，一般中文操作系统的arcgis导出的是GBK，也可能是utf-8或其它，自行测试
            Charset charset = Charset.forName("GBK");
            shpDataStore.setCharset(charset);
            String typeName = shpDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = null;
            featureSource = shpDataStore.getFeatureSource(typeName);
            SimpleFeatureCollection fc = featureSource.getFeatures();
            //将geojson转化为featurecolection
            FeatureCollection polygonCollection =  getFeatureCollection(cPolygonList);
            FeatureCollection source = clipFeatureCollection(fc, (SimpleFeatureCollection) polygonCollection);

            Shape2Image shp2img = new Shape2Image();
            //添加图层
            Map levelProps = new HashMap();
            levelProps.put(0.0,"#ffffff");
            levelProps.put(50.9999,"#ffffff");
            levelProps.put(51,"#ff0000");
            float OPACITY = 1;
            shp2img.addShapeLayer(source,levelProps,OPACITY);
            //输出图片
            String outPath = "C:\\Users\\JGS\\Desktop\\coldWave_2023030720_2023031020.png";
            Map params = new HashMap();
            double[] bbox = new double[]{73.68,18.16,135.083,53.555};
            params.put("bbox",bbox);
            shp2img.getMapContent(params, outPath);

            long surfaceEndTimePicture = System.currentTimeMillis();
            System.out.println("绘图耗时"+DateUtil.getCostTime(surfaceStartTimePicture, surfaceEndTimePicture));
        }

        return null;
    }

    // 获取nc数据
    public static <T> Map getNcData(String ncpath) throws IOException {
        String lonVarName = "XLONG";//经度变量
        String latVarName = "XLAT";//纬度变量
        //加载nc文件
        NetcdfFile ncfile =  NetcdfDataset.open(ncpath);
        //读取经度
        Variable varLon = ncfile.findVariable(lonVarName);
        //读取维度
        Variable varLat = ncfile.findVariable(latVarName);
        //读取具体值
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
        // 原有数据类型
        Map<String, Object> precDataMap = new HashMap<String, Object>();
        precDataMap.put(ConstantUtil.XLONG, _X);
        precDataMap.put(ConstantUtil.XLAT, _Y);
        precDataMap.put(ConstantUtil.COLDWAVE_COLDWAVEALERT, _gridData);
        return precDataMap;
    }

    private int minCodeWave = -16;
    // 降雨量最大值
    private int maxCodeWave = -6;
    // 降雨量预警数据间隔
    private int codeWaveInterval = 1;

    private double[] getIntervals(int _minValue, int _maxValue, double _interval){
        int length = (int) ((_maxValue - _minValue) / _interval) +1;
        double[] intervals = new double[length];
        for(int i=0; i<length; i++){
            intervals[i] = _interval * i + _minValue;
        }
        return intervals;
    }


    /**
     * 数据解析：获取目标行政区域的四至坐标
     * @return
     */
    public List<Polygon> parser(Map map){
        //四至坐标
        double x_min = 73.68;
        double x_max = 135.083;
        double y_min = 18.16;
        double y_max = 53.555;
        // 经度
        double[][] _X  = (double[][])map.get("XLONG");
        // 纬度
        double[][] _Y  = (double[][])map.get("XLAT");
        // 3维数据
        double[][] codeWaveData = (double[][])map.get("ColdWaveAlert");
        // 获取网格数据，根据指定网省坐标获取指定数据
        double[][] _gridData = new double[codeWaveData.length][codeWaveData[0].length];

        double offset = 0.2;
        for (int i = 0; i < codeWaveData.length; i++) {
            for (int j = 0; j < codeWaveData[0].length; j++) {
                double xlon = _X[i][j];
                double xlat = _Y[i][j];
                if ((xlon >= x_min - offset && xlon <= x_max + offset) && (xlat >= y_min - offset && xlat <= y_max + offset)) {
                    _gridData[i][j] = codeWaveData[i][j]; // 目标网省数据值
                } else {
                    _gridData[i][j] = -999.0; // 将目标网省外的值设置为无效值，不参与等值面的划分
                }
            }
        }
        // flag array
        int[][] flag = new int[codeWaveData.length][codeWaveData[0].length];
        // 寒潮的取值范围[-16,-6]
        double[] interval = getIntervals(-20,20,1);
        System.out.println("获取等值面取值范围完成");
        // 正式处理
        // 绘画等值面
        System.out.println("开始绘制等值面");
        long surfaceStartTime = System.currentTimeMillis();
        List<Polygon> polygonList = new EquiSurfaceHandler().equiSurface2(_gridData, _X, _Y, flag, interval);
        long surfaceEndTime = System.currentTimeMillis();
        System.out.println("绘制等值面耗时{}"+DateUtil.getCostTime(surfaceStartTime, surfaceEndTime));
        return polygonList;
    }

    //结果转换
    public static FeatureCollection getFeatureCollection(List<Polygon> cPolygonList) {

        if (cPolygonList == null || cPolygonList.size() == 0) {
            return null;
        }
        FeatureCollection cs = null;
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
        try {
            for (Polygon pPolygon : cPolygonList) {
                //外圈
                LinearRing mainRing;
                Coordinate[] coordinates = new Coordinate[pPolygon.OutLine.PointList.size()];
                for (int i=0,len=pPolygon.OutLine.PointList.size();i<len;i++) {
                    PointD ptd = pPolygon.OutLine.PointList.get(i);
                    coordinates[i] = new Coordinate(ptd.X,ptd.Y);
                }
                mainRing = geometryFactory.createLinearRing(coordinates);

                //孔洞
                LinearRing[] holeRing = new LinearRing[pPolygon.HoleLines.size()];
                for (int i=0;i<pPolygon.HoleLines.size();i++) {
                    PolyLine hole = pPolygon.HoleLines.get(i);
                    Coordinate[] coordinates_h = new Coordinate[hole.PointList.size()];
                    for (int j=0,len=hole.PointList.size();j<len;j++) {
                        PointD ptd = hole.PointList.get(j);
                        coordinates_h[j] = new Coordinate(ptd.X,ptd.Y);
                    }
                    holeRing[i] = geometryFactory.createLinearRing(coordinates_h);
                }
                org.locationtech.jts.geom.Polygon geo = geometryFactory.createPolygon(mainRing,holeRing);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("the_geom", geo);
                map.put("value", pPolygon.LowValue);
                values.add(map);
            }

            cs = FeatureUtil.creatFeatureCollection(
                    "polygons",
                    "the_geom:Polygon:srid=4326,value:double",
                    values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cs;
    }

    //结果裁切
    public FeatureCollection clipFeatureCollection(FeatureCollection fc,
                                                   SimpleFeatureCollection gs) {
        FeatureCollection cs = null;
        try {
            List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
            FeatureIterator contourFeatureIterator = gs.features();
            FeatureIterator dataFeatureIterator = fc.features();
            while (dataFeatureIterator.hasNext()) {
                Feature dataFeature = dataFeatureIterator.next();
                Object dataGeometry = dataFeature.getProperty(
                        "the_geom").getValue();
                //
                if(dataGeometry instanceof MultiPolygon){
                    MultiPolygon p = (MultiPolygon)dataGeometry;
                    while (contourFeatureIterator.hasNext()) {
                        Feature contourFeature = contourFeatureIterator.next();
                        Geometry contourGeometry = (Geometry) contourFeature
                                .getProperty("the_geom").getValue();
                        double v = (Double) contourFeature.getProperty("value")
                                .getValue();
                        if (p.intersects(contourGeometry)) {
                            Geometry geo = p.intersection(contourGeometry);
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("the_geom", geo);
                            map.put("value", v);
                            values.add(map);
                        }
                    }
                }else {
                    Geometry p = (Geometry) dataGeometry;
                    while (contourFeatureIterator.hasNext()) {
                        Feature contourFeature = contourFeatureIterator.next();
                        Geometry contourGeometry = (Geometry) contourFeature
                                .getProperty("the_geom").getValue();
                        double v = (Double) contourFeature.getProperty("value")
                                .getValue();
                        if (p.intersects(contourGeometry)) {
                            Geometry geo = p.intersection(contourGeometry);
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("the_geom", geo);
                            map.put("value", v);
                            values.add(map);
                        }
                    }
                }
            }
            contourFeatureIterator.close();
            dataFeatureIterator.close();

            cs = FeatureUtil.creatFeatureCollection(
                    "MultiPolygons",
                    "the_geom:MultiPolygon:srid=4326,value:double",
                    values);

        }catch (SchemaException e) {
            e.printStackTrace();
        }
        return cs;
    }






    public static void main(String[] args) throws IOException {
        new ncToWkt().isolineProcess();
    }
}
