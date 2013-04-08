package eu.ttbox.androgister.server.web.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import eu.ttbox.androgister.server.domain.model.Product
import org.springframework.stereotype.Controller

@Controller 
class AuthenticateControler {

  @RequestMapping(Array("/auth"))
  @ResponseBody
  def authentificate: String = {
    return "kokeoa"
  }
 
}