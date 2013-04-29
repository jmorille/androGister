package eu.ttbox.androgister.domain;


//KEEP INCLUDES - put your custom includes here
import eu.ttbox.androgister.domain.ref.OrderStatusEnum;
//KEEP INCLUDES END


public class Order {

 
    
    
 // KEEP METHODS - put your custom methods here 
    public eu.ttbox.androgister.domain.ref.OrderStatusEnum getStatus() {
       int statusId = getStatusId();
       return eu.ttbox.androgister.domain.ref.OrderStatusEnum.getEnumFromKey(statusId);
    }

    public void setStatus(eu.ttbox.androgister.domain.ref.OrderStatusEnum status) {
        setStatusId(status.getKey()); 
     }

    public Order withStatus(eu.ttbox.androgister.domain.ref.OrderStatusEnum status) {
        setStatus(status); 
        return this;
     }
    
 // KEEP METHODS END
    
    
    
}
