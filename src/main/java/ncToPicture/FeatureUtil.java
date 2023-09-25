package ncToPicture;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.collection.ClippedFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import wcontour.global.PointD;
import wcontour.global.Polygon;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Auther：jinguangshuai
 * @Data：2023/3/15 - 03 - 15 - 11:14
 * @Description:nc
 * @version:1.0
 */
public class FeatureUtil {

    //geotools创建FeatureCollection
    public static FeatureCollection creatFeatureCollection(String typeName, String typeSpec, List<Map<String, Object>> values) throws SchemaException {
        SimpleFeatureType type = DataUtilities.createType(typeName, typeSpec);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
        DefaultFeatureCollection collection = new DefaultFeatureCollection();

        if (values.size() == 1) {
            for (Map feat : values) {
                featureBuilder.reset();
                featureBuilder.add(feat.get("the_geom"));
                featureBuilder.add("0.0");
                SimpleFeature feature = featureBuilder.buildFeature(null);
                collection.add(feature);
            }
        } else {
            for (Map feat : values) {
                featureBuilder.reset();
                featureBuilder.add(feat.get("the_geom"));
                featureBuilder.add(feat.get("value"));
                SimpleFeature feature = featureBuilder.buildFeature(null);
                collection.add(feature);
            }
        }
        return collection;
    }

    public static String getPolygonGeoJson(List<Polygon> cPolygonList) {
        String geo = null;
        String geometry = " { \"type\":\"Feature\",\"geometry\":";
        String properties = ",\"properties\":{ \"value\":";

        String head = "{\"type\": \"FeatureCollection\"," + "\"features\": [";
//        String end = "  ] }";
        String end = "],\n" +
                "    \"crs\":{\n" +
                "        \"type\":\"name\",\n" +
                "        \"properties\":{\n" +
                "            \"name\":\"urn:ogc:def:crs:OGC:1.3:CRS84\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
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

                geo = geometry + js.toString() + properties + pPolygon.LowValue + "} }" + "," + geo;
            }
            System.out.println("GeoJson的大小为：" + geo.length());
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
        double f1 = bg.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }


    public static String getPolygonGeoJsonByString(String polygon) {
        String geo = null;
        String geometry = " { \"type\":\"Feature\",\"geometry\":";
        String properties = ",\"properties\":{ \"value\":";
        String head = "{\"type\": \"FeatureCollection\"," + "\"features\": [";
        String end = "],\n" +
                "    \"crs\":{\n" +
                "        \"type\":\"name\",\n" +
                "        \"properties\":{\n" +
                "            \"name\":\"urn:ogc:def:crs:OGC:1.3:CRS84\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        if (!StringUtils.isNotBlank(polygon)) {
            return null;
        }
        try {
            //截取原始polygon  POLYGON((1 2,2 3)(3 4,5 6))
            String substring = polygon.substring(8, polygon.length() - 1);
            String[] split = substring.split("\\(");
            // 1 2,2 3）     3 4,5 6）
            for (String s : split) {
                List<Object> ptsTotal = new ArrayList<Object>();
                // 1 2    2 3)    3 4    5 6)
                String[] coordinates = s.split(",");
                for (String coordinate : coordinates) {
                    if (StringUtils.isNotBlank(coordinate)) {
                        List<Double> pt = new ArrayList<Double>();
                        String[] point = coordinate.split(" ");
                        if (StringUtils.isNotBlank(point[0])) {
                            point[0].replaceAll("\\)", "");
                            point[1].replaceAll("\\)", "");
                            point[0].replaceAll("\\),", "");
                            point[1].replaceAll("\\),", "");
                            pt.add(Double.valueOf(point[0]));
                            pt.add(Double.valueOf(point[1]));
                        } else {
                            point[1].replaceAll("\\)", "");
                            point[2].replaceAll("\\)", "");
                            point[1].replaceAll("\\),", "");
                            point[2].replaceAll("\\),", "");
                            pt.add(Double.valueOf(point[1]));
                            pt.add(Double.valueOf(point[2].contains(")") ? point[1].replaceAll("\\)", "") : point[2]));
                        }
                        ptsTotal.add(pt);
                    }
                }
                if (CollUtil.isNotEmpty(ptsTotal)) {
                    List<Object> list3D = new ArrayList<Object>();
                    //处理多边形无法闭合问题
                    List<Double> list1 = (List<Double>) ptsTotal.get(0);
                    List<Double> list2 = (List<Double>) ptsTotal.get(ptsTotal.size() - 1);
                    if (CollUtil.isNotEmpty(list1) && list1 != list2) {
                        List<Double> pt = new ArrayList<Double>();
                        pt.add(list1.get(0));
                        pt.add(list1.get(1));
                        ptsTotal.add(pt);
                        list3D.add(ptsTotal);
                    }else{
                        list3D.add(ptsTotal);
                    }

                    JSONObject js = new JSONObject();
                    js.put("type", "Polygon");
                    js.put("coordinates", list3D);
                    geo = geometry + js.toString() + properties + "0.0" + "} }" + "," + geo;
                }
            }
            System.out.println("GeoJson的大小为：" + geo.length());
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
     * 将 SimpleFeatureCollection 对象转换成 geoJson 字符串
     *
     * @param featureCollection SimpleFeatureCollection 或其子类
     * @return geoJson字符串
     */
    public static String featureCollection2GeoJsonStr(SimpleFeatureCollection featureCollection) throws IOException {
        FeatureJSON featureJSON = new FeatureJSON(new GeometryJSON(5));// 避免精度丢失
        return featureJSON.toString(featureCollection);
    }


    /**
     * geoJson 字符串 转 SimpleFeatureCollection
     *
     * @param geoJson 字符串
     * @return SimpleFeatureCollection
     */
    public static SimpleFeatureCollection geoJsonStr2FeatureCollection(String geoJson) throws IOException {
        DefaultFeatureCollection simpleFeatures = new DefaultFeatureCollection();
        FeatureJSON featureJSON = new FeatureJSON();// 该工具提供了读写geoJson的方法
        SimpleFeature simpleFeature = featureJSON.readFeature(geoJson);
        SimpleFeatureCollection featureCollection =
                (SimpleFeatureCollection) featureJSON.readFeatureCollection(geoJson);
        if (featureCollection == null || featureCollection.isEmpty()) {
            simpleFeatures.add(simpleFeature);
            return simpleFeatures;
        }
        return featureCollection;
    }
}
