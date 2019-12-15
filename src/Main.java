import aliyun.AliyunDns;
import cloudflare.CloudflareDns;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;

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
        String key_ip = "linglouyi@gmail.com";
        String secret = "";
        Integer time = 600000;
        String ip_url = "http://whois.pconline.com.cn/ipJson.jsp?json=true";
        String domain = "linglouyi.tk";
        String sud_domain = "file";
        String server = "cf";
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                String[] name = args[i].split("=");
                if ("key_ip".equals(name[0])) {
                    key_ip = name[1];
                } else if ("secret".equals(name[0])) {
                    secret = name[1];
                } else if ("time".equals(name[0])) {
                    time = Integer.valueOf(name[1]);
                } else if ("ip_url".equals(name[0])) {
                    ip_url = name[1];
                } else if ("domain".equals(name[0])) {
                    domain = name[1];
                } else if ("sub_domain".equals(name[0])) {
                    sud_domain = name[1];
                } else if ("server".equals(name[0])){
                    server = name[0];
                } else {
                    System.out.println("无效参数" + args[i] + "，退出！");
                    return;
                }
            }
        } else {
            System.out.println("程序将以默认产数运行！");
        }
        System.out.println("当前key_ip:" + key_ip);
        System.out.println("当前secret:" + secret);
        System.out.println("当前time:" + time);
        System.out.println("当前ip_url:" + ip_url);
        System.out.println("当前domain:" + domain);
        System.out.println("当前sub_domain:" + sud_domain);
        System.out.println("当前server:" + server);
        System.out.println("初始化完成！");


        System.out.println();


        Timer timer = new Timer();
        timer.schedule(new Task(key_ip, secret, ip_url, domain, sud_domain, server), new Date(), time);
    }
}

class Task extends TimerTask {

    private String keyIp;
    private String secret;
    private String ipUrl;
    private String domain;
    private String subDomain;
    private String server;

    private String zoneId;

    private Map<String,String> header = new HashMap<>();

    public Task(String keyIp, String secret,String ipUrl, String domain, String subDomain,String server) {
        this.keyIp = keyIp;
        this.secret = secret;
        this.ipUrl = ipUrl;
        this.domain = domain;
        this.subDomain = subDomain;
        this.server = server;
        if ("cf".equals(server)){
            header.put("X-Auth-Email",keyIp);
            header.put("X-Auth-Key",secret);
            header.put("Content-Type","application/json");
            zoneId = CloudflareDns.getZoneId(header,domain);
            System.out.println("zoneId--->"+zoneId);
        }
    }

    @Override
    public void run() {
        if ("al".equals(server)){
            AliyunDns.dns(keyIp,secret,domain,subDomain,ipUrl);
        }else if ("cf".equals(server)){
            CloudflareDns.dns(header,domain,subDomain,ipUrl,zoneId);
        }else {
            System.out.println("暂时不支持当前解析方式哦");
        }
    }

}
