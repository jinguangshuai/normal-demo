package ncToPicture;

import org.geotools.data.crs.ForceCoordinateSystemFeatureResults;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.imageio.ImageIO;
import org.geotools.styling.Stroke;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

/**
 * @Auther：jinguangshuai
 * @Data：2023/3/16 - 03 - 16 - 10:32
 * @Description:nc
 * @version:1.0
 */
public class Shape2Image{

//    private static MapContent map = new MapContent();

    //默认常量
    private static final Color LINE_COLOUR = Color.white;
    private static final Color FILL_COLOUR = Color.CYAN;
    private static final float LINE_OPACITY = 1.0f;
    private static final float LINE_WIDTH = 0.1f;
    private static final float POINT_SIZE = 10.0f;
    /**
     * 添加featureCollection等值面图层
     *
     * @param featureCollection 等值面要素几何
     * @param levelProps 色阶,结构如：{0.1:"#a5f38d"}
     * @param opacity 透明度
     */
    public MapContent addShapeLayer(FeatureCollection featureCollection, Map<Double,String> levelProps, float opacity) {
        MapContent map = new MapContent();
        try {
            // 由坐标顺序引发坐标变换，这三行由于修正数据，不加的话会出现要素漏缺。
            SimpleFeatureType simpleFeatureType = (SimpleFeatureType) featureCollection.getSchema();
            String crs = CRS.lookupIdentifier(simpleFeatureType.getCoordinateReferenceSystem(), true);
            featureCollection = new ForceCoordinateSystemFeatureResults(featureCollection, CRS.decode(crs, true));
            //创建样式
            StyleFactory sf = new StyleFactoryImpl();
            FilterFactory ff = new FilterFactoryImpl();
            FeatureTypeStyle fts = sf.createFeatureTypeStyle();

            for (Map.Entry entry:levelProps.entrySet()) {
                double key = (Double) entry.getKey();
//                String value = (String) entry.getValue();
                Color value = (Color) entry.getValue();

                //多边形填充颜色和透明度
                Fill fill = sf.createFill(ff.literal(value),ff.literal(opacity));
                //线条颜色和透明度
                Stroke stroke = sf.createStroke(ff.literal(LINE_COLOUR),ff.literal(LINE_WIDTH),ff.literal(LINE_OPACITY));
                Symbolizer symbolizer = sf.createPolygonSymbolizer(stroke, fill, "the_geom");
                Rule rule = sf.createRule();
                rule.setName("dzm_"+key);
                rule.symbolizers().add(symbolizer);
                Filter filter = ECQL.toFilter("value="+key);
                rule.setFilter(filter);
                fts.rules().add(rule);
            }

            Style style = sf.createStyle();
            style.setName("style_dzm");
            style.featureTypeStyles().add(fts);

            Layer layer = new FeatureLayer(featureCollection, style);
            map.addLayer(layer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 根据四至坐标、长、宽像素获取地图内容，并生成图片
     *
     * @param params
     * @param imgPath
     */
    public void getMapContent(MapContent mapContent,Map params, String imgPath) {
        try {
            double[] bbox = (double[]) params.get("bbox");
            double x1 = bbox[0], x2 = bbox[1],
                    y1 = bbox[2], y2 = bbox[3];
            int width = Integer.parseInt(params.get("width").toString()) ,
                    height = Integer.parseInt(params.get("height").toString());
            // 设置输出范围
            CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
            ReferencedEnvelope mapArea = new ReferencedEnvelope(x1, x2, y1, y2, crs);
            // 初始化渲染器
            StreamingRenderer sr = new StreamingRenderer();
            sr.setMapContent(mapContent);
            // 初始化输出图像
            BufferedImage bi = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.getGraphics();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Rectangle rect = new Rectangle(0, 0, width, height);
            // 绘制地图
            sr.paint((Graphics2D) g, rect, mapArea);
            //将BufferedImage变量写入文件中。
            ImageIO.write(bi, "png", new File(imgPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
