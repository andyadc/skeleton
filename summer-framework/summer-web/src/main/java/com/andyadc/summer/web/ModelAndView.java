package com.andyadc.summer.web;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private final String view;
    int status;
    private Map<String, Object> model;

    public ModelAndView(String viewName) {
        this(viewName, HttpServletResponse.SC_OK, null);
    }

    public ModelAndView(String viewName, Map<String, Object> model) {
        this(viewName, HttpServletResponse.SC_OK, model);
    }

    public ModelAndView(String viewName, int status) {
        this(viewName, status, null);
    }

    public ModelAndView(String viewName, int status, Map<String, Object> model) {
        this.view = viewName;
        this.status = status;
        if (model != null) {
            addModel(model);
        }
    }

    public ModelAndView(String viewName, String modelName, Object modelObject) {
        this(viewName, HttpServletResponse.SC_OK, null);
        addModel(modelName, modelObject);
    }

    public String getViewName() {
        return this.view;
    }

    public void addModel(Map<String, Object> map) {
        if (this.model == null) {
            this.model = new HashMap<>();
        }
        this.model.putAll(map);
    }

    public void addModel(String key, Object value) {
        if (this.model == null) {
            this.model = new HashMap<>();
        }
        this.model.put(key, value);
    }

    public Map<String, Object> getModel() {
        if (this.model == null) {
            this.model = new HashMap<>();
        }
        return this.model;
    }

    public int getStatus() {
        return this.status;
    }

}
