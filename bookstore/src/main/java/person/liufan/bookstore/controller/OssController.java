package person.liufan.bookstore.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import person.liufan.bookstore.constant.MyConstant;
import person.liufan.bookstore.util.Rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liufan E-mail:fan.liu@biz-united.com.cn
 * @version 创建时间：2021/1/28
 */
@RestController
@RequestMapping("/oss")
public class OssController {
    @RequestMapping("/policy")
    public Rest getPolicy() {
        String accessId = "LTAI4G9u2zi6NPsBjg5txB2g";
        String accessKey = "QWoxGJYyaQeyHhSIQJIlhs1CbyTOsv";
        String endpoint = "oss-cn-chengdu.aliyuncs.com";
        String bucket = "gulimall-hello-yuanqi";
        // host的格式为 bucketname.endpoint
        String host = "https://" + bucket + "." + endpoint;
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        /*String callbackUrl = "http://88.88.88.88:8888";*/
        String key = "MyBookstore";
        // 用户上传文件时指定的前缀。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        Map<String, String> respMap = new LinkedHashMap<String, String>();
        try {
            long expireTime = 300;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, "MyBookstore");

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);


            respMap.put("OSSAccessKeyId", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("key", key);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));



        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());

        } finally {
            ossClient.shutdown();
        }
        return Rest.ok().put(MyConstant.DATA, respMap);
    }
}