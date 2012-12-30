package fr.azelart.intellij.plugin.cnf.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
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
    private List<String> facts;

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
            } catch (IOException e) {
                pushMessage("Unable to load facts... please check your network connection or try again later...", actionEvent);
            }
        }

        // Set fact...
        final int max = facts.size() - 1;
        final int number = (int)(Math.random() * (max-0)) + 0;

        pushMessage(facts.get(number), actionEvent);
    }

    /**
     * Reload facts.
     * @throws java.io.IOException if error with http request.
     */
    private void reloadFacts() throws IOException{
        // Get facts...
        final HttpClient client = new HttpClient();
        final GetMethod getMethod = new GetMethod("http://www.chucknorrisfacts.fr/fortunes/fortunes.txt");

        final int statusCode = client.executeMethod(getMethod);

        // Good response.
        if(statusCode == HttpStatus.SC_OK) {
            final String factsTxt = getMethod.getResponseBodyAsString();

            // Parsing facts...
            if(factsTxt!=null) {
                final String[] factsArray = factsTxt.split("%");
                if(factsArray.length>=1) {
                    facts = new ArrayList<String>(Arrays.asList(factsArray));
                }
            }
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

