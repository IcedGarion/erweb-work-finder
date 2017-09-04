package it.erweb.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BandoController
{
	//prova index
	@RequestMapping("/")
	public ModelAndView index()
	{
		ModelAndView model = new ModelAndView("listaBandi");
		model.addObject("msg", "hello world");

		return model;
	}
	
	
	@RequestMapping("/listaBandi")
	public String banList()
	{
		return "listaBandi";
	}
}
