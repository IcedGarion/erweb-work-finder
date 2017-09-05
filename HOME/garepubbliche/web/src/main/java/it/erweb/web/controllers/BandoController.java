package it.erweb.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BandoController
{
	@RequestMapping("/listaBandi")
	public ModelAndView banList()
	{
		//ritorna view con aggiunte; costruttore con nome della view
		ModelAndView model = new ModelAndView("listaBandi");
		model.addObject("msg", "hello world");

		return model;
	}
}
