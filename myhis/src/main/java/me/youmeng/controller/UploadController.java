package me.youmeng.controller;

import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class UploadController {
    @Value("${reggie.basePath}")
    private String basePath;

    /**
     * 上传文件，转存到指定路径，路径基址从yml中读取
     * @param file 必须叫这个名，和前端浏览器请求的参数对应
     * @return
     */
    //上传文件请求的路径是怎么回事？
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        //获取原始文件名称，拿到后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //用UUID随机生成文件名，加上后缀，作为新文件名，转存到basePath下
        String name = UUID.randomUUID().toString();
        String fileName = name + suffix;
        try {
            log.info("文件{}被转存至{}",fileName,basePath);
            log.info(basePath + fileName);
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("上传了一张图片:{}",basePath + fileName);
        return R.success(fileName);

    }

    @GetMapping("/download")
    public R<String> download(String name,HttpServletResponse response){
        try {
            //读入文件输入流
            log.info("文件读取路径是：{}",basePath + name);
            FileInputStream fis = new FileInputStream(new File(basePath + name));

            //读取文件输入流的数据，用reponse的响应流响应到页面上
            ServletOutputStream os = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fis.read(bytes)) != -1){
                os.write(bytes);
                os.flush();
            }

            //释放资源
            os.close();
            fis.close();

            return R.success("上传成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return R.error("上传失败！");
        }

    }
}
