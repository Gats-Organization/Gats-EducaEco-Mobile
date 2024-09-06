package com.mobile.educaeco.models;

public class Video {
    private long id;
    private String titulo;
    private int tema_id;
    private String video_url;

    public Video() {
    }

    public Video (String titulo, int tema_id, String video_url) {
        this.titulo = titulo;
        this.tema_id = tema_id;
        this.video_url = video_url;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getTema_id() {
        return tema_id;
    }

    public void setTema_id(int tema_id) {
        this.tema_id = tema_id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tema_id=" + tema_id +
                ", video_url='" + video_url + '\'' +
                '}';
    }
}
