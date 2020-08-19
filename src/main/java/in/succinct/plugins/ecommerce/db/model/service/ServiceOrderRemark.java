package in.succinct.plugins.ecommerce.db.model.service;

import com.venky.swf.db.model.Model;

public interface ServiceOrderRemark extends Model {
    public long getServiceOrderId();
    public void setServiceOrderId(long id);
    public ServiceOrder getServiceOrder();

    public String getRemarks();
    public void setRemarks(String remarks);
}
