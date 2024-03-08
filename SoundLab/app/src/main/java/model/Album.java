package model;

public class Album {

    private int id;
    private String nome;
    private int anno;

    public Album(int id, String nome, int anno) {
        this.id = id;
        this.nome = nome;
        this.anno = anno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }
}
