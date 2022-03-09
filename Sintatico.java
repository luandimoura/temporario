
package br.ufmt.compiladores.lexico;

//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class Sintatico {
    private LexScanner scan;
    private String simbolo;
    private int tipo;
    private Map<String, Simbolo> tabelaSimbolos = new HashMap<>(); //Tabela de simbolos principal
    private Map<String, Simbolo> tabelaSimbolosP = new HashMap<>(); //Tabela de simbolos do procedimento
    private int verificador = 0; //Verificador para ver se as variáveis são do principal ou do procedimento
    
    private Stack<String> C = new Stack<String>();
    private Stack<Float> D = new Stack<>();
    private int var = -1;
    private int s;
    private int i;
    //private int nulo = 0;
    
    public Sintatico(String arq){
        scan = new LexScanner(arq);
        
    }
    
    public void analise(){
        obtemToken();
        programa();
        if(simbolo.equals("")){
            System.out.println("Executado com sucesso!");
        }else{
            throw new RuntimeException("Erro sintatico: era esperado um fim de cadeia");
        }
    }
    
    private void obtemToken(){
        Token token = scan.nextToken();
        simbolo ="";
        if(token != null){
            simbolo = token.getTermo();
            System.out.println(simbolo);
            tipo = token.getTipo();
        }
        
    }
    
    private void programa(){
        if(simbolo.equals("program")){
            obtemToken();
            C.push("INPP");
            i = 0;
            //s = -1;
            if (tipo == Token.IDENT){
                obtemToken();
                
                
                
                corpo();
                if(simbolo.equals(".")){
                    obtemToken();
                    
                    //C.add("PARA");
                }else{
                  throw new RuntimeException("Erro sintatico: era esperado um ponto (.)");  
                }
            }else{
                throw new RuntimeException("Erro sintatico: era esperado um identificador");
            }
            
        }else{
            throw new RuntimeException("Erro sintatico: era esperado 'program'");
        }
    }
    
    private void corpo(){
        dc();
        if(simbolo.equals("begin")){
            obtemToken();
            comandos();
            if(simbolo.equals("end")){
                C.push("PARA");
                System.out.println(C);
                //System.out.println(D);
                obtemToken();
            }else{
              throw new RuntimeException("Erro sintatico: era esperado 'end'");  
            }
        }else{
           throw new RuntimeException("Erro sintatico: era esperado 'begin'"); 
        }
    }
    
    private void dc(){
        if(simbolo.equals("real") || simbolo.equals("integer")){
            dc_v();
            mais_dc();
        }else if(simbolo.equals("procedure")){
            dc_p();
        }
        
    }
    
    private void mais_dc(){
        if(simbolo.equals(";")){
            obtemToken();
            dc();
        }
    }
    
    private void dc_v(){ //No profeta esta diferente
        tipo_var();
        if(simbolo.equals(":")){
            obtemToken();
            variaveis();
        }else{
            throw new RuntimeException("Erro sintatico: era esperado ':'");
        }
    }
    
    private void tipo_var(){
        if(simbolo.equals("real") || simbolo.equals("integer")){
            obtemToken();
        }else{
            throw new RuntimeException("Erro sintatico: era esperado 'real' ou 'integer'");
        }
    }
    
    private void variaveis(){ //No profeta esta diferente
        if(tipo != Token.IDENT){
            throw new RuntimeException("Erro sintatico: era esperado um identificador");
        }
        if(verificador == 0){
            if(tabelaSimbolos.containsKey(simbolo)){
                throw new RuntimeException("Erro semantico: identificador ja encontrado "+simbolo);
            }else{
                var += 1;
                C.push("ALME "+var);
                i += 1;
                tabelaSimbolos.put(simbolo, new Simbolo(this.tipo, simbolo, var));
                
            }
        }else if(verificador == 1){
            if(tabelaSimbolosP.containsKey(simbolo)){
                throw new RuntimeException("Erro semantico: identificador ja encontrado "+simbolo);
            }else{
                var += 1;
                C.push("ALME "+var);
                i += 1;
                tabelaSimbolosP.put(simbolo, new Simbolo(this.tipo, simbolo, var));
            }
        }
        
        obtemToken();
        mais_var();
    }
    
    private void mais_var(){ //No profeta esta diferente
        if(simbolo.equals(",")){
           obtemToken();
           variaveis();
        }

    }
    
    private void dc_p(){
        if(simbolo.equals("procedure")){
            obtemToken();
            verificador = 1;
            if(tipo == Token.IDENT){
                tabelaSimbolosP.put(simbolo, new Simbolo(this.tipo, simbolo, -5));
                tabelaSimbolos.put(simbolo, new Simbolo(this.tipo, simbolo, -5));
                obtemToken();
                parametros();
                corpo_p();
                
                
            }else{
                throw new RuntimeException("Erro sintatico: era esperado um identificador");
            }
        }else{
            throw new RuntimeException("Erro sintatico: era esperado 'procedure'");
        }
    }
    
    private void parametros(){
        if(simbolo.equals("(")){
            obtemToken();
            lista_par();
            if(simbolo.equals(")")){
                obtemToken();
            }else{
              throw new RuntimeException("Erro sintatico: era esperado ')'");  
            }
        }
    }
    
    private void lista_par(){
        tipo_var();
        if(simbolo.equals(":")){
            obtemToken();
            variaveis();
            mais_par();
        }else{
            throw new RuntimeException("Erro sintatico: era esperado ':'");
        }
    }
    
    private void mais_par(){
        if(simbolo.equals(";")){
            obtemToken();
            lista_par();   
        }
    }
    
    private void corpo_p(){
        dc_loc();
        if(simbolo.equals("begin")){
            obtemToken();
            comandos();
            if(simbolo.equals("end")){
                obtemToken();
                verificador = 0;
            }else{
                throw new RuntimeException("Erro sintatico: era esperado 'end'");
            }
        }else{
           throw new RuntimeException("Erro sintatico: era esperado 'begin'"); 
        }
    }
    
    private void dc_loc(){
        if(simbolo.equals("real") || simbolo.equals("integer")){
            dc_v();
            mais_dcloc();
        }
    }
    
    private void mais_dcloc(){
        if(simbolo.equals(";")){
            obtemToken();
            dc_loc();
        }
    }
    
    private void lista_arg(){
        if(simbolo.equals("(")){
            obtemToken();
            argumentos();
            if(simbolo.equals(")")){
                obtemToken();
            }else{
                throw new RuntimeException("Erro sintatico: era esperado ')'");
            }
        }
    }
    
    private void argumentos(){
        if(tipo == Token.IDENT){
            if(verificador == 0){
                if(tabelaSimbolos.containsKey(simbolo)){
                    obtemToken();
                    mais_ident();
                }else{
                    throw new RuntimeException("Erro semantico: identificador nao declarado");
                }
            }
            else if (verificador == 1){
                if(tabelaSimbolos.containsKey(simbolo)){
                    obtemToken();
                    mais_ident();
                }else{
                    throw new RuntimeException("Erro semantico: identificador nao declarado");
                }
            }
        }else{
           throw new RuntimeException("Erro sintatico: era esperado um identificador"); 
        }
    }
    
    private void mais_ident(){
        if(simbolo.equals(",")){
            obtemToken();
            argumentos();
        }
    }
    
    private void comandos(){
        comando();
        mais_comandos();
    }
    
    private void mais_comandos(){
        if(simbolo.equals(";")){
            obtemToken();
            comandos();
        }
        
    }
    
    private void comando(){
        if(simbolo.equals("read")){
            C.push("LEIT");
            i += 1;
            obtemToken();
            if(simbolo.equals("(")){
                obtemToken();
                if(tipo == Token.IDENT && verificador == 0){
                    if(tabelaSimbolos.containsKey(simbolo)){
                        C.push("ARMZ "+tabelaSimbolos.get(simbolo).getEnd_rel());
                        i += 1;
                        obtemToken();
                        if(simbolo.equals(")")){
                            obtemToken();
                        }else{
                            throw new RuntimeException("Erro sintatico: era esperado ')'");
                        }
                    }else{
                       throw new RuntimeException("Erro semantico: identificador nao declarado"); 
                    }
                }else if(tipo == Token.IDENT && verificador == 1){
                    if(tabelaSimbolosP.containsKey(simbolo)){
                        C.push("ARMZ "+tabelaSimbolosP.get(simbolo).getEnd_rel());
                        i += 1;
                        obtemToken();
                        if(simbolo.equals(")")){
                            obtemToken();
                        }else{
                            throw new RuntimeException("Erro sintatico: era esperado ')'");
                        }
                    }else{
                       throw new RuntimeException("Erro semantico: identificador nao declarado"); 
                    }
                }else{
                    throw new RuntimeException("Erro sintatico: era esperado um identificador");
                }

            }else{
                throw new RuntimeException("Erro sintatico: era esperado '('");
            }
        }else if(simbolo.equals("write")){
            obtemToken();
            if(simbolo.equals("(")){
                obtemToken();
                if(tipo == Token.IDENT && verificador == 0){
                    if(tabelaSimbolos.containsKey(simbolo)){
                        C.push("CRVL "+tabelaSimbolos.get(simbolo).getEnd_rel());
                        i += 1;
                        C.push("IMPR");
                        i += 1;
                        obtemToken();
                        if(simbolo.equals(")")){
                            obtemToken();
                        }else{
                            throw new RuntimeException("Erro sintatico: era esperado ')'");
                        }
                    }else{
                        throw new RuntimeException("Erro semantico: identificador nao declarado");
                    }
                }else if(tipo == Token.IDENT && verificador == 1){
                    if(tabelaSimbolosP.containsKey(simbolo)){
                        C.push("CRVL "+tabelaSimbolosP.get(simbolo).getEnd_rel());
                        i += 1;
                        C.push("IMPR");
                        i += 1;
                        obtemToken();
                        if(simbolo.equals(")")){
                            obtemToken();
                        }else{
                            throw new RuntimeException("Erro sintatico: era esperado ')'");
                        }
                    }else{
                        throw new RuntimeException("Erro semantico: identificador nao declarado");
                    }  
                }else{
                    throw new RuntimeException("Erro sintatico: era esperado um identificador");
                }

            }else{
                throw new RuntimeException("Erro sintatico: era esperado '('");
            }
        }else if(tipo == Token.IDENT){
            if(verificador == 0){
                if(tabelaSimbolos.containsKey(simbolo)){
                    C.push("ARMZ "+tabelaSimbolos.get(simbolo).getEnd_rel());
                    i += 1;
                    obtemToken();
                    restoIdent();
                }else{
                    throw new RuntimeException("Erro semantico: identificador nao declarado");
                }
            }else if(verificador == 1){
                if(tabelaSimbolosP.containsKey(simbolo)){
                    C.push("ARMZ "+tabelaSimbolosP.get(simbolo).getEnd_rel());
                    i += 1;
                    obtemToken();
                    restoIdent();
                }else{
                    throw new RuntimeException("Erro semantico: identificador nao declarado");
                }
            }
        }else if(simbolo.equals("if")){
            obtemToken();
            condicao();
            if(simbolo.equals("then")){
                obtemToken();
                
                /*C.add("DSVF");
                int linDSVF = C.size()-1;
                */
                comandos();
                
                /*
                C.add("DSVI");
                int linDSVI = C.size()-1;
                int linElse = C.size();
                */
                
                pfalsa();
                
                /*
                if(C.size() == linElse){
                    C.remove(C.size()-1);
                */    
                
                
                
                
                if(simbolo.equals("$")){
                    obtemToken();
                }else{
                    throw new RuntimeException("Erro sintatico: era esperado '$'");
                }
                
            }else{
                throw new RuntimeException("Erro sintatico: era esperado 'then'");  
            }
        }else if(simbolo.equals("while")){
            obtemToken();
            
            //int linhaCond = C.size();
            
            condicao();
            
            //C.add("DSVF");
            //int linDSVF = C.size() - 1;
            
            
            if(simbolo.equals("do")){
                obtemToken();
                comandos();
                
                //C.add("DSVI ");
                
                //int linAposWhile = C.size();
                
                if(simbolo.equals("$")){
                    obtemToken();
                }else{
                  throw new RuntimeException("Erro sintatico: era esperado '$'");  
                }
            }else{
                throw new RuntimeException("Erro sintatico: era esperado 'do'");
            }
            
        }else{
            throw new RuntimeException("Erro sintatico: era esperado 'read' ou"
                    + "'write' ou 'if' ou 'while' ou um identificador valido");
        }
    
    }
    
    private void restoIdent(){
        if(simbolo.equals(":=")){
            obtemToken();
            expressao();
        }else{
            lista_arg();
        }
           
    }
    
    private void condicao(){
        expressao();
        relacao();
        expressao();
    }
    
    private void relacao(){
        switch(simbolo){
            case "=":
                C.push("CPIG");
                i += 1;
                obtemToken();
                break;
            case "<>":
                C.push("CDES");
                i += 1;
                obtemToken();
                break;
            case ">=":
                C.push("CMAI");
                i += 1;
                obtemToken();
                break;
            case "<=":
                C.push("CPMI");
                i += 1;
                obtemToken();
                break;
            case ">":
                C.push("CPMA");
                i += 1;
                obtemToken();
                break;
            case "<":
                C.push("CPME");
                i += 1;
                obtemToken();
                break;
            default:
                throw new RuntimeException("Erro sintatico: era esperado '=' ou '<>' ou '>="
                        + "ou '<=' ou '>' ou '<'");
        }
    }
    
    private void expressao(){
        termo();
        outros_termos();
    }
    
    private void termo(){
        op_un();
        fator();
        mais_fatores();
    }
    
    private void op_un(){
        if(simbolo.equals("-")){
            obtemToken();
        }
        
    }
    
    private void fator(){
        if(tipo == Token.IDENT){
            if(verificador == 0){
                if(tabelaSimbolos.containsKey(simbolo)){
                    C.push("CRVL "+tabelaSimbolosP.get(simbolo).getEnd_rel());
                    i += 1;
                    
                    obtemToken();
                }else{
                    throw new RuntimeException("Erro semantico: identificador nao declarado");
                } 
            }else if(verificador == 1){
               if(tabelaSimbolosP.containsKey(simbolo)){
                    C.push("CRVL "+tabelaSimbolosP.get(simbolo).getEnd_rel());
                    i += 1;
                    obtemToken();
                }else{
                    throw new RuntimeException("Erro semantico: identificador nao declarado");
                } 
            }
        }else if(tipo == Token.NUMERO_INTEIRO){
            C.push("CRCT "+simbolo);
            i += 1;
            //D.push(Float.parseFloat(simbolo));
            //s += 1;
            obtemToken();
        }else if(tipo == Token.NUMERO_REAL){
            C.push("CRCT "+simbolo);
            i += 1;
            obtemToken();
        }else if(simbolo.equals("(")){
            obtemToken();
            expressao();
            if(simbolo.equals(")")){
                obtemToken();
            }else{
                throw new RuntimeException("Erro sintatico: era esperado ')'");
            }
        }else{
            throw new RuntimeException("Erro sintatico: era esperado um identificador ou"
                    + " um inteiro ou um real ou '('");
        }
    }
    
    private void outros_termos(){
        if(simbolo.equals("+") || simbolo.equals("-")){
            op_ad();
            termo();
            outros_termos();
        }
    }
    
    private void op_ad(){
        switch(simbolo){
            case "+":
                C.push("SOMA");
                i += 1;
                obtemToken();
                break;
            case "-":
                C.push("SUBT");
                i += 1;
                obtemToken();
                break;
            default:
                throw new RuntimeException("Erro sintatico: era esperado '+' ou '-'");
        }
    }
    
    private void mais_fatores(){
        if(simbolo.equals("*") || simbolo.equals("/")){
            op_mul();
            fator();
            mais_fatores();
        }
    }
    
    private void op_mul(){
        switch(simbolo){
            case "*":
                C.push("MULT");
                i += 1;
                obtemToken();
                break;
            case "/":
                C.push("DIV");
                i += 1;
                obtemToken();
                break;
            default:
                throw new RuntimeException("Erro sintatico: era esperado '*' ou '/'");
        }
    }
    
    private void pfalsa(){
        if(simbolo.equals("else")){
            obtemToken();
            comandos();
        }
    }
      
}
