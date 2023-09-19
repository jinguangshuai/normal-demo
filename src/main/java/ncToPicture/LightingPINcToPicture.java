package ncToPicture;


import cn.hutool.core.date.DateUnit;
import constans.ProvinceCoordinateEnum;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.map.MapContent;
import org.locationtech.jts.geom.*;
//import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import wcontour.global.PointD;
import wcontour.global.PolyLine;
import wcontour.global.Polygon;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * @Auther：jinguangshuai
 * @Data：2023/3/14 - 03 - 14 - 10:03
 * @Description:nc
 * @version:1.0
 */
@Slf4j
public class LightingPINcToPicture {

    private static GeometryFactory geometryFactory = new GeometryFactory();

    //生成等值线主方法
    public void isolineProcess() throws IOException {
        String filePath = "C:\\Users\\JGS\\Desktop\\lightingPI_2023041308.nc";//文件路径
        //获取NC的数据
        Map map = NCInitUtils.getNcData(filePath);
        if (map == null || map.size() == 0) {
            return;
        }

        // 生成等值面->List<Polygon>
        // 解析生成等值面
        List<List<Polygon>> cPolygonList = NCInitUtils.parser(map);

        for (int i = 0; i < cPolygonList.size(); i++) {
            //等值面结果转换
            //将wcontour的poly转化为geojson
            System.out.println("将wcontour的poly转化为geojson");
            long surfaceStartTimeCPolygonList = System.currentTimeMillis();
            String polygonGeoJson = FeatureUtil.getPolygonGeoJson(cPolygonList.get(i));
            //将geojson输出到json文件中
            String jsonFilePath = "C:\\Users\\JGS\\Desktop\\1\\LPIdata" + i + ".json";
            FileOutputStream fos = new FileOutputStream(jsonFilePath);
            fos.write(polygonGeoJson.getBytes(StandardCharsets.UTF_8));
            fos.close();
            long surfaceEndTimeCPolygonList = System.currentTimeMillis();
            System.out.println("将wcontour的poly转化为geojson耗时{}" + DateUtil.getCostTime(surfaceStartTimeCPolygonList, surfaceEndTimeCPolygonList));

            //将geojson转化为shape
            System.out.println("将geojson转化为shape");
            long surfaceStartTimeShape = System.currentTimeMillis();
            String shapePath = "C:\\Users\\JGS\\Desktop\\1\\LPIdata" + i + ".shp";
            Map<String, Object> stringObjectMap = Shp2GeojsonUtils.geoJson2Shape(jsonFilePath, shapePath);
            long surfaceEndTimeShape = System.currentTimeMillis();
            System.out.println("将geojson转化为shape耗时" + DateUtil.getCostTime(surfaceStartTimeShape, surfaceEndTimeShape));

            if (stringObjectMap.get("status").toString().equals("200")) {
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
                FeatureCollection polygonCollection = getFeatureCollection(cPolygonList.get(i));
//                FeatureCollection source = clipFeatureCollection(fc, (SimpleFeatureCollection) polygonCollection);

                Shape2Image shp2img = new Shape2Image();
                //添加图层
                Map levelProps = new HashMap();
                levelProps.put(0.1, Color.yellow);
                levelProps.put(0.4, Color.yellow);
                levelProps.put(0.5, Color.orange);
                levelProps.put(1.9, Color.orange);
                levelProps.put(2.0, Color.red);
                levelProps.put(2.1, Color.red);

//                levelProps.put(0.0, Color.white);
//                levelProps.put(0.1,Color.yellow);levelProps.put(0.2,Color.yellow);levelProps.put(0.3,Color.yellow);levelProps.put(0.4,Color.yellow);
//                levelProps.put(0.5,Color.orange);levelProps.put(0.6,Color.orange);levelProps.put(0.7,Color.orange);levelProps.put(0.8,Color.orange);
//                levelProps.put(0.9,Color.orange);levelProps.put(1.0,Color.orange);levelProps.put(1.1,Color.orange);levelProps.put(1.2,Color.orange);
//                levelProps.put(1.3,Color.orange);levelProps.put(1.4,Color.orange);levelProps.put(1.5,Color.orange);levelProps.put(1.6,Color.orange);
//                levelProps.put(1.7,Color.orange);levelProps.put(1.8,Color.orange);levelProps.put(1.9,Color.orange);
//                levelProps.put(2.0,Color.red);
//                levelProps.put(5.0,Color.red);

                //图层透明度
                float OPACITY = 1.0f;
                MapContent mapContent = shp2img.addShapeLayer(polygonCollection, levelProps, OPACITY);
                //输出图片
                String outPath = "C:\\Users\\JGS\\Desktop\\lightingPI_2023030720" + i + ".png";
                Map params = new HashMap();
//                double[] bbox = new double[]{73.68, 135.083, 18.16, 53.555};
                double[] bbox = new double[]{116.3,121.951,30.75,35.333};
                params.put("width", 792);
                params.put("height", 585);
                params.put("bbox", bbox);
                shp2img.getMapContent(mapContent, params, outPath);
                long surfaceEndTimePicture = System.currentTimeMillis();
                System.out.println("绘图耗时" + DateUtil.getCostTime(surfaceStartTimePicture, surfaceEndTimePicture));
            }
        }
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
                for (int i = 0, len = pPolygon.OutLine.PointList.size(); i < len; i++) {
                    PointD ptd = pPolygon.OutLine.PointList.get(i);
//                    coordinates[i] = new Coordinate(ptd.X,ptd.Y);

                    coordinates[i] = new Coordinate(new BigDecimal(ptd.X).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue(),
                            new BigDecimal(ptd.Y).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                //判断线性坐标点是否闭合,如果无法闭合手动闭合
                if (coordinates[0] == coordinates[coordinates.length - 1]) {
                    mainRing = geometryFactory.createLinearRing(coordinates);
                } else {
                    Coordinate[] coordinatesClose = new Coordinate[pPolygon.OutLine.PointList.size() + 1];
                    System.arraycopy(coordinates, 0, coordinatesClose, 0, coordinatesClose.length - 1);
                    coordinatesClose[coordinates.length] = coordinates[0];
                    mainRing = geometryFactory.createLinearRing(coordinatesClose);
                }

                //孔洞
                LinearRing[] holeRing = new LinearRing[pPolygon.HoleLines.size()];
                for (int i = 0; i < pPolygon.HoleLines.size(); i++) {
                    PolyLine hole = pPolygon.HoleLines.get(i);
                    Coordinate[] coordinatesHole = new Coordinate[hole.PointList.size()];
                    for (int j = 0, len = hole.PointList.size(); j < len; j++) {
                        PointD ptd = hole.PointList.get(j);
//                        coordinates_h[j] = new Coordinate(ptd.X,ptd.Y);
                        coordinatesHole[j] = new Coordinate(new BigDecimal(ptd.X).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue(),
                                new BigDecimal(ptd.Y).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());
                    }
                    //判断线性坐标点是否闭合,如果无法闭合手动闭合
                    if (coordinatesHole[0] == coordinatesHole[coordinatesHole.length - 1]) {
                        holeRing[i] = geometryFactory.createLinearRing(coordinatesHole);
                    } else {
                        Coordinate[] coordinatesHoleClose = new Coordinate[hole.PointList.size() + 1];
                        System.arraycopy(coordinatesHole, 0, coordinatesHoleClose, 0, coordinatesHoleClose.length - 1);
                        coordinatesHoleClose[coordinatesHole.length] = coordinatesHole[0];
                        holeRing[i] = geometryFactory.createLinearRing(coordinatesHoleClose);
                    }
                }
                org.locationtech.jts.geom.Polygon geo = geometryFactory.createPolygon(mainRing, holeRing);
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
                if (dataGeometry instanceof MultiPolygon) {
                    MultiPolygon p = (MultiPolygon) dataGeometry;
                    while (contourFeatureIterator.hasNext()) {
                        Feature contourFeature = contourFeatureIterator.next();
                        Geometry contourGeometry = (Geometry) contourFeature
                                .getProperty("the_geom").getValue();
                        double v = (Double) contourFeature.getProperty("value")
                                .getValue();

                        try {
                            if (p.intersects(contourGeometry)) {
                                Geometry geo = p.intersection(contourGeometry);
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("the_geom", geo);
                                map.put("value", v);
                                values.add(map);
                            }
                        } catch (Exception e) {
//                            log.error("MultiPolygon自相交原始图形：{}", p.toString());
//                            log.error("MultiPolygon自相交待处理图形不合法不处理: {}", contourGeometry.toString());
                            continue;
                        }
                    }
                } else {
                    Geometry p = (Geometry) dataGeometry;
                    while (contourFeatureIterator.hasNext()) {
                        Feature contourFeature = contourFeatureIterator.next();
                        Geometry contourGeometry = (Geometry) contourFeature
                                .getProperty("the_geom").getValue();
                        double v = (Double) contourFeature.getProperty("value")
                                .getValue();
                        try {
                            if (p.intersects(contourGeometry)) {
                                Geometry geo = p.intersection(contourGeometry);
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("the_geom", geo);
                                map.put("value", v);
                                values.add(map);
                            }
                        } catch (Exception e) {
//                            log.error("NonMultiPolygon自相交原始图形：{}", p.toString());
//                            log.error("NonMultiPolygon自相交待处理图形不合法不处理: {}", contourGeometry.toString());
                            continue;
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

        } catch (SchemaException e) {
            e.printStackTrace();
        }
        return cs;
    }


    public static void main(String[] args) throws IOException {
        Date start = new Date();
        long starts = System.currentTimeMillis();
        String format = cn.hutool.core.date.DateUtil.format(start, "yyyy-MM-dd HH:mm:ss");
        System.out.println("开始处理：" + format);


        new LightingPINcToPicture().isolineProcess();
        Date end = new Date();
        String format1 = cn.hutool.core.date.DateUtil.format(end, "yyyy-MM-dd HH:mm:ss");
        long ends = System.currentTimeMillis();

        System.out.println("处理结束：" + format1);
//        System.out.println("共计花费"+cn.hutool.core.date.DateUtil.between(start, end, DateUnit.MINUTE)+"分钟！");
        System.out.println("共计耗时" + DateUtil.getCostTime(starts, ends));

    }
}
