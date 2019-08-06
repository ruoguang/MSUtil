package com.maisi.it;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liaoqian
 * @version 1.0
 * @date 20190805
 * 实现了ip 手机号码 身份证号码 银行卡归属地 等查询 返回结果都是map
 */
public class MSUtil {

    private static String ipurl = null;
    private static PrintWriter ipout = null;
    private static BufferedReader ipin = null;
    private static URLConnection ipconn = null;
    private static Map<String, Object> ipmap = null;
    private static HashMap<String, String> cardTypeMap = new HashMap<String, String>();
    private static Map<String, Object> bankCodeMap = new HashMap<String, Object>();
    private static String bankStrList=null;

    static {
        try {
            ipmap = new HashMap<String, Object>();
            ipurl = "http://ip.360.cn/IPQuery/ipquery";
            URL ipUrl = new URL(ipurl);
            ipconn = ipUrl.openConnection();
            ipconn.setRequestProperty("accept", "*/*");
            ipconn.setRequestProperty("connection", "Keep-Alive");
            ipconn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            ipconn.setDoOutput(true);
            ipconn.setDoInput(true);
            ipout = new PrintWriter(ipconn.getOutputStream());
            // 银行卡类型
            cardTypeMap.put("DC", "储蓄卡");
            cardTypeMap.put("CC", "信用卡");
            cardTypeMap.put("SCC", "准贷记卡");
            cardTypeMap.put("PC", "预付费卡");
            //银行卡简称和名字
            bankStrList =
                    "PSBC:中国邮政储蓄银行," +
                    "ICBC:工商银行," +
                    "ABC:农业银行," +
                    "BOC:中国银行," +
                    "CCB:建设银行," +
                    "COMM:交通银行," +
                    "CITIC:中信银行," +
                    "CEB:光大银行," +
                    "HXBANK:华夏银行," +
                    "CMBC:民生银行," +
                    "GDB:广发银行," +
                    "SPDB:浦东发展银行," +
                    "CMB:招商银行," +
                    "CIB:兴业银行," +
                    "EGBANK:恒丰银行," +
                    "CZBANK:浙商银行," +
                    "BOHAIB:渤海银行," +
                    "SPABANK:平安银行," +
                    "DIYEBANK:企业银行," +
                    "SHBANK:上海银行," +
                    "XMBANK:厦门银行," +
                    "BJBANK:北京银行," +
                    "FJHXBC:福建海峡银行," +
                    "JLBANK:吉林银行," +
                    "NBBANK:宁波银行," +
                    "WZCB:温州银行," +
                    "GCB:广州银行," +
                    "HKB:汉口银行," +
                    "LYB:洛阳银行," +
                    "DLB:大连银行," +
                    "BHB:河北银行," +
                    "HZCB:杭州商业银行," +
                    "NJCB:南京银行," +
                    "URMQCCB:乌鲁木齐市商业银行," +
                    "SXCB:绍兴银行," +
                    "HLDCCB:葫芦岛市商业银行," +
                    "ZZBANK:郑州银行," +
                    "NXBANK:宁夏银行," +
                    "QSBANK:齐商银行," +
                    "BOJZ:锦州银行," +
                    "HSBANK:徽商银行," +
                    "CQBANK:重庆银行," +
                    "HRBANK:哈尔滨银行," +
                    "GYCB:贵阳银行," +
                    "LZYH:兰州银行," +
                    "NCB:南昌银行," +
                    "QDCCB:青岛银行," +
                    "BOQH:青海银行," +
                    "TZCB:台州银行," +
                    "CSCB:长沙银行," +
                    "GZB:赣州银行," +
                    "H3CB:内蒙古银行," +
                    "BSB:包商银行," +
                    "DAQINGB:龙江银行," +
                    "SHRCB:上海农商银行," +
                    "SRCB:深圳农村商业银行," +
                    "GZRCU:广州农村商业银行," +
                    "DRCBCL:东莞农村商业银行," +
                    "BJRCB:北京农村商业银行," +
                    "TRCB:天津农村商业银行," +
                    "JSRCU:江苏省农村信用社联合社," +
                    "ZJQL:浙江泰隆商业银行";
            for (String s : bankStrList.split(",")) {
                bankCodeMap.put(s.split(":")[0], s.split(":")[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ip查询三方接口
     * 正确返回格式为{errno=0.0, errmsg=, data=安徽宣城	联通, ip=36.32.148.0, city=安徽宣城, isp=联通}
     *
     * @param ip
     */
    public static Map<String, Object> ipQuery(String ip) {
        try {
            ipout.print("&ip=" + ip);
            ipout.flush();
            ipin = new BufferedReader(new InputStreamReader(ipconn.getInputStream()));
            String line = ipin.readLine();
            ipmap = json2map(line);
            ipmap.put("ip", ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ipmap.get("data") != null && ipmap.get("data").toString().split("\\s+").length >= 2) {
            ipmap.put("city", ipmap.get("data").toString().split("\\s+")[0]);
            ipmap.put("isp", ipmap.get("data").toString().split("\\s+")[1]);
        }
        System.out.println(ipmap.toString());
        return ipmap;
    }

    /**
     * 查询手机号码归属地三方接口
     * 正确返回格式为{code=0, data=河北邢台 联通, province=河北, city=邢台, tel=18632097220, isp=联通}
     *
     * @param telNumber
     * @return
     */
    @SuppressWarnings("all")
    public static Map<String, String> telQuery(String telNumber) {
        StringBuilder r = new StringBuilder();
        try {
            // 备用地址：http://mobsec-dianhua.baidu.com/dianhua_api/open/location?tel=13195530002
            URL url = new URL("https://cx.shouji.360.cn/phonearea.php?number=" + telNumber);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            conn.setRequestProperty("accept", "*/*");
            conn.setDoOutput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print("?number=" + telNumber);
            out.flush();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "gb2312"));
            String result = "";
            while ((result = br.readLine()) != null) {
                r.append(result);
            }
            is.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject resultObj = JSONObject.parseObject(r.toString());
        JSONObject data = (JSONObject) resultObj.get("data");
        Object tempCode = resultObj.get("code");
        Map<String, String> map = new HashMap();

        String province;
        String city;
        String isp;
        String code;

        try {
            province = data.get("province").toString();
            city = data.get("city").toString();
            isp = data.get("sp").toString();
            code = tempCode.toString();
        } catch (NullPointerException e) {
            province = "";
            city = "";
            isp = "";
            code = "";
        }
        map.put("code", code);
        map.put("data", province + city + " " + isp);
        map.put("tel", telNumber);
        map.put("province", province);
        map.put("city", city);
        map.put("isp", isp);
        System.out.println(map.toString());
        return map;
    }

    /**
     * 将字符创转为Map格式
     *
     * @param strJson json型的字符串
     * @return
     */
    public static Map<String, Object> json2map(String strJson) {
        Map<String, Object> res = null;
        try {
            Gson gson = new Gson();
            res = gson.fromJson(strJson, new TypeToken<Map<String, Object>>() {
            }.getType());
        } catch (JsonSyntaxException ignored) {
        }
        return res;
    }

    /**
     * 身份证信息查询
     * 正确返回格式为{att=安徽省宣城市宣州区, postno=242000, areano=0563, idcard=342501199402242817, born=1994年02月24日, sex=男, styleCitynm=中华人民共和国,安徽省,宣城市, styleSimcall=中国,安徽,宣城}
     *
     * @param idCard
     * @return
     */
    @SuppressWarnings("all")
    public static Map<String, Object> idCardQuery(String idCard) {
        URL url = null;
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            url = new URL("http://api.k780.com:88/?app=idcard.get&idcard=" + idCard + "&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=xml");
            Document doc = builder.parse(url.openStream());
            NodeList node = doc.getElementsByTagName("result");

            for (int i = 0; i < node.getLength(); i++) {
                String idcard = "";
                String born = "";
                String sex = "";
                String att = "";
                String postno = "";
                String areano = "";
                String styleSimcall = "";
                String styleCitynm = "";
                if (doc.getElementsByTagName("idcard").item(i).getFirstChild() != null) {
                    idcard = doc.getElementsByTagName("idcard").item(i).getFirstChild().getNodeValue();
                }
                if (doc.getElementsByTagName("born").item(i).getFirstChild() != null) {
                    born = doc.getElementsByTagName("born").item(i).getFirstChild().getNodeValue();
                }
                if (doc.getElementsByTagName("sex").item(i).getFirstChild() != null) {
                    sex = doc.getElementsByTagName("sex").item(i).getFirstChild().getNodeValue();
                }
                if (doc.getElementsByTagName("att").item(i).getFirstChild() != null) {
                    att = doc.getElementsByTagName("att").item(i).getFirstChild().getNodeValue();
                }
                if (doc.getElementsByTagName("postno").item(i).getFirstChild() != null) {
                    postno = doc.getElementsByTagName("postno").item(i).getFirstChild().getNodeValue();
                }
                if (doc.getElementsByTagName("areano").item(i).getFirstChild() != null) {
                    areano = doc.getElementsByTagName("areano").item(i).getFirstChild().getNodeValue();
                }
                if (doc.getElementsByTagName("style_simcall").item(i).getFirstChild() != null) {
                    styleSimcall = doc.getElementsByTagName("style_simcall").item(i).getFirstChild().getNodeValue();
                }
                if (doc.getElementsByTagName("style_citynm").item(i).getFirstChild() != null) {
                    styleCitynm = doc.getElementsByTagName("style_citynm").item(i).getFirstChild().getNodeValue();
                }
                map.put("idcard", idcard);
                map.put("born", born);
                map.put("sex", sex);
                map.put("att", att);
                map.put("postno", postno);
                map.put("areano", areano);
                map.put("styleSimcall", styleSimcall);
                map.put("styleCitynm", styleCitynm);
                System.out.println(map.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }





    /**
     * 银行卡号归属地查询
     * 一般正确返回格式为{bankCode=CMB, stat=ok, cardType=储蓄卡, messages=[], CMB=招商银行, cardTypeCode=DC, cardNumber=6214832184369903}
     * 或者返回原生三方接口格式{cardType=DC, bank=CMB, key=6214832184369903, messages=[], validated=true, stat=ok}
     * @param cardNo
     * @return
     */
    @SuppressWarnings("all")
    public static Map<String, Object> brankQuery(String cardNo) {
        URL url = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            url = new URL("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="+cardNo+"&cardBinCheck=true");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PrintWriter out = null;
            conn.setRequestProperty("accept", "*/*");
            conn.setDoOutput(true);
            out = new PrintWriter(conn.getOutputStream());
            StringBuilder r = new StringBuilder();
            out.flush();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "gb2312"));
            String result = "";
            while ((result = br.readLine()) != null) {
                r.append(result);
            }
            map = json2map(r.toString());
            Map<String, Object> resultMap = new HashMap<String, Object>();
            if(bankCodeMap.get(map.get("bank"))!=null){
                resultMap.put("stat",map.get("stat"));
                resultMap.put("messages",map.get("messages"));
                resultMap.put("cardNumber",map.get("key").toString());
                resultMap.put("bankCode",map.get("bank").toString());
                resultMap.put(map.get("bank").toString(),bankCodeMap.get(map.get("bank")).toString());
                if(cardTypeMap.get(map.get("cardType"))!=null){
                    resultMap.put("cardTypeCode",map.get("cardType").toString());
                    resultMap.put("cardType",cardTypeMap.get(map.get("cardType")).toString());
                }
            }
            if(resultMap!=null){
                map=resultMap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(map.toString());
        return map;
    }

}

