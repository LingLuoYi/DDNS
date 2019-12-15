<h1>DDNS</h1>

轮询公网ip，如果有改变则设置到相应的dns服务商

支持阿里和cloudflare

适用与自己有公网ip但有一天变换个十几次又不想买服务器但人们
自己的宽带可以要公网ip

设置好自己的参数，包里面提供了启动脚本

运行环境jre>=1.8

参数说明：key_ip=accessKeyId,secret=accessSecret,time=扫描公网IP间隔时间ms,ip_url=获取公网IP的api,domain=要解析的域名,sub_domain=子域

如果是使用cloudflare,则key_ip为cloudflare的登录邮箱，secret为cloudflare的Origin CA Key，其他不变

其中accessKeyId和accessSecret为阿里云获取到的