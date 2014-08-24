package javapad.server.interfaces;

public interface INotifier
{
    enum Type
    {
        CONSOLE_MESSAGE, STACK_TRACE, ERROR_EXCEPTION, ERROR_MESSAGE;
    }

    void sendToConsole(String message);

    void sendStackTrace(StackTraceElement[] stackTrace);

    void sendError(Exception e);

    void sendError(String errorMessage);
}
