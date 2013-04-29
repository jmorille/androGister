package eu.ttbox.androgister.domain;


//KEEP INCLUDES - put your custom includes here
import eu.ttbox.androgister.domain.ref.OrderStatusEnum;
//KEEP INCLUDES END


public class Order {

 
    
    
 // KEEP METHODS - put your custom methods here 
    
    // Order Status
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

    // Payment Mode
    public eu.ttbox.androgister.domain.ref.OrderPaymentModeEnum getPaymentMode() {
        int modeId = getPaymentModeId();
        return eu.ttbox.androgister.domain.ref.OrderPaymentModeEnum.getEnumFromKey(modeId);
     }
    
    public void setPaymentMode(eu.ttbox.androgister.domain.ref.OrderPaymentModeEnum mode) {
        setPaymentModeId(mode.getKey()); 
     }
  
    public Order withPaymentMode(eu.ttbox.androgister.domain.ref.OrderPaymentModeEnum mode) {
        setPaymentMode(mode); 
        return this;
     }
  
 // KEEP METHODS END
    
    
    
}
