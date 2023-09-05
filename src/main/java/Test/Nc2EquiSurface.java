package Test;

//import com.lzugis.CommonMethod;
//import com.lzugis.geotools.EquiSurface;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import wcontour.Contour;
import wcontour.global.Border;
import wcontour.global.PolyLine;
import wcontour.global.Polygon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nc2EquiSurface {
//    private static String rootPath = System.getProperty("user.dir");
//    private EquiSurface equ = new EquiSurface();
//
//    public Map getNcData(String ncpath) {
//        Map map = new HashMap();
//        NetcdfFile ncfile = null;
//        try {
//            ncfile = NetcdfDataset.open(ncpath);
//            Variable varLon = ncfile.findVariable("lon");
//            Variable varLat = ncfile.findVariable("lat");
//            Variable varPre = ncfile.findVariable("TEM");
//            float[] lon = (float[]) varLon.read().copyToNDJavaArray();
//            float[] lat = (float[]) varLat.read().copyToNDJavaArray();
//            float[][] pre = (float[][]) varPre.read().copyToNDJavaArray();
//            double[] dLon = new double[lon.length], dLat = new double[lon.length];
//            double[][] dPre = new double[pre.length][pre[0].length];
//            for (int i = 0, len = lon.length; i < len; i++) {
//                dLon[i] = Double.parseDouble(String.valueOf(lon[i]));
//            }
//            for (int i = 0, len = lat.length; i < len; i++) {
//                dLat[i] = Double.parseDouble(String.valueOf(lat[i]));
//            }
//            for (int i = 0, len = pre.length; i < len; i++) {
//                float[] _pre = pre[i];
//                for (int j = 0, jlen = _pre.length; j < jlen; j++) {
//                    dPre[i][j] = Double.parseDouble(String.valueOf(_pre[j]));
//                }
//            }
//
//            map.put("lon", dLon);
//            map.put("lat", dLat);
//            map.put("tem", dPre);
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        return map;
//    }
//
//    public String nc2EquiSurface(Map ncData, double[] dataInterval) {
//        String geojsonpogylon = "";
//
//        List<PolyLine> cPolylineList = new ArrayList<PolyLine>();
//        List<Polygon> cPolygonList = new ArrayList<Polygon>();
//
//        double[][] _gridData = (double[][]) ncData.get("tem");
//        int[][] S1 = new int[_gridData.length][_gridData[0].length];
//        double[] _X = (double[]) ncData.get("lon"), _Y = (double[]) ncData.get("lat");
//        double _undefData = -9999.0;
//        List<Border> _borders = Contour.tracingBorders(_gridData, _X, _Y,
//                S1, _undefData);
//        int nc = dataInterval.length;
//        cPolylineList = Contour.tracingContourLines(_gridData, _X, _Y, nc,
//                dataInterval, _undefData, _borders, S1);// 生成等值线
//
//        cPolylineList = Contour.smoothLines(cPolylineList);// 平滑
//        cPolygonList = Contour.tracingPolygons(_gridData, cPolylineList,
//                _borders, dataInterval);
//
//        geojsonpogylon = equ.getPolygonGeoJson(cPolygonList);
//
//        return geojsonpogylon;
//    }
//
//    public static void main(String[] args) {
//        Nc2EquiSurface nc2equ = new Nc2EquiSurface();
//        CommonMethod cm = new CommonMethod();
//
//        long start = System.currentTimeMillis();
//        String ncpath = rootPath + "/data/nc/gdfs1.nc";
//
//        //获取NC的数据
//        Map map = nc2equ.getNcData(ncpath);
//
//        //根据NC生成等值面
//        double[] dataInterval = new double[]{0, 5, 10, 15, 20, 25, 30, 35, 40, 45};
//        String strGeojson = nc2equ.nc2EquiSurface(map, dataInterval);
//
//        String strFile = rootPath + "/out/china_pre.json";
//        cm.append2File(strFile, strGeojson);
//
//        System.out.println("Total cost:" + (System.currentTimeMillis() - start));
//    }
}