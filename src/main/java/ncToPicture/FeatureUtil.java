package ncToPicture;

import cn.hutool.json.JSONObject;
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import wcontour.global.PointD;
import wcontour.global.Polygon;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        for (Map feat : values) {
            featureBuilder.reset();
            featureBuilder.add(feat.get("the_geom"));
            featureBuilder.add(feat.get("value"));
            SimpleFeature feature = featureBuilder.buildFeature(null);
            collection.add(feature);
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
            System.out.println("GeoJson的大小为："+geo.length());
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


}
