package mailServer.Controller;

import mailServer.ServerMain;

import java.net.Socket;

public class ServerHandler implements Runnable{
  private ServerMain serverMain;
  private Socket incoming;
  private FileManager fileManager;

}
