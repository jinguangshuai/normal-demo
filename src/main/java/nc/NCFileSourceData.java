package nc;

import lombok.Data;

/**
* @Author: yzmiao
* @Description: 缓存NC文件源数据,NC文件转换过数据类型的数据,
* 时间记录为int数组, 经纬度坐标及数据值记录为double数组
* @Date: 2021年6月3日 下午2:29:25
* @Version: V1.0
**/
@Data
public class NCFileSourceData {

	// Time ID
	private int[] times;
	// Start Time ID
	private int[] startTimes;
	// End Time ID
	private int[] endTimes;
	// LATITUDE, SOUTH IS NEGATIVE
	private double[][] xlat;
	// LONGITUDE, WEST IS NEGATIVE
	private double[][] xlong;
	// Data 2D
	private double[][] data2D;
	// Data 2D _2
	private double[][] data2D_2;
	// Data 3D
	private double[][][] data3D;
	// Data 3D _2
	private double[][][] data3D_2;
	
}
