package aliyun;

import Internet.IP;

import java.text.SimpleDateFormat;
import java.util.Date;

//阿里云的dns解析放到这里来
public class AliyunDns {

    public static void dns(String keyIp, String secret,String domain,String subDomain,String ipUrl){
        boolean s = true;
        try {
            String value = null;
            String RecordId = null;
            System.out.println("获取RecordId");
            CommonRpc commonRpc = new CommonRpc(keyIp, secret);
            if (s) {
                String[] re = (commonRpc.get_Record(domain, subDomain)).split(",");
                System.out.println("RecordId:" + re[0]);
                System.out.println("value:" + re[1]);
                value = re[1];
                if (value == null) {
                    System.out.println("无法正确获取到阿里云配置IP，请检查是否添加" + subDomain + "解析，退出！");
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
            String wip = IP.get_ip(ipUrl);
            if (wip == null) {
                System.out.println("无法正确获取到外网IP，请检查api地址,退出");
                return;
            }
            System.out.println("IP:" + wip);
            if (!wip.equals(value)) {
                System.out.println("检测到本地外网IP与阿里云配置IP不相符，将替换,检测时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                String b = commonRpc.gain_ip(RecordId, subDomain, wip);
                System.out.println("成功，请求码：" + b);
                s = true;
            } else {
                System.out.println("当前外网IP与阿里云配置相同，检测时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                s = false;
            }
        } catch (Exception e) {
            System.out.println("运行出现错误！");
            System.out.println(e.getMessage());
            s = false;
        }
    }
}
