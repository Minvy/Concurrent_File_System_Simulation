import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Client implements Runnable {
    private MyFileServer server;
    private boolean operation;
    String thread;

    public Client(MyFileServer server, boolean operation, String thread){
        this.server = server;
        this.operation = operation;
        this.thread = thread;
    }

    /**
     * Returns key of random item from the server
     * @return String: Item key
     */
    public String pickFile(){
        if(this.server.availableFiles().size() < 1){
            return null;
        }
        int rndNum = ThreadLocalRandom.current().nextInt(0, this.server.availableFiles().size());
        HashSet<String> files = new HashSet<>(this.server.availableFiles());
        return (String) files.toArray()[rndNum];
    }

    @Override
    public void run() {
        //True for Read, False for ReadWrite operation
        boolean operation = ThreadLocalRandom.current().nextBoolean();
        String fileToAccess = pickFile();
        Optional<File> optionalFile;

        if (this.operation){
            optionalFile = server.open(fileToAccess,Mode.READABLE);
            File file = optionalFile.get();
            server.close(file);

        }else{
            optionalFile = server.open(fileToAccess,Mode.READWRITEABLE);
            File file = optionalFile.get();
            file.write(thread);
            server.close(file);
        }
    }
}
