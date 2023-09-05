package nc;

import lombok.Data;

import java.util.List;

/**
* @Author: yzmiao
* @Description: NC文件解析后的数据 BY TIME
* @Date: 2021年5月19日 下午3:18:12
* @Version: V1.0
**/
@Data
public class NCFileProcessData {
	
	// 文件/数据类型类型
	private String dataType;
	// 文件时间——fileName记录的时间
	private String fileTime;
	// 文件结束时间——fileName记录的结束时间
	private String fileEndTime;
	// 数据时间——文件数据时间
	private String dataTime;
	// 数据时间——文件数据结束时间
	private String dataEndTime;
	// Geometry & Value
	private List<GeomData> geomds;

}
