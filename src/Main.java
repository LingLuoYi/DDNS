import aliyun.AliyunDns;
import cloudflare.CloudflareDns;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import config.Config;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));
        for(String log:loggers) {
            Logger logger = (Logger) LoggerFactory.getLogger(log);
            logger.setLevel(Level.INFO);
            logger.setAdditive(false);
        }
        System.out.println(
                " ___      ___   __    _  _______  ___      _______  __   __  __   __  ___  \n" +
                        "|   |    |   | |  |  | ||       ||   |    |       ||  | |  ||  | |  ||   | \n" +
                        "|   |    |   | |   |_| ||    ___||   |    |   _   ||  | |  ||  |_|  ||   | \n" +
                        "|   |    |   | |       ||   | __ |   |    |  | |  ||  |_|  ||       ||   | \n" +
                        "|   |___ |   | |  _    ||   ||  ||   |___ |  |_|  ||       ||_     _||   | \n" +
                        "|       ||   | | | |   ||   |_| ||       ||       ||       |  |   |  |   | \n" +
                        "|_______||___| |_|  |__||_______||_______||_______||_______|  |___|  |___| "
        );
        System.out.println("*********************************************************************************");
        System.out.println("支持使用阿里解析的域名和cloudflare解析的域名");
        System.out.println("默认使用阿里云");
        System.out.println("freenom申请的免费域名可以使用cloudflare进行解析在使用本程序进行动态更新");
        System.out.println("本程序适用与有公网ip，但IP不断进行更改但环境");
        System.out.println("使用阿里云进行解析的需要在阿里云上申请<accessKeyId>和<accessSecret>");
        System.out.println("使用cloudflare进行解析的需要在cloudflare上申请Origin CA Key");
        System.out.println("参数说明：key_ip=accessKeyId,secret=accessSecret,time=扫描公网IP间隔时间ms,ip_url=获取公网IP的api,domain=要解析的域名,sub_domain=子域,server=服务商（al  and  cf）");
        System.out.println("如果是使用cloudflare,则key_ip为cloudflare的登录邮箱，secret为cloudflare的Origin CA Key，其他不变");
        System.out.println("*********************************************************************************");
        System.out.println("开始初始化！");
        Config.KeyIp = "linglouyi@gmail.com";
        Config.Secret = "";
        Config.Time = 600000;
        Config.IpUrl = "http://whois.pconline.com.cn/ipJson.jsp?json=true";
        Config.Domain = new String[]{"linglouyi.tk"};
        Config.SubDomain = new String[]{"file"};
        Config.Server = "cf";
        Config.Config = "/Users/liyi/Desktop/DDNS/config.json";
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                String[] name = args[i].split("=");
                if ("config".equals(name[0])){
                    Config.Config = name[1];
                    break;
                }else {
                    if ("key_ip".equals(name[0])) {
                        Config.KeyIp = name[1];
                    } else if ("secret".equals(name[0])) {
                        Config.Secret = name[1];
                    } else if ("time".equals(name[0])) {
                        Config.Time = Integer.parseInt(name[1]);
                    } else if ("ip_url".equals(name[0])) {
                        Config.IpUrl = name[1];
                    } else if ("domain".equals(name[0])) {
                        Config.Domain = new String[]{name[1]};
                    } else if ("sub_domain".equals(name[0])) {
                        Config.SubDomain = new String[]{name[1]};
                    } else if ("server".equals(name[0])) {
                        Config.Server = name[0];
                    } else {
                        System.out.println("无效参数" + args[i] + "，退出！");
                        return;
                    }
                }
            }
        } else {
            System.out.println("程序将以默认产数运行！");
        }
        if (!"".equals(Config.Config)){
            Map map = new Gson().fromJson(Config.txt2String(new File(Config.Config)),Map.class);
            Config.KeyIp = (String) map.get("keyIp");
            Config.Secret = (String) map.get("secret");
            Config.Time = Double.valueOf(map.get("time").toString()).intValue();
            Config.IpUrl = (String) map.get("ipUrl");
            Config.Domain = (String[]) ((ArrayList) map.get("domain")).toArray(new String[0]);
            Config.SubDomain = (String[])((ArrayList)map.get("subDomain")).toArray(new String[0]);
            Config.Server = (String) map.get("server");
        }
        System.out.println("当前key_ip:" + Config.KeyIp);
        System.out.println("当前secret:" + Config.Secret);
        System.out.println("当前time:" + Config.Time);
        System.out.println("当前ip_url:" + Config.IpUrl);
        System.out.println("当前domain:" + Arrays.toString(Config.Domain));
        System.out.println("当前sub_domain:" + Arrays.toString(Config.SubDomain));
        System.out.println("当前server:" + Config.Server);
        System.out.println("初始化完成！");


        System.out.println();

        Timer timer = new Timer();
        timer.schedule(new Task(Config.KeyIp, Config.Secret, Config.IpUrl, Config.Domain, Config.SubDomain, Config.Server), new Date(), Config.Time);
    }
}

class Task extends TimerTask {

    private String keyIp;
    private String secret;
    private String ipUrl;
    private String[] domains;
    private String[] subDomains;
    private String server;

    private Map<String,String> header = new HashMap<>();

    public Task(String keyIp, String secret,String ipUrl, String[] domain, String[] subDomain,String server) {
        this.keyIp = keyIp;
        this.secret = secret;
        this.ipUrl = ipUrl;
        this.domains = domain;
        this.subDomains = subDomain;
        this.server = server;
        if ("cf".equals(server)){
            header.put("X-Auth-Email",keyIp);
            header.put("X-Auth-Key",secret);
            header.put("Content-Type","application/json");
            for (String d:domains) {
                Config.ZoneIds.put(d,CloudflareDns.getZoneId(header,d));
            }
        }
    }

    @Override
    public void run() {
        if ("al".equals(server)){
            AliyunDns.dns(keyIp,secret,domains,subDomains,ipUrl);
        }else if ("cf".equals(server)){
            CloudflareDns.dns(header,domains,subDomains,ipUrl);
        }else {
            System.out.println("暂时不支持当前解析方式哦");
        }
    }

}
