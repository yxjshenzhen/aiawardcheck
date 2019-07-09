package cn.com.xiaofabo.scia.aiawardcheck.util;

import cn.com.xiaofabo.scia.aiawardcheck.common.ApiResult;
import cn.com.xiaofabo.scia.aiawardcheck.common.ResultCodeEnum;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static boolean isNumber(String str) {
        if (str == null)
            return false;
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(str).matches();
    }

    public static int fixPageNumber(String str){
        List<String> strList = numberList(str);
        if (strList.size() == 1){
            str = strList.get(0);
        }

        if (str.contains("/")){
            String[] strArr = str.split("/");
            str = numberList(strArr[1]).get(0);
        }

        return Integer.parseInt(str);
    }

    public static List<String> numberList(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    public static String getNumber(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static final String md5key = "helloworld";

    public static String md5(String text) {
        if (StringUtils.isEmpty(text)){
            return null;
        }
        String encodeStr= DigestUtils.md5Hex(text + md5key);
        return encodeStr;
    }

    public static final String separator = File.separator;

    /**
     * 下载文件
     * @param filePath 文件上级目录
     * @param fileName 文件名
     * @param newName  下载的展示文件名
     * @return 响应
     */
    public static Object download(String filePath, String fileName, String newName) {

        ResponseEntity<InputStreamResource> response = null;
        try {
            String path = filePath + fileName;
            FileSystemResource fileSystemResource = new FileSystemResource(path);
            InputStream inputStream = fileSystemResource.getInputStream();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition",
                    "attachment; filename=" + new String(newName.getBytes("gbk"), "iso8859-1"));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            response = ResponseEntity.ok().headers(headers)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(inputStream));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return ApiResult.resultWith(ResultCodeEnum.FILE_OUTPUT_NOT_FOUND);
        } catch (IOException e) {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_OUTPUT_IO_EXCEPTION);
        }
        return response;
    }

    public static void delFile(String filePath,String filename){
        File file=new File(filePath+filename);
        if(file.exists()&&file.isFile())
            file.delete();
    }

    public static String getSubDateString(String str){
        Pattern pattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            return matcher.group();
        }
        return "0000-00-00";
    }


    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}");
        Matcher matcher = pattern.matcher("AC4503007-2011-003      2019-06-28");
        while(matcher.find()){
            System.out.println(matcher.group());
        }
    }

}
