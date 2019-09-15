package com.dh.project.Face_Recognition.controller;

import com.dh.project.Face_Recognition.service.OpencvService;
import com.dh.project.Face_Recognition.service.OpencvServiceImpl;
import com.dh.project.Face_Recognition.service.ProjectService;
import com.dh.project.Face_Recognition.utils.BuildResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/face")
@EnableAutoConfiguration
@Api(value = "Face API", description = "All the Face Recognition Stuffs!!!")
public class MainController {


    @Autowired
    private BuildResponse response;

    @Autowired
    private ProjectService projectService;

    @ApiOperation(value = "Train Data", notes = "REST api for Image Dataset", httpMethod = "POST", produces = "application/json")
    @RequestMapping(value = "/trainData", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity trainData() throws IOException, InterruptedException {
        Map response = new HashMap();
        try{
        this.projectService.trainData();
            response.put("Status", "Training Done");
        }
        catch (Exception e){
            response.put("Status","Failed");
            response.put("Reason",e.getMessage());
        }
        return this.response.createResponse(response, HttpStatus.OK);
    }


    @ApiOperation(value = "Test Data", notes = "REST api for Image Dataset", httpMethod = "POST", produces = "application/json")
    @RequestMapping(value = "/testData", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity testData() throws IOException, InterruptedException {
        List response = this.projectService.testData();

        return this.response.createResponse(response, HttpStatus.OK);
    }

//    @PostConstruct
//    public void loadTrainingData() throws IOException {
//        this.projectService.loadData();
//    }
}
