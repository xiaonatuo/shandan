package com.keyware.shandan.common.util;

import cn.hutool.core.io.FileUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * 基于POI的文件内容读取工具类
 *
 * @author GuoXin
 * @since 2021/6/29
 */
public class PoiFileReadUtil {

    /**
     * 解析文件文本内容
     *
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String parseTextByFile(File file) throws Exception {
        if (file.exists() && file.isFile()) {
            String fileSuffix = FileUtil.extName(file);
            switch (fileSuffix.toLowerCase()) {
                case "doc":
                    return readContentByDoc(file);
                case "docx":
                    return readContentByDocx(file);
                case "xls":
                    return readContentByXls(file);
                case "xlsx":
                    return readContentByXlsx(file);
                case "ppt":
                    return readContentByPpt(file);
                case "pptx":
                    return readContentByPptx(file);
                case "rtf":
                    return readContentByRtf(file);
                case "pdf":
                    return readContentByPdf(file);
                case "txt":
                    return readContentByTxt(file);
                default:
                    return "";
            }
        } else {
            throw new Exception("文件不存在");
        }
    }

    /**
     * 从DOC文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByDoc(File file) throws IOException {
        InputStream fis = new FileInputStream(file);
        WordExtractor wordExtractor = new WordExtractor(fis);//使用HWPF组件中WordExtractor类从Word文档中提取文本或段落
        StringBuilder result = new StringBuilder();
        for (String words : wordExtractor.getParagraphText()) {//获取段落内容
            result.append(words).append("\n");
        }
        fis.close();
        return result.toString();
    }

    /**
     * 从DOCX文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByDocx(File file) throws IOException {
        OPCPackage opcPackage = POIXMLDocument.openPackage(file.getAbsolutePath());//包含所有POI OOXML文档类的通用功能，打开一个文件包。
        XWPFDocument document = new XWPFDocument(opcPackage);//使用XWPF组件XWPFDocument类获取文档内容
        List<XWPFParagraph> paras = document.getParagraphs();
        StringBuilder result = new StringBuilder();
        for (XWPFParagraph paragraph : paras) {
            result.append(paragraph.getText()).append("\n");
        }
        return result.toString();
    }

    /**
     * 从XLS文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByXls(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(is));
        ExcelExtractor extractor = new ExcelExtractor(wb);
        extractor.setFormulasNotResults(false);
        extractor.setIncludeSheetNames(false);
        String result = extractor.getText();
        extractor.close();
        is.close();
        return result;
    }

    /**
     * 从XLSX文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByXlsx(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        XSSFExcelExtractor extractor = new XSSFExcelExtractor(new XSSFWorkbook(is));
        extractor.setIncludeSheetNames(false);
        String result = extractor.getText();
        extractor.close();
        is.close();
        return result;
    }

    /**
     * 从PDF文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByPdf(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        StringBuilder result = new StringBuilder();
        if (!document.isEncrypted()) {
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            PDFTextStripper textStripper = new PDFTextStripper();
            String exposeContent = textStripper.getText(document);
            String[] content = exposeContent.split("\\n");
            for (String line : content) {
                result.append(line);
            }
        }
        document.close();
        return result.toString();
    }

    /**
     * 从RTF文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByRtf(File file) throws IOException, BadLocationException {
        DefaultStyledDocument styledDoc = new DefaultStyledDocument();
        // 创建文件输入流
        InputStream is = new FileInputStream(file);
        new RTFEditorKit().read(is, styledDoc, 0);
        is.close();
        byte[] buff = styledDoc.getText(0, styledDoc.getLength()).getBytes(StandardCharsets.ISO_8859_1);

        return new String(buff, get_charset(buff));
    }

    /**
     * 从PPT文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByPpt(File file) throws IOException {
        // word 2003： 图片不会被读取
        InputStream fis = new FileInputStream(file);
        PowerPointExtractor ex = new PowerPointExtractor(fis);
        String text = ex.getText().replace("\n", "");
        ex.close();
        fis.close();
        return text;
    }

    /**
     * 从PPTX文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByPptx(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        XMLSlideShow slide = new XMLSlideShow(is);
        XSLFPowerPointExtractor extractor = new XSLFPowerPointExtractor(slide);
        extractor.close();
        is.close();
        return extractor.getText().replace("\n", "");
    }

    /**
     * 从TXT文档读取内容
     *
     * @param file 文件
     * @return 文件内容
     */
    private static String readContentByTxt(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return getCharset(fis);
    }

    /**
     * 获取文件编码格式
     *
     * @param is 文件的输入流
     * @return -
     * @throws IOException -
     */
    public static String getCharset(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);

        int len;
        byte[] buf = new byte[1024];
        // 定义一个输出流，相当StringBuffer（），会根据读取数据的大小，调整byte的数组长度
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = bis.read(buf)) != -1) {
            // 如果有数据的话，就把数据添加到输出流
            //这里直接用字符串StringBuffer的append方法也可以接收
            bos.write(buf, 0, len);
        }
        // 把文件输出流的数据，放到字节数组
        byte[] buffer = bos.toByteArray();

        //关闭所有的流
        bos.close();
        bis.close();
        is.close();

        return new String(buffer, get_charset(buffer));
    }

    /**
     * 获得文件编码方式
     *
     * @param file -
     * @return -
     * @throws IOException -
     */
    private static String get_charset(byte[] file) throws IOException {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        InputStream bis = null;
        try {
            boolean checked = false;
            bis = new ByteArrayInputStream(file);
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
        return charset;
    }

    public static void convertToUTF8(MultipartFile file, Consumer<? super MultipartFile> action) throws IOException {
        File temp = new File(file.getName());
        String charset = get_charset(file.getBytes());
        if("UTF-8".equalsIgnoreCase(charset)){
            action.accept(file);
        }
        BufferedReader  reader = new BufferedReader(new InputStreamReader(file.getInputStream(), charset));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8));

        String str = "";
        while ((str = reader.readLine()) != null) {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            str = new String(bytes, 0, bytes.length);
            writer.write(str + "\r\n");
        }
        reader.close();
        writer.close();

        FileInputStream fileInput = new FileInputStream(temp);
        MultipartFile toMultipartFile = new MockMultipartFile(file.getName(), file.getOriginalFilename(), "text/plain", IOUtils.toByteArray(fileInput));
        action.accept(toMultipartFile);
        temp.deleteOnExit();
    }
}
