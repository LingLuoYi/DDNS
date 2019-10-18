package aliyun;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.*;


public class CommonRpc {

    private String key_ip = "LTAI0fMJ06hZs6BJ";

    private String secret = "SH6CpsubXfCI7ujsaox5AdSOWNbqJF";

    public CommonRpc(String key_ip, String secret) {
        this.key_ip = key_ip;
        this.secret = secret;
    }

    public String gain_ip(String domain, String sub_domain, String ip) throws Exception {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", key_ip, secret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("alidns.aliyuncs.com");
        request.setVersion("2015-01-09");
        request.setAction("UpdateDomainRecord");
        request.putQueryParameter("RecordId", domain);
        request.putQueryParameter("RR", sub_domain);
        request.putQueryParameter("Type", "A");
        request.putQueryParameter("Value", ip);
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(response.getData());
        return jsonObject.get("RequestId").getAsString();
    }

    public String get_Record(String domain, String sub_domain) throws Exception {
        String s = null;
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", key_ip, secret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("alidns.aliyuncs.com");
        request.setVersion("2015-01-09");
        request.setAction("DescribeDomainRecords");
        request.putQueryParameter("DomainName", domain);
        CommonResponse response = client.getCommonResponse(request);
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(response.getData());
        JsonArray jsonArray = jsonObject.get("DomainRecords").getAsJsonObject().getAsJsonArray("Record");
        for (JsonElement user : jsonArray) {
            Object s1 = new Gson().fromJson(user, Object.class);
            JsonObject jsonObject1 = (JsonObject) new JsonParser().parse(s1.toString());
//            System.out.println("获取用户解析列表："+user);
            if (sub_domain.equals(jsonObject1.get("RR").getAsString()))
                s = jsonObject1.get("RecordId").getAsString() + "," + jsonObject1.get("Value").getAsString();
        }
        return s;
    }
}
