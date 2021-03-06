package javapad.client;

import javapad.client.controllers.JavaPadController;
import javapad.client.services.FileService;
import javapad.client.services.SSLNetworkResponseListener;
import javapad.client.services.SSLNetworkService;
import javapad.client.views.ChangeFontDialog;
import javapad.client.views.ChangeSyntaxModeDialog;
import javapad.client.views.ConnectionDialog;
import javapad.client.views.JavaPadView;

import javax.net.ssl.SSLSocketFactory;

import static javapad.client.services.SSLNetworkService.*;

/**
 * Created by conor on 20/10/2014.
 */
public class Runner {

    public static void main(String[] args) {
        JavaPadView view = new JavaPadView();
        JavaPadController controller = new JavaPadController(view, new FileService(),
                new SSLNetworkService(((SSLSocketFactory) SSLSocketFactory.getDefault()), new SSLNetworkResponseListener()),
                new ChangeFontDialog(view), new ChangeSyntaxModeDialog(view), new ConnectionDialog(view));
    }
}
