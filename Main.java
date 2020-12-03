import java.util.Optional;
import java.util.Set;

public class Main {
    public static void main(String args[]){
        MyFileServer server = new MyFileServer();
        server.create("aa","aas");

        Thread t1 = new Thread(new Client(server,true, "t1"));
        Thread t2 = new Thread(new Client(server, true, "t2"));
        Thread t3 = new Thread(new Client(server, false, "t3"));
        Thread t4 = new Thread(new Client(server, false, "t4"));
        Thread t5 = new Thread(new Client(server, true, "t5"));

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        long start = System.currentTimeMillis();
        long end = start + 1*500; // 60 seconds * 1000 ms/sec
        while (System.currentTimeMillis() < end)
        {
            // run
        }

        System.out.println(server.open("aa",Mode.READABLE).get().read());

        for(int i = 0; i < 5; i++){
            //Client cl = new Client(server);
            //cl.run();
        }
    }
}
