package ru.marksblog;

public class Header {

    private String filename;
    private Long filesize;
    private String header;
    private String nickname;


    public Header(String filename,Long filesize){
        this.filename=filename;
        this.filesize=filesize;
    }

    public String getHeader(){
        StringBuilder builder=new StringBuilder();
        builder.append(filesize);
        builder.append("#");
        builder.append(filename);
        builder.append("#");
        builder.append(nickname);
        header=builder.toString();
        return header;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFilename(String filename){
        this.filename=filename;
    }

    public void setFilesize(Long filesize){
        this.filesize=filesize;
    }
}
