import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Lista implements Serializable  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    ArrayList<Pessoa> pessoas = new ArrayList<Pessoa>();
    Pessoa candidatoPrincipal;
    String nomeLista;
    int numVotes;

    public Lista() {
    }

    public Lista(ArrayList<Pessoa> pessoas, Pessoa candidatoPrincipal, String nomeLista) {

        this.pessoas = pessoas;
        this.candidatoPrincipal = candidatoPrincipal;
        this.nomeLista = nomeLista;
    }

    public void setPessoas(ArrayList<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public void setCandidatoPrincipal(Pessoa candidatoPrincipal) {
        this.candidatoPrincipal = candidatoPrincipal;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public ArrayList<Pessoa> getPessoas() {
        return pessoas;
    }

    public Pessoa getCandidatoPrincipal() {
        return candidatoPrincipal;
    }

    public String getNomeLista() {
        return nomeLista;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    @Override
    public String toString() {
        return  "\nListas: " +
                "\nNome da Lista -> " + nomeLista +
                "\nNumero de Votos -> " + numVotes +
                "\nPessoas -> " + pessoas +
                "\nCandidatoPrincipal -> " + candidatoPrincipal.nome ;
    }


    public void manageCandidateList(){
        if(this == null){
            System.out.println("NAO EXISTEM LISTAS DISPONIVEIS !");
        }
        else{
            System.out.println("CANDIDATO PRINCIPAL: ");
            System.out.println(this.getCandidatoPrincipal().nome);
            System.out.println("NUMERO DE VOTOS: ");
            System.out.println(this.getNumVotes());
            System.out.println("OUTRAS PESSOAS DA LISTA: ");
            for(Pessoa p : this.getPessoas()){
                System.out.println(p.getNome());
            }
        }
    }

    public void createList(ArrayList<Pessoa> pessoa) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String in;
        Scanner scan = new Scanner(System.in);

        System.out.print("NOME DA LISTA: ");
        in = reader.readLine();
        this.nomeLista = in;

        System.out.println("CANDIDATO PRINCIPAL: ");
        for(int k = 0; k < pessoa.size(); k++){
            System.out.println(k+" "+pessoa.get(k).getNome());
        }

        int numCandidate = scan.nextInt();
        this.candidatoPrincipal = pessoa.get(numCandidate);

        while(true) {
            for (int k = 0; k < pessoa.size(); k++) {
                if(!(this.getPessoas().contains(pessoa.get(k))))
                    System.out.println((k+1) + " " + pessoa.get(k).getNome());
            }
            numCandidate = scan.nextInt();
            if(numCandidate != 0) {
                this.getPessoas().add(pessoa.get(numCandidate - 1));
            }else{
                break;
            }
        }
    }
    public void modifyList(ArrayList<Pessoa> pessoas, String tipo) throws IOException {
        Scanner scan = new Scanner(System.in);
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        System.out.println("<1> MUDAR NOME: ");
        System.out.println("<2> MUDAR CANDIDATO PRINCIPAL: ");
        System.out.println("<3> REMOVER PESSOAS: ");
        System.out.println("<4> ADICIONAR PESSOAS: ");
        int numOpt = scan.nextInt();
        String in = null;
        ArrayList<Pessoa> aux = new ArrayList<Pessoa>();

        switch (numOpt){
            case 1:
                System.out.println("INTRODUZA O NOME: ");
                in = reader.readLine();
                this.nomeLista = in;
                break;
            case 2:
                System.out.println("CANDIDATO PRINCIPAL: " + this.getCandidatoPrincipal().getNome());
                System.out.println("SELECIONE OUTRO: ");
                for(int i = 0; i< pessoas.size(); i++){
                    if(tipo.toUpperCase().equals(pessoas.get(i).getTrabalho()) && pessoas.get(i) != this.getCandidatoPrincipal()){
                        aux.add(pessoas.get(i));
                    }
                }

                for(int i = 0; i < aux.size(); i ++){
                    System.out.println("<" + i + "> "+ aux.get(i).getNome());
                }

                
                int numCand = scan.nextInt();
                this.candidatoPrincipal = aux.get(numCand);
                break;
            case 3:
                System.out.println("SELECIONE QUEM QUER REMOVER:");
                for(int i = 0; i < this.getPessoas().size(); i++){
                    System.out.println(i + " "+ this.getPessoas().get(i).getNome());
                }
                int canIndex = scan.nextInt();
                this.getPessoas().remove(canIndex);
                break;
            case 4:
                System.out.println("LISTA DE PESSOAS QUE PODE ADICIONAR: ");

                for(int i = 0; i< pessoas.size(); i++){
                    if(tipo.toUpperCase().equals(pessoas.get(i).getTrabalho()) && !this.getPessoas().contains(aux.get(i))){
                        aux.add(pessoas.get(i));
                    }
                }

                for(int i = 0; i < aux.size(); i ++){
                    System.out.println("< " + i + "> "+ aux.get(i).getNome());
                }
                int canIndex1 = scan.nextInt();
                this.getPessoas().add(aux.get(canIndex1));
                break;
        }
    }
}
