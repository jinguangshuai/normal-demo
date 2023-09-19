package ncToPicture;

import lombok.extern.slf4j.Slf4j;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wcontour.global.Polygon;

/**
 * @Auther：jinguangshuai
 * @Data：2023/9/15 - 09 - 15 - 10:45
 * @Description:ncToPicture
 * @version:1.0
 */
@Slf4j
public class NCInitUtils {
    // 最小值
    private static double minValue = 0;
    // 最大值
    private static double maxValue = 2.4;
    // 数据间隔
    private static double interval = 0.8;

    private static double[] getIntervals(double _minValue, double _maxValue, double _interval) {
        int length = (int) ((_maxValue - _minValue) / _interval) + 1;
        double[] intervals = new double[length];
        for (int i = 0; i < length; i++) {
            intervals[i] = _interval * i + _minValue;
        }
        return intervals;
    }

    // 获取nc数据
    public static <T> Map getNcData(String ncpath) throws IOException {
        String lonVarName = "XLONG";//经度变量
        String latVarName = "XLAT";//纬度变量
        //加载nc文件
        NetcdfFile ncfile = NetcdfDataset.open(ncpath);
        //读取经度
        Variable varLon = ncfile.findVariable(lonVarName);
        //读取维度
        Variable varLat = ncfile.findVariable(latVarName);
        //读取具体值
        Variable codeWaveVar = ncfile.findVariable(ConstantUtil.LIGHTINGPI_LPIDATA);
        Map map = new HashMap();
        //获取nc文件，经度、维度、具体值
        map = readNCLonLatFloat(varLon, varLat, codeWaveVar);
        return map;
    }

    //处理nc经纬度
    private static Map readNCLonLatFloat(Variable xlongVar, Variable xlatVar, Variable codeWaveVar) {
        // 初始化待会nc数据乘放容器
        // 获取该字段变量范围，有多少值
        float[][] xlonData = new float[xlongVar.getShape(0)][xlongVar.getShape(1)];
        float[][] xlatData = new float[xlatVar.getShape(0)][xlatVar.getShape(1)];
        int[][][] lightningPIData = new int[codeWaveVar.getShape(0)][codeWaveVar.getShape(1)][codeWaveVar.getShape(2)];
        // 读取NC文件文件内容
        try {
            xlonData = (float[][]) xlongVar.read().copyToNDJavaArray();
            xlatData = (float[][]) xlatVar.read().copyToNDJavaArray();
            lightningPIData = (int[][][]) codeWaveVar.read().copyToNDJavaArray();
        } catch (Exception e) {
            log.error("读取寒潮NC文件数据异常", e);
        }
        // 统一数据类型
        double[][] _X = new double[xlonData.length][xlonData[0].length];// 经度
        double[][] _Y = new double[xlatData.length][xlatData[0].length];// 纬度
        double[][][] _gridData = new double[lightningPIData.length][lightningPIData[0].length][lightningPIData[0][0].length];// 数据
        for (int j = 0; j < lightningPIData[0].length; j++) {
            for (int k = 0; k < lightningPIData[0][0].length; k++) {
//                _X[j][k] = Double.parseDouble(String.valueOf(xlonData[j][k]));
//                _Y[j][k] = Double.parseDouble(String.valueOf(xlatData[j][k]));
                _X[j][k] = new BigDecimal(Double.parseDouble(String.valueOf(xlonData[j][k]))).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
                _Y[j][k] = new BigDecimal(Double.parseDouble(String.valueOf(xlatData[j][k]))).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
//                _gridData[j][k] = Double.parseDouble(String.valueOf(codeWaveData[j][k])); // 目标网省数据值
                //保证只有一位小数
                for (int i = 0; i < lightningPIData.length; i++) {
                    _gridData[i][j][k] = new BigDecimal(Double.parseDouble(String.valueOf(lightningPIData[i][j][k]))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
            }
        }
        // 原有数据类型
        Map<String, Object> precDataMap = new HashMap<String, Object>();
        precDataMap.put(ConstantUtil.XLONG, _X);
        precDataMap.put(ConstantUtil.XLAT, _Y);
        precDataMap.put(ConstantUtil.LIGHTINGPI_LPIDATA, _gridData);
        return precDataMap;
    }

    /**
     * 数据解析：获取目标行政区域的四至坐标
     *
     * @return
     */
    public static List<List<Polygon>> parser(Map map) {
        //四至坐标
        double x_min = 116.3;
        double x_max = 121.951;
        double y_min = 30.75;
        double y_max = 35.333;
        // 经度
        double[][] _X = (double[][]) map.get("XLONG");
        // 纬度
        double[][] _Y = (double[][]) map.get("XLAT");
        // 3维数据
        double[][][] lightningPIData = (double[][][]) map.get("LPIdata");
        // 获取网格数据，根据指定网省坐标获取指定数据
        double[][][] _gridData = new double[lightningPIData.length][lightningPIData[0].length][lightningPIData[0][0].length];

        double offset = 0.2;
        for (int j = 0; j < lightningPIData[0].length; j++) {
            for (int k = 0; k < lightningPIData[0][0].length; k++) {
                double xlon = _X[j][k];
                double xlat = _Y[j][k];
                if ((xlon >= x_min - offset && xlon <= x_max + offset) && (xlat >= y_min - offset && xlat <= y_max + offset)) {
                    for (int i = 0; i < lightningPIData.length; i++) {
                        _gridData[i][j][k] = lightningPIData[i][j][k]; // 目标网省数据值
                    }
                } else {
                    for (int i = 0; i < lightningPIData.length; i++) {
                        _gridData[i][j][k] = -999.0; // 目标网省数据值
                    }
                }
            }
        }
        // flag array
        int[][][] flag = new int[lightningPIData.length][lightningPIData[0].length][lightningPIData[0][0].length];
        // 寒潮的取值范围[-16,-6]
//        double[] interval = getIntervals(minValue, maxValue, NCInitUtils.interval);

//        double[] interval = new double[]{0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,5.0};

        double[] interval = new double[]{0.1,0.4,0.5,1.9,2.0,2.1};

//        double[] interval = new double[]{0.1,0.4,2.0};
        System.out.println("获取等值面取值范围完成");
        // 正式处理
        // 绘画等值面
        List<List<Polygon>> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            System.out.println("开始绘制等值面");
            long surfaceStartTime = System.currentTimeMillis();
            List<Polygon> polygonList = new EquiSurfaceHandler().equiSurface2(_gridData[i], _X, _Y, flag[i], interval);
            long surfaceEndTime = System.currentTimeMillis();
            System.out.println("绘制等值面耗时{}" + DateUtil.getCostTime(surfaceStartTime, surfaceEndTime));
            result.add(polygonList);
        }
        return result;
    }

}
