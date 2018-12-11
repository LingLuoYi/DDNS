import Internet.IP;
import aliyun.CommonRpc;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) throws Exception {
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
        System.out.println("本程序使用阿里云sdk");
        System.out.println("仅支持使用阿里云进行解析的域名");
        System.out.println("本程序适用与有公网ip，但IP不断进行更改但环境");
        System.out.println("使用本程序必须要在阿里云上申请<accessKeyId>和<accessSecret>");
        System.out.println("参数说明：key_ip=accessKeyId,secret=accessSecret,time=扫描公网IP间隔时间ms,ip_url=获取公网IP的api,domain=要解析的域名");
        System.out.println("*********************************************************************************");
        System.out.println("开始初始化！");
        String key_ip = "LTAI0fMJ06hZs6BJ";
        String secret = "";
        Integer time = 600000;
        String ip_url = "http://whois.pconline.com.cn/ipJson.jsp?json=true";
        String domain = "kylinhash.com";
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                String[] name = args[i].split("=");
                if ("key_ip".equals(name[0])){
                    key_ip = name[1];
                }else if ("secret".equals(name[0])){
                    secret = name[1];
                }else if ("time".equals(name[0])){
                    time = Integer.valueOf(name[1]);
                }else if ("ip_url".equals(name[0])){
                    ip_url = name[1];
                }else if ("domain".equals(name[0])){
                    domain = name[1];
                }else {
                    System.out.println("产数设置错误，退出！");
                    return;
                }
            }
        }else {
            System.out.println("程序将以默认产数运行！");
        }
        System.out.println("当前key_ip:"+key_ip);
        System.out.println("当前secret:"+secret);
        System.out.println("当前time:"+time);
        System.out.println("当前ip_url:"+ip_url);
        System.out.println("当前domain:"+domain);
        System.out.println("初始化完成！");


        System.out.println();


        Timer timer = new Timer();
        timer.schedule(new Task(key_ip,secret,time,ip_url,domain),new Date(),time);
    }
}

class Task extends TimerTask{

    String key_ip ;
    String secret ;
    Integer time ;
    String ip_url ;
    String domain ;

    public Task(String key_ip,String secret,Integer time,String ip_url,String domain){
        this.key_ip = key_ip;
        this.secret = secret;
        this.time = time;
        this.ip_url = ip_url;
        this.domain = domain;
    }

    @Override
    public void run() {
        boolean s = true;
        try {

            String value = null;
            String RecordId = null;


        System.out.println("获取RecordId");
        CommonRpc commonRpc = new CommonRpc();
        if (s) {
            String[] re = (commonRpc.get_Record(domain)).split(",");
            System.out.println("RecordId:" + re[0]);
            System.out.println("value:" + re[1]);
            value = re[1];
            if (value == null) {
                System.out.println("无法正确获取到阿里云配置IP，请检查是否添加包含ddns解析，退出！");
                return;
            }
            RecordId = re[0];
            if (RecordId == null) {
                System.out.println("无法正确获取到阿里云record_id,请检查是否包含了域名解析，退出！");
                return;
            }
            s = false;
        }
        System.out.println("获取当前外网ip");
        IP ip=new IP();
        String wip = ip.get_ip(ip_url);
        if (wip == null){
            System.out.println("无法正确获取到外网IP，请检查api地址,退出");
            return;
        }
        System.out.println("IP:"+wip);
        if (!wip.equals(value)){
            System.out.println("检测到本地外网IP与阿里云配置IP不相符，将替换");
            String b = commonRpc.gain_ip(RecordId,wip);
            System.out.println("成功，请求码："+b);
            s = true;
        }else {
            System.out.println("当前外网IP与阿里云配置相同");
            System.out.println("外网ip"+wip);
            System.out.println("阿里ip"+value);
            s = false;
        }
        }catch (Exception e){
            System.out.println("运行出现错误！");
            System.out.println(e.getMessage());
            s = false;
        }
    }
}
