package nc;

/**
 * 常量
 */
public interface ConstantUtil {

    // file_name & variables of highRain_ncfile
    String HIGHRAIN_NAME = "highRain_";
    String HIGHRAIN_RAINALERT = "RainAlert";
    String PREC = "prec";

    // 温度
    String TEMP_NAME = "temp_";
    String TEMP_T2 = "T2";

    // 风速、风向
    String WIND_NAME = "wnd_";
    String WIND_WNDSPEED = "WndSpeed";
    String WIND_WNDDIR = "WndDir";

    // 湿度
    String RH_RHALERT = "RH2";

    // 寒潮
    String COLDWAVE_COLDWAVEALERT = "ColdWaveAlert";

    // variables of x  ， y & time
    String TIMES = "Times";
    String STARTTIMES = "startTimes";
    String ENDTIMES = "endTimes";
    String XLAT = "XLAT";
    String XLONG = "XLONG";

    // 降雨量
    String PRECIP = "Precip";

    // 雷电潜势
    String LIGHTINGPI_LPIDATA = "LPIdata";

    // 低温
    String LOWTEMP_ALERT = "lowTempAlert";

    // file_name & variables of prec_ncfile
    String PREC_NAME = "prec_";

    // 5、file_name & variables of highTemp_ncfile
    String HIGHTEMP_NAME = "highTemp_";
    String HIGHTEMP_HIGHTEMPALERT = "highTempAlert";

    // 6、file_name & variables of highWind_ncfile
    String HIGHWIND_NAME = "highWind_";
    String HIGHWIND_HIGHWINDDATA = "highWindData";

    // 文件名划分
    String NCFILE_NAME_SPLIT = "_";

}
