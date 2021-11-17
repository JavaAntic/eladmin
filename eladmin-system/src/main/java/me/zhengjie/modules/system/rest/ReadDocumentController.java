package me.zhengjie.modules.system.rest;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.util.RadeWord;
import me.zhengjie.util.ReadWordCopy;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

import static com.spire.pdf.security.SignatureConfigureText.Date;

/**
 * ReadDocumentController
 *
 * @author Jin
 * @Date 10:25 2021/11/16
 */
@RestController
@Api(tags = "POC:文件读取")
@RequestMapping("/poc")
public class ReadDocumentController {

    @Value("${upload.filePath}")
    public String filePath;

    /**
     *  上传文件并更新数据
     */
    @PostMapping("/upload")
    @ApiOperation("上传文件并更新数据")
    public String upload(@RequestParam("file") MultipartFile file,@RequestParam("type") String type) {
        if (file.isEmpty()) {
            return"上传失败，请添加文件";
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 读取word文件,转文件流
      ReadWordCopy.exportWord(inputStream);
        List<ReadWordCopy.Paragraph> paragraphList =  ReadWordCopy.getParagraphList();
        List<ReadWordCopy.Table> tableList = ReadWordCopy.getTableList();
        System.out.println(JSONObject.toJSONString(paragraphList));
        System.out.println(JSONObject.toJSONString(tableList));
        return "ok";
    }

    /**
     * 上传模板
     * @param file 文件
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation("上传模板")
    public String uploadTemplate(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请添加文件";
        }
        String fileName = System.currentTimeMillis() + file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf("."));

        System.out.println(filePath + fileName);
        File dest = new File(filePath + "/template/" + fileName);
        if (!dest.exists()) {
            dest.mkdirs();
        }
        try {
            file.transferTo(dest);
            System.out.println("上传成功");
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("失敗"+e.getMessage());
            return "上传失败，请重新上传";
        }
    }

    /**
     * @param path 想要下载的文件的路径
     * @功能描述 下载文件:
     */
    @PostMapping("/download")
    @ApiOperation("下载文件")
    public void download(@RequestParam("path") String path, HttpServletResponse response) {
        System.out.println(path);
        try {
            RadeWord.downFile(filePath + path,response);
        } catch (Exception e) {
            e.printStackTrace();
            new Exception( "下载失败，原因:"+e.getMessage());
        }
    }

    /**
     * 下载基本模板
     * @功能描述 下载基本模板:
     */
    @PostMapping("/downloadBaseTemplate")
    @ApiOperation("下载基本模板")
    public void downloadBaseTemplate(HttpServletResponse response) {
        String path = "/baseTemplate/baseTemplate.docx";
        try {
            RadeWord.downFile(filePath + path, response);
        } catch (Exception e) {
            e.printStackTrace();
            new Exception("下载失败，原因:" + e.getMessage());
        }
    }
}
