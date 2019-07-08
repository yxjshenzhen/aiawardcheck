# 简要说明
裁决书格式校对系统简要说明
## 安装说明
将aiawardcheck-x.y.z.war部署于网络应用服务器（如Tomcat）。
在保证网络畅通的情况下使用以下地址调用裁决书格式校对服务：
http://[host]:[port]/aiawardcheck/formatcheckservice

服务调用方式为HTTP POST。

呼叫服务时应使用'file'标签上传裁决书输入文件。系统将返回已校对的word文件作为请求的回复。

## 调用示例
使用curl调用服务格式为：
curl -F "file=@/D/tmp/sampleinput3.docx" http://[host]:[port]/aiawardcheck/formatcheckservice
其中host为服务器URL，port为服务端口。
