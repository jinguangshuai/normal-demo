package Test2;


import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.style.Style;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Auther：jinguangshuai
 * @Data：2023/9/5 - 09 - 05 - 11:01
 * @Description:Test2
 * @version:1.0
 */
public class test04 {
//    public static void main(String[] args) throws Exception {
//        // 读取Shapefile文件
//        File file = new File("C:\\Users\\JGS\\Desktop\\22\\dance\\河南\\W_202302280000202302281200_9.shp");
////        getShapeFile(file);
//
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("url", file.toURI().toURL());
//        DataStore dataStore = DataStoreFinder.getDataStore(map);
//        String typeName = dataStore.getTypeNames()[0];
//        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(typeName);
//        SimpleFeatureCollection collection = featureSource.getFeatures();
//
//        // 遍历要素并显示几何形状
//        try (SimpleFeatureIterator iterator = collection.features()) {
//            while (iterator.hasNext()) {
//                SimpleFeature feature = iterator.next();
//                Geometry geometry = (Geometry) feature.getDefaultGeometry();
//                System.out.println(geometry.toString());
//            }
//        }
//
//        // 显示Shapefile图层
//        CoordinateReferenceSystem crs = featureSource.getSchema().getCoordinateReferenceSystem();
//        Style style = SLD.createSimpleStyle(featureSource.getSchema());
//        MapContent mapContext = new MapContent();
//        mapContext.addLayer(featureSource, style);
//        JMapFrame mapFrame = new JMapFrame(mapContext);
//        mapFrame.enableToolBar(true);
//        mapFrame.enableStatusBar(true);
//        mapFrame.setVisible(true);
//
//    }

    public static void getShapeFile(File file) throws Exception {
        Map<String, Object> map = new HashMap<>(1);
        map.put("url", file.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        //字符转码，防止中文乱码
        ShapefileDataStore shpStore = (ShapefileDataStore) dataStore;
        shpStore.setCharset(StandardCharsets.UTF_8);
        //获取shp文件坐标系
        SimpleFeatureSource source = shpStore.getFeatureSource();
        SimpleFeatureType schema = source.getSchema();
        Query query = new Query(schema.getTypeName());
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);

        //获取shp文件所有的地块
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                String name = feature.getName().toString();
                System.out.println("地块坐标系=【{}】"+name);
                //坐标系的名称
                ReferenceIdentifier referenceIdentifier = feature.getFeatureType().getCoordinateReferenceSystem().getCoordinateSystem().getName();
                String code = referenceIdentifier.getCode();
                System.out.println("地块坐标系=【{}】"+code);
                //获取shp文件的属性信息
                Iterator<? extends Property> iterator = feature.getValue().iterator();
                while (iterator.hasNext()) {
                    Property property = iterator.next();
                    System.out.println("地块属性名【{}】 地块属性值【{}】"+ property.getName()+ property.getValue());
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return;
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        // Create a map content and add our shapefile to it
        MapContent map = new MapContent();
        map.setTitle("Quickstart");

        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, (org.geotools.styling.Style) style);
        map.addLayer(layer);

        // Now display the map
        JMapFrame.showMap(map);
    }

}

