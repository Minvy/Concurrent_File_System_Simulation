import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class MyFileServer implements FileServer{
    private HashMap<String, FileFrame> fileFrames;
    private HashMap<String, Object> syncLock;
    private HashMap<String, Queue<String>> readQueue;

    public MyFileServer(){
        fileFrames = new HashMap<>();
        syncLock = new HashMap<>();
        readQueue = new HashMap<>();
    }

    @Override
    public void create(String filename, String content) {
        FileFrame fileFrame = new FileFrame(content, Mode.CLOSED);
        fileFrames.put(filename,fileFrame);
        syncLock.put(filename, new Object());
        readQueue.put(filename, new LinkedList<>());
    }

    @Override
    public Optional<File> open(String filename, Mode mode) {

        //If a file or the mode is not present then do nothing
        if (fileStatus(filename).equals(Mode.UNKNOWN) || mode.equals(Mode.UNKNOWN) || mode.equals(Mode.CLOSED)) {
            return Optional.empty();
        }
        FileFrame fileframe = fileFrames.get(filename);
        System.out.println("Server -> " + mode.name() + ", File -> " + fileframe.mode);

        if(operation(filename, mode).equals("wait")){
            synchronized (syncLock.get(filename)){
                try {
                    syncLock.get(filename).wait();
                    return Optional.of(read(filename,mode));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else{
            if(mode.equals(Mode.READABLE)){
                readQueue.get(filename).add("read");
            }
            return Optional.of(read(filename,mode));
        }
        return null;
    }

    @Override
    public void close(File file) {
            String filename = file.filename();

            if(fileFrames.get(filename).mode.equals(Mode.READWRITEABLE)){
                fileFrames.get(filename).content = file.read();
                fileFrames.get(filename).mode = Mode.CLOSED;
            }else if(fileFrames.get(filename).mode.equals(Mode.READABLE)){
                readQueue.get(filename).poll();
                if(readQueue.get(filename).isEmpty()){
                    fileFrames.get(filename).mode = Mode.CLOSED;
                }else{
                    fileFrames.get(filename).mode = Mode.READABLE;
                }
            }

            synchronized (syncLock.get(filename)){
                syncLock.get(filename).notify();
            }
    }

    @Override
    public Mode fileStatus(String filename) {
        Mode mode;
        if(fileFrames.containsKey(filename)){
            mode = fileFrames.get(filename).mode;
        }else{
            mode = Mode.UNKNOWN;
        }
        return mode;
    }

    @Override
    public Set<String> availableFiles() {
        Set<String> keys = fileFrames.keySet();
        return keys;
    }

    public String operation(String filename, Mode mode){
        Mode fileMode = fileFrames.get(filename).mode;
        if(fileMode.equals(Mode.READWRITEABLE) && mode.equals(Mode.READABLE)){
            return "wait";
        }else if(fileMode.equals(Mode.READWRITEABLE) && mode.equals(Mode.READWRITEABLE)){
            return "wait";
        }else if(fileMode.equals(Mode.READABLE) && mode.equals(Mode.READWRITEABLE)){
            return "wait";
        }else{
            return "proceed";
        }
    }

    private File read(String filename, Mode mode){
            try {
                fileFrames.get(filename).mode = mode;
                String content = fileFrames.get(filename).content;
                File file = new File(filename, content, mode);
                return file;
            }finally {

            }
    }
}
