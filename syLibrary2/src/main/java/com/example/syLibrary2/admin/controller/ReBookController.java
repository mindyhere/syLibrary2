package com.example.syLibrary2.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.syLibrary2.admin.model.dao.ReBookDAO;

@Controller
@RequestMapping("/admin/*")
public class ReBookController {
	
	@Autowired
	ReBookDAO dao;
	
	
}
