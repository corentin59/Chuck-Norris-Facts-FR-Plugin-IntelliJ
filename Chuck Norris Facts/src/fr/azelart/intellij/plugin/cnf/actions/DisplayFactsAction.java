package fr.azelart.intellij.plugin.cnf.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Corentin Azelart
 * Date: 30/12/12
 * Time: 15:56
 */
public class DisplayFactsAction extends AnAction {

    /**
     * Facts.
     */
    private JSONArray facts;

    /**
     * User performed action on check norris icon.
     * @param actionEvent is the user action event.
     */
    @Override
    public void actionPerformed(final AnActionEvent actionEvent) {

        // No facts... we load...
        if(facts == null) {
            try {
                pushMessage("Please wait...", actionEvent);
                this.reloadFacts();
            } catch (final IOException e) {
                pushMessage("Unable to load facts... please check your network connection or try again later...", actionEvent);
            } catch (final JSONException e) {
                pushMessage("Unable to load facts... try again later...", actionEvent);
            }
        }

        // Set fact...
        final int max = facts.length() - 1;
        final int number = (int)(Math.random() * (max-0)) + 0;

        try {
            final JSONObject fact = (JSONObject) facts.get(number);
            pushMessage(StringEscapeUtils.unescapeHtml(fact.getString("fact")), actionEvent);
        } catch (final JSONException je) {
            pushMessage("No fact to display... try again later...", actionEvent);
        }
    }

    /**
     * Reload facts.
     * @throws IOException if error with http request.
     * @throws JSONException if we can't decode JSON
     */
    private void reloadFacts() throws IOException, JSONException {
        // Get facts...
        final HttpClient client = new HttpClient();
        final GetMethod getMethod = new GetMethod("http://www.chucknorrisfacts.fr/api/get?data=tri:alea:nb:50");

        final int statusCode = client.executeMethod(getMethod);

        // Good response.
        if(statusCode == HttpStatus.SC_OK) {
            final String factsTxt = getMethod.getResponseBodyAsString();
            facts = new JSONArray(factsTxt);
        } else {
            throw new IOException("Server response != 200");
        }
    }

    /**
     * Push message on status bar.
     * @param message is the message to put on status bar.
     * @param actionEvent is the user action event.
     */
    private void pushMessage(final String message, final AnActionEvent actionEvent) {
        // Get IDE status bar...
        final StatusBar statusBar = WindowManager.getInstance().getStatusBar(DataKeys.PROJECT.getData(actionEvent.getDataContext()));

        // Put message
        statusBar.setInfo(message);
    }
}

