package jian.com.ximon.ad.file;

/**
 * Created by ying on 17-11-9.
 */

public class LocalFileInfo {
    private Long id;

    private String  fileName;

    public LocalFileInfo(Long id, String fileName, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String filePath;
}
