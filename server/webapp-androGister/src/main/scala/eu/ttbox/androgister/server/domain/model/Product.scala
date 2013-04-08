package eu.ttbox.androgister.server.domain.model

class Product(valId: Long, valName: String) {

  def id = valId
  def name = valName

 override  def toString: String =
    "Product " + id + " , name =" + name
    
}