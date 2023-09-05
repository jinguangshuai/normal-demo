package nc;

import lombok.Data;

/**
* @Author: yzmiao
* @Description: 缓存等值面的 WKT && VALUE, 因等值面算法有自己定义的Polygon
* (import wcontour.global.Polygon)类型, 为方便后续的JTS操作，将其转换成WKT
* @Date: 2021年5月28日 下午4:45:13
* @Version: V1.0
**/
@Data
public class WktData {
	
	// WKT
	private String wkt;
	// 数据值
	private Double value;

}
