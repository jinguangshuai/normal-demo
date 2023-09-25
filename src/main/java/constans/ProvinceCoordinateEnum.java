package constans;

/**
 * @Auther：jinguangshuai
 * @Data：2023/9/18 - 09 - 18 - 15:31
 * @Description:constans
 * @version:1.0
 */
public enum ProvinceCoordinateEnum {

//    CHINA("000000", "CHINA", "中国", "73.68","135.083","18.16","53.555"),
    BEIJING("110000", "BEIJING", "北京","115.41","117.501","39.43","41.051"),
    TIANJIN("120000", "TIANJIN", "天津", "116.72","118.071","38.57","40.401"),
    HEBEI("130000", "HEBEI", "河北", "113.06","119.883","36.01","42.617"),
//    JIBEI("181804", "JIBEI", "冀北", "73.68","","",""),
    SHANXI("140000", "SHANXITAIYUAN", "山西","110.25","114.533","34.6","40.733"),
    NEIMENGGU("150000", "NEIMENGGU", "内蒙古","97.16","126.033","37.5","53.333"),
//    MENGDONG("181801", "MENGDONG", "蒙东","73.68","","",""),
//    MENGXI("150002", "MENGXI", "蒙西", "73.68","","",""),
    LIAONING("210000", "LIAONING", "辽宁","118.88","125.767","38.71","43.433"),
    JILIN("220000", "JILIN", "吉林", "121.63","131.317","40.86","46.301"),
    HEILONGJIANG("230000", "HEILONGJIANG", "黑龙江","121.18","135.083","43.41","53.551"),
    SHANGHAI("310000", "SHANGHAI", "上海", "120.86","122.201","30.66","31.883"),
    JIANGSU("320000", "JIANGSU", "江苏", "116.3","121.951","30.75","35.333"),
    ZHEJIANG("330000", "ZHEJIANG", "浙江", "118.01","123.171","27.03","31.517"),
    ANHUI("340000", "ANHUI", "安徽", "114.9","119.617","29.68","34.634"),
    FUJIAN("350000", "FUJIAN", "福建", "115.83","120.667","23.5","28.367"),
    JIANGXI("360000", "JIANGXI", "江西", "113.56","118.467","24.48","30.067"),
    SHANDONG("370000", "SHANDONG", "山东", "114.31","122.717","34.36","38.384"),
    HENAN("410000", "HENAN", "河南", "110.35","116.651","31.38","36.367"),
    HUBEI("420000", "HUBEI", "湖北", "108.35","116.117","29.08","33.333"),
    HUNAN("430000", "HUNAN", "湖南", "108.78","114.401","24.63","29.301"),
    GUANGDONG("440000", "GUANGDONG", "广东", "109.75","117.333","20.2","25.517"),
    GUANGXI("450000", "GUANGXI", "广西", "104.46","112.067","20.9","26.383"),
    HAINAN("460000", "HAINAN", "海南", "108.61","111.083","18.16","20.167"),
    CHONGQING("500000", "CHONGQING", "重庆", "105.28","110.183","28.16","32.217"),
    SICHUAN("510000", "SICHUAN", "四川", "97.35","108.517","26.05","34.317"),
    GUIZHOU("520000", "GUIZHOU", "贵州", "103.6","109.583","24.61","29.216"),
    YUNNAN("530000", "YUNNAN", "云南", "97.51","106.201","21.13","29.251"),
    XIZANG("540000", "XIZANG", "西藏", "78.41","99.101","26.73","36.533"),
    SHANXI_XIAN("610000", "SHANXIXIAN", "陕西","105.48","111.251","31.7","39.583"),
    GANSU("620000", "GANSU", "甘肃", "92.21","108.767","32.18","42.951"),
    QINGHAI("630000", "QINGHAI", "青海", "89.4","103.067","31.53","39.334"),
    NINGXIA("640000", "NINGXIA", "宁夏", "104.28","107.651","35.23","39.383"),
    XINJIANG("650000", "XINJIANG", "新疆", "73.68","96.301","34.36","49.551"),
    TAIWAN("710000", "TAIWAN", "台湾", "119.3","124.571","20.75","25.945"),
    HONGKONG("810000", "HONGKONG", "香港", "113.51","114.501","22.9","22.367"),
    AOMEN("820000", "AOMEN", "澳门", "113.28","113.801","22.03","22.501");

    private String code;
    private String provinceEName;
    private String provinceName;
    private String xMin;
    private String xMax;
    private String yMin;
    private String yMax;

    ProvinceCoordinateEnum(String code, String provinceEName, String provinceName, String xMin, String xMax, String yMin, String yMax) {
        this.code = code;
        this.provinceEName = provinceEName;
        this.provinceName = provinceName;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvinceEName() {
        return provinceEName;
    }

    public void setProvinceEName(String provinceEName) {
        this.provinceEName = provinceEName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getxMin() {
        return xMin;
    }

    public void setxMin(String xMin) {
        this.xMin = xMin;
    }

    public String getxMax() {
        return xMax;
    }

    public void setxMax(String xMax) {
        this.xMax = xMax;
    }

    public String getyMin() {
        return yMin;
    }

    public void setyMin(String yMin) {
        this.yMin = yMin;
    }

    public String getyMax() {
        return yMax;
    }

    public void setyMax(String yMax) {
        this.yMax = yMax;
    }
}
