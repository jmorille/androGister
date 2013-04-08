package eu.ttbox.androgister.server.web.controler

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class SynchroControler {

  @RequestMapping(Array("/auth"))
  @ResponseBody
  def authentificate: String = {
    return "kokeoa"
  }

  @RequestMapping(value = Array("/list"), method = Array(RequestMethod.GET))
  @ResponseBody
  def productList(@RequestParam(value = "s", defaultValue = "0") start: Int, @RequestParam(value = "p", defaultValue = "10") pageSize: Int): String = {
    return "kokeoa"
  }

}