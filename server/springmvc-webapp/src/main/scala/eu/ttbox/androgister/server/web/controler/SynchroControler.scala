package eu.ttbox.androgister.server.web.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import eu.ttbox.androgister.server.domain.model.Product
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@RequestMapping(Array("/product"))
class SynchroControler {

  @RequestMapping(value = Array("/{entityId}"), method = Array(RequestMethod.GET), headers = Array("Accept=application/json"))
  @ResponseBody
  def getEntity(@PathVariable entityId: Long): Product = {
    val p = mockProduct(entityId)
    println("getEntity " + entityId + " : " + p)
    p
  }

  @RequestMapping(value = Array("/list"), method = Array(RequestMethod.GET))
  @ResponseBody
  def findEntities(@RequestParam(value = "s", defaultValue = "0") start: Int, @RequestParam(value = "p", defaultValue = "10") pageSize: Int): List[Product] = {
    //   for (i <- 0 to 2) mockProduct(i)
    List(mockProduct(1), mockProduct(2), mockProduct(3), mockProduct(4), mockProduct(5), mockProduct(6), mockProduct(7))
  }

  @RequestMapping(value = Array("/init"), method = Array(RequestMethod.GET))
  @ResponseBody
  def init(): List[Product] = {
    //   for (i <- 0 to 2) mockProduct(i)
    List(mockProduct(1), mockProduct(2), mockProduct(3), mockProduct(4), mockProduct(5), mockProduct(6), mockProduct(7))
  }

  def mockProduct(id: Long): Product = {
    new Product(id, "Name " + id)
  }

}