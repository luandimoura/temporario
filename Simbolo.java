
package br.ufmt.compiladores.lexico;


public class Simbolo {
    private String nome;
    private int tipo;
    private int end_rel;
    
    public Simbolo(int tipo, String nome, int end_rel){
        this.tipo= tipo;
        this.nome = nome;
        this.end_rel = end_rel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getEnd_rel() {
        return end_rel;
    }

    public void setEnd_rel(int end_rel) {
        this.end_rel = end_rel;
    }
    
    
    
}
