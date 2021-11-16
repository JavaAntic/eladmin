package me.zhengjie.modules.system.rest;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PdfRestController
 *
 * @author guoyuan
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
public class PdfRestController {
    @GetMapping("/inspect")
    public String inspect(@RequestParam String filename) {
        PdfDocument pdf = new PdfDocument();
        //Load a PDF file
        pdf.loadFromFile(filename);
        //Save to .docx file
        pdf.saveToFile("ToWord.docx", FileFormat.DOCX);
        pdf.close();
        return "result";
    }
}
