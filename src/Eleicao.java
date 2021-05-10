import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Eleicao implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String nome, DataInicio, DataFim, publicoAlvo;
    int estado, votosBranco, votoNulo;

    public ArrayList<Lista> listas = new ArrayList<Lista>();
    public ArrayList<Pessoa> peopleWhoVoted = new ArrayList<Pessoa>();
    
    public Eleicao(String nome, String DataInicio, String DataFim, String publicoAlvo, int estado,ArrayList<Lista> listas, ArrayList<Pessoa> peopleWhoVoted){
        this.nome = nome;
        this.DataInicio = DataInicio;
        this.DataFim = DataFim;
        this.publicoAlvo = publicoAlvo;
        this.estado = estado;
        this.listas = listas;
        this.peopleWhoVoted = peopleWhoVoted;
    }
    public  Eleicao(){

    }
    public String getNome() {
        return this.nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getDataInicio() {
        return this.DataInicio;
    }
    public void setDataInicio(String DataInicio) {
        this.DataInicio = DataInicio;
    }
    public int getEstado(){
        return this.estado;
    }
    public String getDataFim() {
        return this.DataFim;
    }
    public void setDataFim(String DataFim) {
        this.DataFim = DataFim;
    }
    public String getPublicoAlvo() {
        return this.publicoAlvo;
    }
    public void setPublicoAlvo(String publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }
    public ArrayList<Lista> getListas() {
        return listas;
    }
    public ArrayList<Pessoa> getpeopleWhoVoted(){
        return this.peopleWhoVoted;
    }
    public void setListas(ArrayList<Lista> listas) {
        this.listas = listas;
    }
    @Override
    public String toString() {
        String pessoas = "";
        for(Pessoa p : peopleWhoVoted ){ 
            pessoas += p.nome + " ";
        }
        return  "Eleicao -> " + nome + 
                "\nDataInicio -> " + DataInicio +
                "\nDataFim -> " + DataFim +
                "\nPublicoAlvo ->" + publicoAlvo + 
                "\nEstado -> " + estado + listas +
                "\nVotantes -> " + pessoas +
                "\nVotos Nulos -> " + votoNulo +
                "\nVotos Branco -> " + votosBranco;
    }

    public void createEleicao(ArrayList<Pessoa> pessoa) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String in;
        Scanner scan = new Scanner(System.in);
        int numberOfLists;
        int numCandidate;

        System.out.print("NOME ELEICAO: ");
        in = reader.readLine();
        this.nome = in;

        System.out.print("DATA INICIO (aaaa-mm-dd hh:mm:ss): ");
        in = reader.readLine();
        this.DataInicio = in;

        System.out.print("DATA FIM (aaaa-mm-dd hh:mm:ss): ");
        in = reader.readLine();
        this.DataFim = in;
        
        System.out.println("PUBLICO ALVO: ");
        System.out.println("<1> ESTUDANTE");
        System.out.println("<2> DOCENTE");
        System.out.println("<3> FUNCIONARIO");
        int opt = scan.nextInt();
        if(opt == 1){
            this.publicoAlvo = "ESTUDANTE";
        }
        else if(opt == 2){
            this.publicoAlvo = "DOCENTE";
        }
        else if(opt == 3){
            this.publicoAlvo = "FUNCIONARIO";
        }

        System.out.println("NUMERO DE LISTAS: ");
        numberOfLists = scan.nextInt();

        for(int i = 0; i < numberOfLists; i++){
            Lista l = new Lista();
            System.out.println("LISTA "+(i+1)+":");
            System.out.print("NOME DA LISTA: ");
            in = reader.readLine();
            l.setNomeLista(in);
            System.out.println("CANDIDATO PRINCIPAL: ");
            ArrayList<Pessoa> aux = new ArrayList<Pessoa>();
            for(int k = 0; k < pessoa.size(); k++){
                if(opt == 1 && pessoa.get(k).getTrabalho().toUpperCase().equals("ESTUDANTE")){
                    aux.add(pessoa.get(k));
                }
                else if(opt == 2 && pessoa.get(k).getTrabalho().toUpperCase().equals("DOCENTE")){
                    aux.add(pessoa.get(k));
                }
                else if(opt == 3 && pessoa.get(k).getTrabalho().toUpperCase().equals("FUNCIONARIO")){
                    aux.add(pessoa.get(k));
                }
            }

            for(int k = 0; k<aux.size(); k++){
                System.out.println("<" + k + "> "+ aux.get(k).getNome());
            }

            numCandidate = scan.nextInt();
            l.setCandidatoPrincipal(aux.get(numCandidate));
            while(true) {
                System.out.println("<0> SAIR");
                for (int k = 0; k < aux.size(); k++) {
                    if(!(l.getPessoas().contains(aux.get(k))))
                        System.out.println("<" + (k + 1) + "> " + aux.get(k).getNome());
                }
                numCandidate = scan.nextInt();
                if(numCandidate != 0) {
                    l.getPessoas().add(aux.get(numCandidate - 1));
                }else{
                    break;
                }
            }
            l.setNumVotes(0);
            this.listas.add(l);
        }
        //funcao para verificar se a a eleicao já começou
        this.estado = 0;
        this.votoNulo = 0;
        this.votosBranco = 0;
    }

    public void changeEle() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        Scanner scan = new Scanner(System.in);
        String in;

        System.out.println("SELECIONE A OPCAO QUE PERTENDE ALTERAR");
        System.out.println("<1> ALTERAR O NOME: ");
        System.out.println("<2> MUDAR DATA INICIAL: ");
        System.out.println("<2> MUDAR DATA FINAL: ");
        int opt = scan.nextInt();
        switch (opt){
            case 1:
                System.out.println("INSIRA O NOVO NOME: ");
                in = reader.readLine();
                this.nome = in;
                break;
            case 2:
                System.out.println("INSIRA A DATA: ");
                in = reader.readLine();
                this.DataInicio  = in;
                break;
            case 3:
                System.out.println("INISRA A DATA: ");
                in = reader.readLine();
                this.DataFim  = in;
                break;
        }
    }
}
