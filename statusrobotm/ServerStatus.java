package statusrobotm;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class ServerStatus {
    private static final int INVALID_HTTP_RESPONSE = -1;
    private static final int MALFORMED_URL = -999;
    private static final int STATUS_CHECK_TIMEOUT = -998;
    private static final int UNEXPECTED_IO_EXCEPTION = -997;

    private static Throwable lastException=null;

    private static void setLastException(Throwable t) {
        lastException=t;
    }

    public static String getLastStatusDetails() {
        StringBuilder statusDetails= new StringBuilder();

        if (lastException!=null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            lastException.printStackTrace(pw);

            String lastExceptionStackTrace = sw.toString();
            statusDetails.append(lastExceptionStackTrace);
        }

        // You may append other useful information to the details...
        return statusDetails.toString();
    }

    public static int getResponseCode(String urlString) {
        int ret;

        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            ret= huc.getResponseCode();
        } catch(MalformedURLException mue) {
            setLastException(mue);
            ret=MALFORMED_URL;
        } catch (SocketTimeoutException ste) {
            setLastException(ste);
            ret=STATUS_CHECK_TIMEOUT;
        } catch(IOException ioe) {
            setLastException(ioe);
            ret=UNEXPECTED_IO_EXCEPTION ;
        }

        return ret;
    }
}
