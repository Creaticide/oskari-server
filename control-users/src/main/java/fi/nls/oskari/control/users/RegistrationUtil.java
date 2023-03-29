package fi.nls.oskari.control.users;

import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.util.PropertyUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by SMAKINEN on 1.9.2016.
 */
public class RegistrationUtil {

    // From: https://owasp.org/www-community/OWASP_Validation_Regex_Repository
    private static final String EMAIL_REGEXP = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEXP);
    public static final String getServerAddress(ActionParameters params) {
        final String domain = PropertyUtil.get("oskari.domain", null);
        if(domain != null) {
            return domain;
        }
        final HttpServletRequest request = params.getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Create timestamp for 2 days as expirytime.
     * @return
     */
    public static Timestamp createExpiryTime(){
        Calendar calender = Calendar.getInstance();
        Timestamp currentTime = new java.sql.Timestamp(calender.getTime().getTime());
        calender.setTime(currentTime);
        int expireDays = PropertyUtil.getOptional("oskari.email.link.expirytime", 2);
        calender.add(Calendar.DAY_OF_MONTH, expireDays);
        Timestamp expiryTime = new java.sql.Timestamp(calender.getTime().getTime());
        return expiryTime;
    }

    public static boolean isEnabled() {
        return PropertyUtil.getOptional("allow.registration", false);
    }
}
