<h1>DDNS</h1>

一个简单的将自己的外网IP自动设置到阿里云的应用

适用与自己有公网ip但有一天变换个十几次又不想买服务器但人们

运行环境jre>=1.8

参数说明：key_ip=accessKeyId,secret=accessSecret,time=扫描公网IP间隔时间ms,ip_url=获取公网IP的api,domain=要解析的域名,sub_domain=子域

其中accessKeyId和accessSecret为阿里云获取到的