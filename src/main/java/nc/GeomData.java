package nc;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

/**
* @Author: yzmiao
* @Description: 缓存JTS相交处理过程数据
* @Date: 2021年5月28日 下午5:48:05
* @Version: V1.0
**/
@Data
public class GeomData {
	
	// Geometry
	private Geometry geoms;
	// 数据值
	private Double value;
	
}
