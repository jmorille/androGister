package eu.ttbox.androgister.server.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sample.SomeClass

@Controller
class IndexController {
 
  @RequestMapping(Array("/layout"))
  def layout = "/index.scaml"

  @RequestMapping(Array("/viewXX"))
  def view: ModelAndView = {
    val mav = new ModelAndView
    mav.addObject("it", new SomeClass)
    return mav
  }

  @RequestMapping(Array("/", "/render"))
  def render = "layout:/index.scaml"
    
}