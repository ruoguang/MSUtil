# MSUtil
### 功能说明（免费接口，持续更新三方接口）
* ip查询
* 手机号查询
* 身份证查询
* 银行卡归属地查询
### 事例
测试类
```
public class Test {
    public static void main(String[] args) {
        MSUtil.ipQuery("67.220.90.13");
        MSUtil.telQuery("132****1226");
        MSUtil.idCardQuery("3425*********17");
        MSUtil.brankQuery("621*********903");
    }
}
```
运行结果（做了隐私处理，实际是全部展示）
```
{errno=0.0, errmsg=, data=美国, ip=67.220.90.13}
{code=0, data=浙江嘉兴 联通, province=浙江, city=嘉兴, isp=联通, tel=132****1226}
{att=安徽省宣城市宣州区, postno=242000, areano=0563, idcard=342501**********17, born=1994年02月24日, sex=男, styleCitynm=中华人民共和国,安徽省,宣城市, styleSimcall=中国,安徽,宣城}
{bankCode=CMB, stat=ok, cardType=储蓄卡, messages=[], CMB=招商银行, cardTypeCode=DC, cardNumber=621********903}
```

