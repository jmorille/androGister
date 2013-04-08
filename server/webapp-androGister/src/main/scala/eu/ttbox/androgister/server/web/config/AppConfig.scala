package eu.ttbox.androgister.server.web.config

import eu.ttbox.androgister.server.web.controller.IndexController
import org.springframework.context.annotation._
import org.fusesource.scalate.spring.view.ScalateViewResolver 
import eu.ttbox.androgister.server.web.controller.SynchroControler

class AppConfig {

  @Bean
  def indexController = new IndexController

   @Bean
  def productController = new SynchroControler
  
  @Bean
  def viewResolver = new ScalateViewResolver
}