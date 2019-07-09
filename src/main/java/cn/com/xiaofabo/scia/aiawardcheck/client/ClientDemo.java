package cn.com.xiaofabo.scia.aiawardcheck.client;

import org.apache.commons.io.FileUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ClientDemo {

    /**
     * 上传文件
     * @throws  IOException
     */
    public static void main(String[] args){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try{
            String fileName = "sampleinput.doc";
            // 要上传的文件的路径
            String filePath =new String("E:\\sampleinput.doc");
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost =new HttpPost("http://localhost:8080/formatcheckservice");
            // 把文件转换成流对象FileBody
            File file =new File(filePath);
            FileBody bin =new FileBody(file);
            StringBody uploadFileName =new StringBody(fileName, ContentType.create("text/plain", Consts.UTF_8));
            //以浏览器兼容模式运行，防止文件名乱码。
            HttpEntity reqEntity = null;
            try {
                reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .addPart("file", bin)//uploadFile对应服务端类的同名属性<File类型>
                        .addPart("filename", uploadFileName)//uploadFileName对应服务端类的同名属性<String类型>
                        .setCharset(CharsetUtils.get("UTF-8")).build();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            httpPost.setEntity(reqEntity);

            System.out.println("发起请求的页面地址 "+ httpPost.getRequestLine());
            // 发起请求 并返回请求的响应
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileUtils.copyInputStreamToFile(response.getEntity().getContent(),
                        new File("D:\\out.doc"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }finally{
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
