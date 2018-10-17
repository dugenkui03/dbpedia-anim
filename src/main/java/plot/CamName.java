package plot;

public class CamName {
    public CamName(String cam,int start,int end,int sall,int eall){
        camName=cam;
        startFrame=start;
        endFrame=end;
        startinall=sall;
        endinall=eall;
    }
    private String camName;
    private int startFrame;
    private int endFrame;
    private int startinall;
    private int endinall;
    public int getStartinall() {
        return startinall;
    }
    public void setStartinall(int startinall) {
        this.startinall = startinall;
    }
    public int getEndinall() {
        return endinall;
    }
    public void setEndinall(int endinall) {
        this.endinall = endinall;
    }
    public String getCamName() {
        return camName;
    }
    public void setCamName(String camName) {
        this.camName = camName;
    }
    public int getStartFrame() {
        return startFrame;
    }
    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }
    public int getEndFrame() {
        return endFrame;
    }
    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

}

