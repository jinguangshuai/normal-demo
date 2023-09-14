package Test2;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.ReferenceIdentifier;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Auther：jinguangshuai
 * @Data：2023/9/5 - 09 - 05 - 16:02
 * @Description:Test2
 * @version:1.0
 */
public class test05 {

    public static void main(String[] args) throws Exception{
        File file = new File("C:\\Users\\JGS\\Desktop\\文件\\舞动\\湖南\\DW223030920024.shp");
        getShapeFile(file);
    }

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
}
