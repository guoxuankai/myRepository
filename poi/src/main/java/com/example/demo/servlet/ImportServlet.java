package com.example.demo.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet("/import")
public class ImportServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<FileItem> fileItemFromRequestList = null;

        try {
            fileItemFromRequestList = getDataFromRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        if (fileItemFromRequestList != null && !fileItemFromRequestList.isEmpty()) {

            for (FileItem fileItem : fileItemFromRequestList) {
                if (!fileItem.isFormField()) { // 判断是不是文件

                    InputStream inputStream = fileItem.getInputStream();
                    try {
                        importExcel(inputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }


    }

    /**
     * 从request中获取FileItem对象列表
     *
     * @param request
     * @return fileItemFromRequestList
     * @throws FileUploadException
     */
    private static List<FileItem> getDataFromRequest(HttpServletRequest request) throws FileUploadException {
        String tempPath = request.getServletContext().getRealPath("/temp");
        System.out.println("tempPath:" + tempPath);
        File file = new File(tempPath);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024 * 1024 * 1024);// 设置缓存大小
        factory.setRepository(file);// 默认情况下 临时文件不会自动删除
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        servletFileUpload.setHeaderEncoding("UTF-8");
        List<FileItem> fileItemFromRequestList = servletFileUpload.parseRequest(request);
        file.delete(); // 删除临时文件
        return fileItemFromRequestList;
    }

    public void importExcel(InputStream inputStream) throws Exception {

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        POIFSFileSystem fileSystem = new POIFSFileSystem(bufferedInputStream);
        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

        HSSFSheet sheet = workbook.getSheetAt(0);

        int lastRowNum = sheet.getLastRowNum();
        for (int i = 2; i <= lastRowNum; i++) {
            HSSFRow row = sheet.getRow(i);
            int id = (int) row.getCell(0).getNumericCellValue();
            String name = row.getCell(1).getStringCellValue();
            int age = (int) row.getCell(2).getNumericCellValue();

            System.out.println(id + "-" + name + "-" + age);
        }
    }


}
