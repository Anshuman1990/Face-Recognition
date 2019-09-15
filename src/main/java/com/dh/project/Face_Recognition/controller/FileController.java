package com.dh.project.Face_Recognition.controller;


import com.dh.project.Face_Recognition.service.FileStorageService;
import com.dh.project.Face_Recognition.utils.BuildResponse;
import com.dh.project.Face_Recognition.utils.UploadFileResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@RestController
@RequestMapping("/AI6")
@EnableAutoConfiguration
@Api(value = "AI6 File Handling", description = "File Controller for the application")
public class FileController {

    @Autowired
    private BuildResponse response;

    @Autowired
    private FileStorageService fileStorageService;



    @ApiOperation(value = "Upload File", notes = "REST api for uploading file", produces = "application/json", httpMethod = "POST")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST, produces = "application/json")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
//        LinkedHashSet<String> fileNames =  onlineMetric.extractFeature(fileName);
        Map<String,Object> response = new HashMap<>();
        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize(),response);
    }

//    @PostMapping("/uploadMultipleFiles")
//    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
//        return Arrays.asList(files)
//                .stream()
//                .map(file -> {
//                    try {
//                        return uploadFile(file);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                })
//                .collect(Collectors.toList());
//    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
