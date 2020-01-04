package cloudflare;


import Internet.IP;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hoe.utils.HttpUtil;
import config.Config;
import org.apache.http.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//添加cloudflare的dns解析
public class CloudflareDns {

    private static String URL = "https://api.cloudflare.com/client/v4";

    public static void dns(Map<String, String> header, String[] domains, String[] subDomains, String urlIp) {
        try {
            //循环domain
            for (String domain : domains) {
                //获取zoneId
                if (!Config.ZoneIds.containsKey(domain))
                    Config.ZoneIds.put(domain, getZoneId(header, domain));
                //取 subDomain
                for (String subDomain : subDomains) {
                    if (subDomain.contains(domain)) {
                        System.out.println("-------------------"+subDomain+"-----------------------");
                        //获取域记录
                        String domainData = HttpUtil.doGet(URL + "/zones/" + Config.ZoneIds.get(domain) + "/dns_records?name=" + subDomain, header);
//                        System.out.println("domainData--->" + domainData);
                        Map records = new Gson().fromJson(domainData, Map.class);
                        List<Map> recordsResults = (List<Map>) records.get("result");
                        for (Map result : recordsResults) {
                            if (subDomain.equals(result.get("name"))) {
                                String ip = IP.get_ip(urlIp);
                                if (ip == null || "".equals(ip)) {
                                    System.out.println("无法正确获取到外网IP，请检查api地址,退出");
                                    return;
                                }
                                if (ip.equals(result.get("content"))) {
                                    System.out.println("当前配置ip--->" + result.get("content"));
                                    System.out.println("当前外网ip--->" + ip);
                                    System.out.println("当前外网IP与Cloudflare配置相同，检测时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                } else {
                                    System.out.println("当前配置ip--->" + result.get("content"));
                                    System.out.println("当前外网ip--->" + ip);
                                    System.out.println("检测到本地外网IP与Cloudflare配置IP不相符，将替换,检测时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                    Map<String, java.io.Serializable> params = new HashMap<>();
                                    params.put("type", "A");
                                    params.put("name", subDomain);
                                    params.put("content", ip);
                                    params.put("ttl", 600);
                                    params.put("proxied", false);
                                    String recordData = HttpUtil.doPut(URL + "/zones/" + Config.ZoneIds.get(domain) + "/dns_records/" + result.get("id"), new StringEntity(JSONObject.toJSONString(params), StandardCharsets.UTF_8), header);
                                    System.out.println("执行结果----->" + recordData);
                                    Map map = new Gson().fromJson(recordData, Map.class);
                                    if ((boolean) map.get("success")) {
                                        System.out.println("更新成功");
                                    }
                                }
                            }
                        }
                        System.out.println("------------------------------------------");
                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("运行出现错误！");
            System.out.println(e.getMessage());
        }
    }

    public static String getZoneId(Map header, String domain) {
        String zoneData = HttpUtil.doGet(URL + "/zones?name=" + domain, header);
//        System.out.println("zone-->" + zoneData);
        Map zone = new Gson().fromJson(zoneData, Map.class);
        List<Map> zoneResults = (List<Map>) zone.get("result");
        for (Map result : zoneResults) {
            if (domain.equals(result.get("name"))) {
                return (String) result.get("id");
            }
        }
        return null;
    }

//    /**
//     * 测试main
//     */
//    public static void main(String[] args) throws IOException {
//        String email = "linglouyi@gmail.com";
//        String key = "bf8ea6f330a02b34d16ef411c19a33423328a";
//        String domain = "linglouyi.tk";
//        Map<String,String> header = new HashMap<>();
//        header.put("X-Auth-Email",email);
//        header.put("X-Auth-Key",key);
//        header.put("Content-Type","application/json");
//        CloudflareDns.dns(header,domain,"www","http://whois.pconline.com.cn/ipJson.jsp?json=true",CloudflareDns.getZoneId(header,domain));
//    }
}
