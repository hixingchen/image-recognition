package com.hixingchen.modules;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping(value = "import")
public class ImageController {

    @PostMapping("imageRecognition")
    public String imageRecognition(MultipartFile file) throws IOException, TesseractException {
        //设置配置文件夹微视、识别语言、识别模式
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("./tessdata");
        //设置识别语言为中文简体，（如果要设置为英文可改为"eng"）
        tesseract.setLanguage("chi_sim");
        //使用 OSD 进行自动页面分割以进行图像处理
        tesseract.setPageSegMode(1);
        //设置引擎模式是神经网络LSTM引擎
        tesseract.setOcrEngineMode(1);
        //开始识别整张图片中的文字
        File image = multiPartFileToFile(file);
        String result = tesseract.doOCR(image);
        if (result.indexOf("健康证明") != -1){
            result = "是健康证";
        }else{
            result = "不是健康证";
        }
        File f = new File(image.toURI());
        if (f.delete()){
            System.out.println("删除成功");
        }else {
            System.out.println("删除失败");
        }
        return result;
    }

    public File multiPartFileToFile(MultipartFile multipartFile){
        //文件上传前的名称
        String fileName = multipartFile.getOriginalFilename();
        File file = new File(fileName);
        OutputStream out = null;
        try{
            //获取文件流，以文件流的方式输出到新文件
            out = new FileOutputStream(file);
            byte[] ss = multipartFile.getBytes();
            for(int i = 0; i < ss.length; i++){
                out.write(ss[i]);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
}
