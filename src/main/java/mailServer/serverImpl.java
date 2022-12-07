package mailServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class serverImpl extends Thread implements Server {
 private final int serverPort;
 private final int serverThread;
 private final int serverTime;
 private final ExecutorService threadPool;
 private final ServerSocket serverSocket;

 public serverImpl(){
   serverPort = Integer.parseInt();
   serverTime= Integer.parseInt();
   serverThread = Integer.parseInt();

   threadPool = Executors.newFixedThreadPool(serverThread);

 }

 @Override
 public void start(){
 System.out.println("starting server on port"+serverPort+"...");
 super.start();
 }



  @Override
  public void close(){
  try{
   serverSocket.close();
   threadPool.shutdown();
   int wait = Math.min(serverThread*2,500);

   if(!threadPool.awaitTermination(wait, TimeUnit.SECONDS)){
    threadPool.shutdown();
   }
  } catch (InterruptedException | IOException e){
    e.printStackTrace();
    System.out.println("SocketServer exception on closing"+e.getLocalizedMessage());
  }
 System.out.println("server stopped");
  }


  public void interrupt(){
  super.interrupt();
  if(!serverSocket.isClosed())
   close();
  }
}
