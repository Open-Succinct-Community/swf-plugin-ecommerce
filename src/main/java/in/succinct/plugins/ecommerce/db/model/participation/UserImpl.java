package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.core.date.DateUtils;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.plugins.calendar.db.model.WorkCalendar;

import java.sql.Date;

public class UserImpl extends ModelImpl<User> {
    public UserImpl(User user){
        super(user);
    }
    public UserImpl(){
        super();
    }
    public Date getWorkDate() {
        long date = DateUtils.getStartOfDay(System.currentTimeMillis()) - 24L * 60L * 60L * 1000L;

        User user  = getProxy();
        if (user.getAsset() != null){
            WorkCalendar calendar = user.getAsset().getWorkCalendar();
            if (calendar != null){
                Date nextDate = new Date(calendar.nextWorkingDay(new Date(date)).getTime());
                return nextDate;
            }
        }
        return new Date(date + 24L * 60L * 60L * 1000L);

    }

}
