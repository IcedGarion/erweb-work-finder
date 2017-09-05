package it.erweb.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UtenteController
{
	@RequestMapping("/register")
	public ModelAndView registerUser()
	{
		// ritorna view con aggiunte; costruttore con nome della view
		ModelAndView model = new ModelAndView("register");
		model.addObject("msg", "ok");

		return model;
	}

	@RequestMapping("/login")
	public ModelAndView loginUser()
	{
		// ritorna view con aggiunte; costruttore con nome della view
		ModelAndView model = new ModelAndView("login");
		model.addObject("msg", "ok");

		return model;
	}
}
