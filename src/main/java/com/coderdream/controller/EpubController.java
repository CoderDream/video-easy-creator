//package com.coderdream.controller;
//
//import com.coderdream.service.EpubProcessingService;
//import com.coderdream.util.FileUtils;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.ResourceUtils;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/epub")
//@Slf4j
//public class EpubController {
//
//    @Resource
//    private EpubProcessingService epubProcessingService;
//
//
//    @PostMapping(value = "/upload1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Map<String, Object> upload1(
//        @RequestParam("file") MultipartFile file) {
//        try {
//            String dir =
//                ResourceUtils.getURL("classpath:").getPath() + "static/upload";
//            String realPath = dir.replace('/', '\\').substring(1, dir.length());
//            //用于查看路径是否正确
//            System.out.println(realPath);
//            File fileDir = new File(dir);
//            if (!fileDir.exists()) {
//                boolean mkdir = fileDir.mkdirs();
//                log.info("文件目录创建成功: {}", mkdir);
//            }
//
//            // 将MultipartFile转换为File对象（可以选择更合适的临时文件处理方式，这里为简单示例）
//
//            String newFileName = dir + file.getOriginalFilename();
//            log.info("新文件名为：{}", newFileName);
//
//            log.info("realPath：{}", realPath);
//            log.info("file.getOriginalFilename()：{}",
//                file.getOriginalFilename());
//
//            File tempFile = new File(realPath,
//                Objects.requireNonNull(file.getOriginalFilename()));
//            file.transferTo(tempFile);
//
//            log.info("上传的文件名称为：{}", tempFile.getName());
//            log.info("上传的文件大小为：{}", tempFile.length());
//            log.info("上传的文件类型为：{}", file.getContentType());
//
////            log.info("上传的文件内容为：{}", file.getInputStream());
////            log.info("上传的文件名称为：{}", file.getOriginalFilename());
//            log.info("新文件的getAbsoluteFile为：{}",
//                tempFile.getAbsoluteFile());
//            log.info("新文件的getAbsolutePath为：{}",
//                tempFile.getAbsolutePath());
//
////            // 调用Service层方法处理文件，获取生成的zip文件
////            File zipFile = epubProcessingService.processEpubToZip(tempFile);
////
////            // 读取zip文件内容为字节数组
////            byte[] zipBytes = java.nio.file.Files.readAllBytes(
////                zipFile.toPath());
////
////            // 设置响应头信息，指定文件名以及内容类型为zip格式
////            HttpHeaders headers = new HttpHeaders();
////            headers.add(HttpHeaders.CONTENT_DISPOSITION,
////                "attachment; filename=" + zipFile.getName());
////            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
////
////            // 返回包含zip文件字节数组的响应实体
////            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("code", 200);
//            map.put("msg", "上传成功");
//
//            // 上传的文件名称
//            map.put("上传的文件名称为：", tempFile.getName());
//
//            // 上传的文件大小
//            map.put("上传的文件大小为：", tempFile.length());
//
//            // 上传的文件类型（这里假设file对象有getContentType方法）
//            map.put("上传的文件类型为：",
//                "file.getContentType()"); // 注意：这里直接使用了字符串，因为getContentType()需要file对象
//
//            // 上传的文件内容（InputStream通常不易直接存储，可能需要转换为字节数组或Base64编码字符串）
//            try (InputStream inputStream = new FileInputStream(
//                tempFile) /* 假设这里能获取到file的InputStream */) {
//                // 示例：将InputStream转换为字节数组（注意：这可能会消耗大量内存对于大文件）
//                byte[] fileContent = inputStream.readAllBytes();
//                map.put("上传的文件内容为：", fileContent);
//            } catch (Exception e) {
//                e.printStackTrace();
//                // 处理异常，例如记录错误日志或返回错误消息
//            }
//
//            // 上传的文件名称（这里再次使用file对象的getOriginalFilename方法）
//            // map.put("上传的文件名称为：", file.getOriginalFilename()); // 注意：这与上面的tempFile.getName()可能重复，根据实际需求选择使用
//
//            // 新文件的绝对文件对象
//            map.put("新文件的getAbsoluteFile为：", tempFile.getAbsoluteFile());
//
//            // 新文件的绝对路径
//            map.put("新文件的getAbsolutePath为：", tempFile.getAbsolutePath());
//
////            map.put("path", File.separator + "img" + File.separator + newFileName);
////            map.put("path2", "/img/" + newFileName);
////            map.put("data", path);
//            return map;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;//new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//
//    @PostMapping(value = "/uploadEpub", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<org.springframework.core.io.Resource> uploadEpub(
//        @RequestParam("file") MultipartFile file) {
//        try {
//            String dir =
//                ResourceUtils.getURL("classpath:").getPath() + "static/upload";
//            String realPath = dir.replace('/', '\\').substring(1, dir.length());
//            //用于查看路径是否正确
//            System.out.println(realPath);
//            File fileDir = new File(dir);
//            if (!fileDir.exists()) {
//                boolean mkdir = fileDir.mkdirs();
//                log.info("文件目录创建成功: {}", mkdir);
//            }
//
//            // 将MultipartFile转换为File对象（可以选择更合适的临时文件处理方式，这里为简单示例）
//
//            // application/epub+zip
//            //限制文件上传的类型
//            String contentType = file.getContentType();
//            if ("application/epub+zip".equals(contentType)) {
//                String newFileNameStr = dir + File.separator + file.getOriginalFilename();
//                File tempFile = new File(newFileNameStr);  //   new File(realPath,                    Objects.requireNonNull(file.getOriginalFilename()));
//                file.transferTo(tempFile);
//
////                String newFileName = dir + file.getOriginalFilename();
//                log.info("新文件名为：{}", newFileNameStr);
//
//                log.info("realPath：{}", realPath);
//                log.info("file.getOriginalFilename()：{}",
//                    file.getOriginalFilename());
//
//                log.info("上传的文件名称为：{}", tempFile.getName());
//                log.info("上传的文件大小为：{}", tempFile.length());
//                log.info("上传的文件类型为：{}", file.getContentType());
////            log.info("上传的文件内容为：{}", file.getInputStream());
////            log.info("上传的文件名称为：{}", file.getOriginalFilename());
//                log.info("新文件的getAbsoluteFile为：{}",
//                    tempFile.getAbsoluteFile());
//                log.info("新文件的getAbsolutePath为：{}",
//                    tempFile.getAbsolutePath());
//
//                // 调用Service层方法处理文件，获取生成的zip文件
//                String zipFileName = epubProcessingService.processEpubToZip(tempFile);
//
////                // 读取zip文件内容为字节数组
////                byte[] zipBytes = java.nio.file.Files.readAllBytes(
////                    zipFile);
////
////                // 设置响应头信息，指定文件名以及内容类型为zip格式
////                HttpHeaders headers = new HttpHeaders();
////                headers.add(HttpHeaders.CONTENT_DISPOSITION,
////                    "attachment; filename=" + zipFile.getName());
////                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
////
////                // 返回包含zip文件字节数组的响应实体
////                return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
//
////                String fileNameRaw = zipFile.getName();
////                log.info("zipFile:{}", zipFile);
//                log.info("zipFileName:{}", zipFileName);
//                org.springframework.core.io.Resource resource = new ClassPathResource(zipFileName);
//                // 文件名编码，防止中文乱码
//                String fileNameRaw = zipFileName.substring(zipFileName.lastIndexOf(File.separator) + 1);
//                String fileName = URLEncoder.encode(fileNameRaw, StandardCharsets.UTF_8);
//                log.info("fileNameRaw:{}", fileNameRaw);
//                log.info("fileName:{}", fileName);
//                // 构建响应实体：ResponseEntity, 包含状态码、头、响应体等信息
//                ResponseEntity<org.springframework.core.io.Resource> responseEntity = ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"") // 设置响应头信息
//                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE) // 设置响应内容类型
//                    .body(resource);// 设置响应体
//                log.info("方式4: 下载文件成功");
//                log.info("responseEntity:{}", responseEntity);
//                return responseEntity;
//            } else {
//                log.error("上传的文件类型为：{}", file.getContentType());
//                log.error("上传失败！");
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PostMapping(value = "/uploadEpub2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public void uploadEpub2(
//        @RequestParam("file") MultipartFile file, HttpServletResponse response) {
//        try {
//            String dir =
//                ResourceUtils.getURL("classpath:").getPath() + "static/upload";
//            String realPath = dir.replace('/', '\\').substring(1, dir.length());
//            //用于查看路径是否正确
//            System.out.println(realPath);
//            File fileDir = new File(dir);
//            if (!fileDir.exists()) {
//                boolean mkdir = fileDir.mkdirs();
//                log.info("文件目录创建成功: {}", mkdir);
//            }
//
//            // 将MultipartFile转换为File对象（可以选择更合适的临时文件处理方式，这里为简单示例）
//
//            // application/epub+zip
//            //限制文件上传的类型
//            String contentType = file.getContentType();
//            if ("application/epub+zip".equals(contentType)) {
//                String newFileNameStr = dir + File.separator + file.getOriginalFilename();
//                File tempFile = new File(newFileNameStr);  //   new File(realPath,                    Objects.requireNonNull(file.getOriginalFilename()));
//                file.transferTo(tempFile);
//
////                String newFileName = dir + file.getOriginalFilename();
//                log.info("新文件名为：{}", newFileNameStr);
//
//                log.info("realPath：{}", realPath);
//                log.info("file.getOriginalFilename()：{}",
//                    file.getOriginalFilename());
//
//                log.info("上传的文件名称为：{}", tempFile.getName());
//                log.info("上传的文件大小为：{}", tempFile.length());
//                log.info("上传的文件类型为：{}", file.getContentType());
////            log.info("上传的文件内容为：{}", file.getInputStream());
////            log.info("上传的文件名称为：{}", file.getOriginalFilename());
//                log.info("新文件的getAbsoluteFile为：{}",
//                    tempFile.getAbsoluteFile());
//                log.info("新文件的getAbsolutePath为：{}",
//                    tempFile.getAbsolutePath());
//
//                // 调用Service层方法处理文件，获取生成的zip文件
//                String zipFileName = epubProcessingService.processEpubToZip(tempFile);
//
////                // 读取zip文件内容为字节数组
////                byte[] zipBytes = java.nio.file.Files.readAllBytes(
////                    zipFile);
////
////                // 设置响应头信息，指定文件名以及内容类型为zip格式
////                HttpHeaders headers = new HttpHeaders();
////                headers.add(HttpHeaders.CONTENT_DISPOSITION,
////                    "attachment; filename=" + zipFile.getName());
////                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
////
////                // 返回包含zip文件字节数组的响应实体
////                return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
//
////                String fileNameRaw = zipFile.getName();
////                log.info("zipFile:{}", zipFile);
//                log.info("zipFileName:{}", zipFileName);
////                org.springframework.core.io.Resource resource = new ClassPathResource(zipFileName);
//                // 文件名编码，防止中文乱码
//                String fileNameRaw = zipFileName.substring(zipFileName.lastIndexOf(File.separator) + 1);
//                String fileName = URLEncoder.encode(fileNameRaw, StandardCharsets.UTF_8);
//                log.info("fileNameRaw:{}", fileNameRaw);
//                log.info("fileName:{}", fileName);
//                // 构建响应实体：ResponseEntity, 包含状态码、头、响应体等信息
////                ResponseEntity<org.springframework.core.io.Resource> responseEntity = ResponseEntity.ok()
////                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"") // 设置响应头信息
////                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE) // 设置响应内容类型
////                    .body(resource);// 设置响应体
////                log.info("方式4: 下载文件成功");
////                log.info("responseEntity:{}", responseEntity);
//
//                // 指定要下载的文件
//                File fileTTT  =  new File(zipFileName); // ResourceUtils.getFile("classpath:文件说明.txt");
//                // 文件转成字节数组
//                byte[] fileBytes = Files.readAllBytes(fileTTT.toPath());
//                // 文件名编码，防止中文乱码
////                String fileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
//                // 设置响应头信息
//                response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
//                // 内容类型为通用类型，表示二进制数据流
//                response.setContentType("application/octet-stream");
//                // 循环，变读取变输出，可编码大文件OOM
//                try (InputStream inputStream = new FileInputStream(fileTTT);
//                    OutputStream outputStream = response.getOutputStream()) {
//                    byte[] bytes = new byte[1024]; // 缓冲区
//                    int readLength;     // 每次读取的长度
//                    while ((readLength = inputStream.read(bytes)) != -1) {    // 读取到缓冲区，返回读取的长度
//                        outputStream.write(bytes, 0, readLength);  // 输出到响应流
//                    }
//                }
//
//               // return responseEntity;
//            } else {
//                log.error("上传的文件类型为：{}", file.getContentType());
//                log.error("上传失败！");
//               // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//           // return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
////    @RequestMapping(value = "/")
////    public ResponseEntity<byte[]> downloadInterviewFile() throws Exception {
////        // 根据面试官主键编码 下载文件
////        List<InterviewFile> interviewFiles =  this.interviewFileService.getAll(interviewfile);
////
////        ByteArrayOutputStream byteOutPutStream = null;
////
////        if (!interviewFiles.isEmpty()) {
////            //单个文件名称
////            String interviewFileName = "";
////            //文件后缀
////            String fileNameSuffix = "";
////            //创建一个集合用于 压缩打包的参数
////            List<Map<String, String>> parms = new ArrayList<>();
////            //创建一个map集合
////            Map<String, String> fileMaps =null;
////            //得到存储文件盘符 例  D:
////            String root = properties.getValue("upload.root");
////            //创建一个字节输出流
////            byteOutPutStream = new ByteArrayOutputStream();
////
////            for (InterviewFile file : interviewFiles) {
////
////                fileMaps = new HashMap<>();
////
////                interviewFileName = file.getFileName();
////
////                fileNameSuffix = file.getFileSuffix();
////                //将单个存储路径放入
////                fileMaps.put("filePath", root.concat(file.getFilePath()));
////                //将单个文件名放入
////                fileMaps.put("fileName", interviewFileName.concat("." + fileNameSuffix));
////                //放入集合
////                parms.add(fileMaps);
////
////            }
////            //压缩文件
////            FileUtils.batchFileToZIP(parms, byteOutPutStream);
////        }
////
////
////        HttpHeaders headers = new HttpHeaders();
////
////        String fileName = null;
////        try {
////            fileName = new String("附件.zip".getBytes("UTF-8"), "ISO-8859-1");
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        }
////
////        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
////
////        headers.setContentDispositionFormData("attachment", fileName);// 文件名称
////
////        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(byteOutPutStream.toByteArray(), headers, HttpStatus.CREATED);
////
////        return responseEntity;
////
////    }
//
//
//    @RequestMapping(value = "/")
//    public ResponseEntity<byte[]> downloadInterviewFile() throws Exception {
//
//        // ---------------------------压缩文件处理-------------------------------
//        ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
//
//        // 创建一个集合用于 压缩打包的参数
//        List<Map<String, String>> params = new ArrayList<>();
//        // 创建一个map集合
//        Map<String, String> fileMaps = new HashMap<>();
//        fileMaps.put("filePath", "D:/文件路径");
//        fileMaps.put("fileName", "HRM-Package.txt");
//
//        Map<String, String> fileMaps1 = new HashMap<>();
//        fileMaps1.put("filePath", "D:/文件路径1");
//        fileMaps1.put("fileName", "java.txt");
//        // 放入集合
//        params.add(fileMaps);
//        params.add(fileMaps1);
//
//        // 压缩文件
//        FileUtils.batchFileToZIP(params, byteOutPutStream);
//        // ---------------------------压缩文件处理-------------------------------
//        HttpHeaders headers = new HttpHeaders();
//
//        String fileName = null;
//        try {
//            fileName = new String("附件.zip".getBytes("UTF-8"), "ISO-8859-1");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//
//        headers.setContentDispositionFormData("attachment", fileName);// 文件名称
//
//        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
//            byteOutPutStream.toByteArray(), headers,
//            HttpStatus.CREATED);
//
//        return responseEntity;
//
//    }
//}
