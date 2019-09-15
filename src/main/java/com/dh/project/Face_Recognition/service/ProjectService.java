package com.dh.project.Face_Recognition.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProjectService {
    public  void trainData() throws IOException;
    public void loadData() throws IOException;
    public List<Map> testData();
}
