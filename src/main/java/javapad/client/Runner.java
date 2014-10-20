package javapad.client;

import javapad.client.controllers.JavaPadController;
import javapad.client.services.FileService;
import javapad.client.services.JavaPadNetworkService;
import javapad.client.services.StandardNetworkService;
import javapad.client.views.JavaPadView;

/**
 * Created by conor on 20/10/2014.
 */
public class Runner {

    public static void main(String[] args) {
        JavaPadView view = new JavaPadView();
        JavaPadController controller = new JavaPadController(view, new FileService(), new StandardNetworkService());
    }
}
