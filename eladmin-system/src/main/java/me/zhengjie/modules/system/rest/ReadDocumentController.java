package me.zhengjie.modules.system.rest;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.util.ReadWordCopy;
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
     * @param path 想要下载的文件的路径
     * @功能描述 下载文件:
     */
    @PostMapping("/download")
    @ApiOperation("下载文件")
    public void download(@RequestParam("path") String path, HttpServletResponse response) {
        System.out.println(path);
        try {
            // path是指想要下载的文件的路径
            String downPath = filePath + path;
            File file = new File(downPath);
            // 获取文件名
            String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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
     * 下载基本模板
     * @功能描述 下载基本模板:
     */
    @PostMapping("/downloadBaseTemplate")
    @ApiOperation("下载基本模板")
    public void downloadBaseTemplate(HttpServletResponse response) {
        try {
            String path = "/baseTemplate/baseTemplate.docx";
            // path是指想要下载的文件的路径
            String downPath = filePath + path;
            File file = new File(downPath);
            // 获取文件名
            String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
