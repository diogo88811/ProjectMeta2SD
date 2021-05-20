package rmiserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Pessoa implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String nome, password, trabalho, departamento, telemovel, morada, CCnumber, CCVal, typePerson;
    public ArrayList<String> Tables = new  ArrayList<String>();

    public Pessoa(String nome, String password, String job, String telemovel, String morada, String CCnumber, String CCVal, String departamento, ArrayList<String> Tables,String typePerson){
        this.nome = nome;
        this.password = password;
        this.trabalho = job;
        this.departamento = departamento;
        this.telemovel = telemovel;
        this.morada = morada;
        this.CCnumber = CCnumber;
        this.CCVal = CCVal;
        this.Tables = Tables;
        this.typePerson = typePerson;
    }

    public Pessoa() {
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTrabalho() {
        return this.trabalho;
    }

    public void setTrabalho(String trabalho) {
        this.trabalho = trabalho;
    }

    public String getDepartamento() {
        return this.departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTelemovel() {
        return this.telemovel;
    }

    public void setTelemovel(String telemovel) {
        this.telemovel = telemovel;
    }

    public String getMorada() {
        return this.morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getCCnumber() {
        return this.CCnumber;
    }

    public void setCCnumber(String CCnumber) {
        this.CCnumber = CCnumber;
    }

    public String getCCVal() {
        return this.CCVal;
    }

    public void setCCVal(String CCVal) {
        this.CCVal = CCVal;
    }

    public ArrayList<String> getTables() {
        return this.Tables;
    }

    public void setTables(ArrayList<String> tables) {
        Tables = tables;
    }

    public String getTypePerson() {
        return typePerson;
    }

    public String toString(){//overriding the toString() method
        return  "USERNAME: "       + this.nome         +
                "\nPASSWORD: "     + this.password     +
                "\nJOB: "          + this.trabalho     +
                "\nDEPARTAMENTO: " + this.departamento +
                "\nTELE: "         + this.telemovel    +
                "\nADRESS: "       + this.morada       +
                "\nCCNUMBER: "     + this.CCnumber     +
                "\nCCVAL: "        + this.CCVal        +
                "\nType: "         + this.typePerson    ;
    }

    public void RegisterPerson() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        Scanner scan = new Scanner(System.in);
        String in;
        int num;

        System.out.print("NOME: ");
        in = reader.readLine();
        this.nome = in;

        System.out.print("TELEMOVEL: ");
        in = reader.readLine();
        this.telemovel = in;

        System.out.print("MORADA: ");
        in = reader.readLine();

        this.morada = in;

        System.out.print("NUMERO CC: ");
        in = reader.readLine();

        this.CCnumber = in;

        System.out.print("VALIDADE CC (dd/mm/aaaa): ");
        in = reader.readLine();

        this.CCVal = in;

        System.out.println("CARGO:");
        System.out.println("<1> ESTUDANTE");
        System.out.println("<2> DOCENTE");
        System.out.println("<3> FUNCIONARIO");
        num = scan.nextInt();
        switch(num){
            case 1:
                this.trabalho = "ESTUDANTE";
                break;
            case 2:
                this.trabalho = "DOCENTE";
                break;
            case 3:
                this.trabalho = "FUNCIONARIO";
                break;
        }

        System.out.print("DEPARTAMENTO QUE FREQUENTA: ");
        in = reader.readLine();

        this.departamento = in;

        System.out.print("PASSWORD: ");
        in = reader.readLine();

        this.password = in;

        System.out.println("ADMIN/USER");
        in = reader.readLine();
        this.typePerson = in;

    }
}
